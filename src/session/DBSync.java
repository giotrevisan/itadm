package session;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.classfile.attribute.StackMapTableAttribute;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

public class DBSync {
	
	private Departamento _publicDepartment;
	private static DBSync _instance;
	public Map<Integer, Departamento> Departamentos;
	public Map<Integer, Funcionario> Funcionarios;
	public Map<Integer, TipoFuncionario> TipoFuncionario;
	public Map<Integer, Permissoes> Permissoes;
	public Map<Integer, Aplicativo> Apps;
	public Map<Integer, Solicitacao> Solics;
	public List<Log> Logs;
	

	private void SyncFixedData() throws SQLException {
		// TipoFuncionario, Permissoes, Apps
		Map<Integer, TipoFuncionario> tipo = new HashMap<Integer, TipoFuncionario>();
		Map<Integer, Permissoes> perms = new HashMap<Integer, Permissoes>();
		Map<Integer, Aplicativo> apps = new HashMap<Integer, Aplicativo>();
		
		String query = "SELECT * FROM ";
		ResultSet r = DBConnection.GetInstance().Connection.prepareStatement(query + "TIPO_FUNCIONARIO").executeQuery();
		while (r.next()) {
			int id = r.getInt("T_ID");
			String nome = r.getString("T_NOME");
			tipo.put(id, new TipoFuncionario(id, nome));
		}
		TipoFuncionario = Collections.unmodifiableMap(tipo);
	

		r = DBConnection.GetInstance().Connection.prepareStatement(query + "PERMISSOES").executeQuery();
		while (r.next()) {
			int id = r.getInt("PERM_ID");
			perms.put(id, new Permissoes(
					id,
					r.getBoolean("PERM_APPDEPARTAMENTO"),
					r.getBoolean("PERM_APPFUNCIONARIOS"),
					r.getBoolean("PERM_APPSOLICITACOES"),
					r.getBoolean("PERM_APPLOGS")));
		}
		Permissoes = Collections.unmodifiableMap(perms);
		
		r = DBConnection.GetInstance().Connection.prepareStatement(query + "APPS").executeQuery();
		while (r.next()) {
			int id = r.getInt("APP_ID");
			apps.put(id, new Aplicativo(id, r.getString("APP_NOME")));
		}
		Apps = Collections.unmodifiableMap(apps);
	}
	
	private DBSync() {
		_publicDepartment = new Departamento(0, "PUBLICO", "Func. sem departamento", true);
		
		try {
			SyncFixedData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void SyncDepartamentos() throws SQLException {
		Map<Integer, Departamento> m = new HashMap<Integer, Departamento>();
		m.put(0, new Departamento(0, LocalDateTime.now().toString(), "+ Adicionar...", true));
		ResultSet r = DBConnection.GetInstance().Connection.prepareStatement("SELECT * FROM DEPARTAMENTO").executeQuery();
		while (r.next()) {
			int id = r.getInt("DEP_CODIGO");
			if (id == 0) {
				continue;
			}
			
			m.put(id, new Departamento(id, r.getString("DEP_SIGLA"), r.getString("DEP_DESC"), r.getBoolean("DEP_ATIVO")));
		}
		Departamentos = Collections.unmodifiableMap(m);
	}
	
	public void SyncFuncionarios() throws SQLException {
		Map<Integer, Funcionario> m = new HashMap<Integer, Funcionario>();
		m.put(0, new Funcionario(0, "+ Adicionar...", 0, 0, true));
		ResultSet r = DBConnection.GetInstance().Connection.prepareStatement("SELECT * FROM FUNCIONARIO").executeQuery();
		while (r.next()) {
			int id = r.getInt("FUN_MATRICULA");
			m.put(id, new Funcionario(id, r.getString("FUN_NOME"), r.getInt("FUN_DEPARTAMENTO"), r.getInt("FUN_TIPO"), r.getBoolean("FUN_ATIVO")));
		}
		Funcionarios = Collections.unmodifiableMap(m);
	}
	
	public void SyncLogs() throws SQLException {
		List<Log> logs = new ArrayList<Log>();
		ResultSet r = DBConnection.GetInstance().Connection.prepareStatement("SELECT * FROM LOGS").executeQuery();
		while (r.next()) {
			logs.add(new Log(r.getInt("LOG_ID"), r.getString("LOG_APP"), r.getString("LOG_TIPO"), r.getString("LOG_USER"), r.getString("LOG_CHANGES")));
		}
		Logs = Collections.unmodifiableList(logs);
	}
	
	public void SyncSolicitacoes() throws SQLException {
		Map<Integer, Solicitacao> m = new HashMap<Integer, Solicitacao>();
		
		// Decidir qual o nível de solicitação adicionar...
		SessionUser self = Session.GetInstance().User;
		boolean isChief = self.get_type().get_id() <= 3; // 3 = Chefe
		boolean isIt = self.get_dep().get_sigla().contentEquals("DTI");
		
		m.put(0, new Solicitacao(0, null, _publicDepartment, null, "+ Adicionar...", "", null, null, "", ""));
		String query = "SELECT * FROM SOLICITACOES" +
				(!(isChief && isIt) && !self.AmISuperUser() ? " WHERE (SOLIC_RESPONSAVEL = ? OR SOLIC_SOLICITANTE = ?) AND SOLIC_ESTADO_FINAL != 'Encerrado'" : "");

		PreparedStatement s = DBConnection.GetInstance().Connection.prepareStatement(query);
		int myId = self.get_func().get_id();
		if (!(isChief && isIt) && !self.AmISuperUser()) {
			s.setInt(1, myId);
			s.setInt(2, myId);
		}
		
		ResultSet r = s.executeQuery();
		while (r.next()) {
			int id = r.getInt("SOLIC_ID");
			int id_func = r.getInt("SOLIC_SOLICITANTE");
			int id_dep = r.getInt("SOLIC_DEPARTAMENTO");
			int id_resp = r.getInt("SOLIC_RESPONSAVEL");
			
			Departamento d = id_dep == 0 ? _publicDepartment : Departamentos.get(id_dep);
			m.put(id, new Solicitacao(id, Funcionarios.get(id_func), d, Funcionarios.get(id_resp), r.getString("SOLIC_ASSUNTO"), r.getString("SOLIC_MSG"), r.getDate("SOLIC_DATAINICIO").toLocalDate(), r.getDate("SOLIC_DATAFIM").toLocalDate(), r.getString("SOLIC_ESTADO_TEMP"), r.getString("SOLIC_ESTADO_FINAL")));
		}
		
		Solics = Collections.unmodifiableMap(m);
	}
	
	public void AddToLog(Aplicativo appInfo, String actionType, String user, String changes) throws SQLException {
		String query = "INSERT INTO LOGS (LOG_APP, LOG_TIPO, LOG_USER, LOG_CHANGES) VALUES (?, ?, ?, ?)";
		PreparedStatement s = DBConnection.GetInstance().Connection.prepareStatement(query);
		s.setString(1, appInfo.get_name());
		s.setString(2, actionType);
		s.setString(3, user);
		s.setString(4, changes);
		s.executeUpdate();
	}
	
	public static DBSync GetInstance() {
		if (_instance == null) {
			_instance = new DBSync();
		}
		
		return _instance;
	}

	public void SyncAll() throws SQLException {
		SessionUser self = Session.GetInstance().User;
		if (self.get_perms().get_appDepartamentos()) {
			SyncDepartamentos();
		}
		
		if (self.get_perms().get_appFuncionarios()) {
			SyncFuncionarios();
		}
		
		if (self.get_perms().get_appSolicitacoes() || self.get_dep().get_sigla().contentEquals("DTI")) {
			SyncSolicitacoes();
		}
		
		if (self.get_perms().get_appLogs()) {
			SyncLogs();
		}
	}
	
	
	public Departamento get_publicDepartment() {
		return _publicDepartment;
	}
}
