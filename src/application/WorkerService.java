package application;

import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.*;

import session.*;

public class WorkerService {
	
	private Aplicativo _appInfo;
	private JButton _add;
	private JButton _remove;
	private JButton _save;
	private JTextField _txtFuncMatricula;
	private JTextField _txtFuncNome;
	private JTextField _txtFuncSenha;
	private JTextField _txtFuncLogin;
	private JComboBox _dep;
	private JComboBox _tipo;
	private JCheckBox _inactive;
	private JList _funcList;
	
	private void AdicionarFuncionario() {
		String query = "INSERT INTO FUNCIONARIO (FUN_NOME, FUN_DEPARTAMENTO, FUN_TIPO, FUN_ATIVO) VALUES (?, ?, ?, ?)";
		PreparedStatement s;
		try {
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			String nome = _txtFuncNome.getText();
			s.setString(1, nome);
			
			Departamento depObj = (Departamento)_dep.getSelectedItem();
			int dep = depObj == null ? 0 : depObj.get_id(); // 0 = PUBLICO
			s.setInt(2, dep);
			
			TipoFuncionario tipoObj = (TipoFuncionario)_tipo.getSelectedItem();
			int tipo = tipoObj == null ? 4 : tipoObj.get_id(); // 4 = FUNCIONÁRIO
			s.setInt(3, tipo);
			s.setBoolean(4, !_inactive.isSelected());
			
			if (s.executeUpdate() > 0) {
				// Insert worked, create credentials...
				s = DBConnection.GetInstance().Connection.prepareStatement("SELECT FUN_MATRICULA FROM FUNCIONARIO WHERE FUN_NOME = ? AND FUN_DEPARTAMENTO = ? AND FUN_TIPO = ? AND FUN_ATIVO = ?");
				s.setString(1, _txtFuncNome.getText());
				s.setInt(2, dep);
				s.setInt(3, tipo);
				s.setBoolean(4, !_inactive.isSelected());
				ResultSet r = s.executeQuery();
				r.next();
				int id = r.getInt("FUN_MATRICULA");
				
				
				query = "INSERT INTO CREDENCIAIS (CRED_LOGIN, CRED_SENHA, CRED_MATRICULA) VALUES (?, ?, ?)";
				s = DBConnection.GetInstance().Connection.prepareStatement(query);
				
				String login = _txtFuncLogin.getText();
				s.setString(1, login);
				
				String senha = _txtFuncSenha.getText();
				s.setString(2, senha);
				s.setInt(3, id);
				if (s.executeUpdate() > 0) {
					// ...and add to log!
					DBSync.GetInstance().AddToLog(_appInfo, "insert", Session.GetInstance().User.get_func().get_name(), String.format("Matricula = %d, Nome = %s, Departamento = %s, Tipo = %s, Ativo = %s, Login = %s, Senha = %s",
							id,
							nome,
							depObj == null ? DBSync.GetInstance().get_publicDepartment() : depObj.get_sigla(),
							tipoObj == null ? DBSync.GetInstance().TipoFuncionario.get(4) : tipoObj.get_name(), // 4 = Funcionário
							(!_inactive.isSelected()) ? "true" : "false",
							login,
							senha));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void RemoverFuncionario() {
		String query = "DELETE FROM FUNCIONARIO WHERE FUN_MATRICULA = ?";
		PreparedStatement s;
		try {
			int id = Integer.parseInt(_txtFuncMatricula.getText());
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			s.setInt(1, id);
			
			if (s.executeUpdate() > 0) {
				// Delete worked, add to log!
				String nome = _txtFuncNome.getText();
				Departamento depObj = (Departamento)_dep.getSelectedItem();
				TipoFuncionario tipoObj = (TipoFuncionario)_tipo.getSelectedItem();
				
				DBSync.GetInstance().AddToLog(_appInfo, "delete", Session.GetInstance().User.get_func().get_name(), String.format("Matricula = %d, Nome = %s, Departamento = %s, Tipo = %s",
						id,
						nome,
						depObj.get_sigla(),
						tipoObj.get_name()));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void EditarFuncionario() {
		int id = Integer.parseInt(_txtFuncMatricula.getText());
		String nome = _txtFuncNome.getText();
		Departamento depObj = (Departamento)_dep.getModel().getSelectedItem();
		if (depObj == null) {
			depObj = DBSync.GetInstance().get_publicDepartment();
		}
		
		TipoFuncionario tipoObj = (TipoFuncionario)_tipo.getModel().getSelectedItem();
		if (tipoObj == null) {
			tipoObj = DBSync.GetInstance().TipoFuncionario.get(4); // 4 = Funcionario
		}
		
		String login = _txtFuncLogin.getText();
		String senha = _txtFuncSenha.getText();
		
		String query = "UPDATE FUNCIONARIO SET FUN_NOME = ?, FUN_DEPARTAMENTO = ?, FUN_TIPO = ?, FUN_ATIVO = ? WHERE FUN_MATRICULA = ?";
		PreparedStatement s;
		try {
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			s.setString(1, nome);
			s.setInt(2, depObj.get_id());
			s.setInt(3, tipoObj.get_id());
			s.setBoolean(4, !_inactive.isSelected());
			s.setInt(5, id);
			
			if (s.executeUpdate() > 0) {
				// Update worked, update credentials...
				query = "SELECT CRED_ID FROM CREDENCIAIS WHERE CRED_MATRICULA = ?";
				s = DBConnection.GetInstance().Connection.prepareStatement(query);
				s.setInt(1, id);
				ResultSet r = s.executeQuery();
				if (r.next()) {
					query = "UPDATE CREDENCIAIS SET CRED_LOGIN = ?, CRED_SENHA = ? WHERE CRED_MATRICULA = ?";
					s = DBConnection.GetInstance().Connection.prepareStatement(query);
					s.setString(1, login);
					s.setString(2, senha);
					s.setInt(3, id);
					
				}
				else {
					query = "INSERT INTO CREDENCIAIS (CRED_LOGIN, CRED_SENHA, CRED_MATRICULA) VALUES (?, ?, ?)";
					s = DBConnection.GetInstance().Connection.prepareStatement(query);
					s.setString(1, login);
					s.setString(2, senha);
					s.setInt(3, id);
				}
				
				s.executeUpdate();
				
				// ...and add to log!
				DBSync.GetInstance().AddToLog(_appInfo, "update", Session.GetInstance().User.get_func().get_name(), String.format("Matricula = %d, Nome = %s, Departamento = %s, Tipo = %s, Ativo = %s, Login = %s, Senha = %s",
						id,
						nome,
						depObj.get_sigla(),
						tipoObj.get_name(),
						(!_inactive.isSelected()) ? "true" : "false",
						login,
						senha));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void LimparCampos() {
		_txtFuncMatricula.setText("");
    	_txtFuncLogin.setText("");
    	_txtFuncNome.setText("");
    	_txtFuncSenha.setText("");
    	_dep.getModel().setSelectedItem(null);
    	_tipo.getModel().setSelectedItem(null);
    	_inactive.setSelected(false);
	}
	
	private void AtualizarLista() {
		_funcList.setListData(DBSync.GetInstance().Funcionarios.values().toArray());
	}
	
	private void SetModoEdicao() {
		SessionUser self = Session.GetInstance().User;
		int selectedId = Integer.parseInt(_txtFuncMatricula.getText());
		boolean selfSelected = self.get_func().get_id() == selectedId;
		boolean selectedUserTypeIsLowerHierarchy = self.get_type().get_id() < ((TipoFuncionario)_tipo.getModel().getSelectedItem()).get_id();

		boolean isLowerOrIAmSuperUser = !selfSelected && selectedId != 1 && (selectedUserTypeIsLowerHierarchy || self.AmISuperUser());
		
		boolean canEdit = selectedUserTypeIsLowerHierarchy || self.AmISuperUser() || selfSelected; 
		
		_add.setEnabled(false);
		_remove.setEnabled(isLowerOrIAmSuperUser);
		_save.setEnabled(canEdit);
		_dep.setEnabled(isLowerOrIAmSuperUser);
		_tipo.setEnabled(isLowerOrIAmSuperUser);
		_inactive.setEnabled(isLowerOrIAmSuperUser);
		
		_txtFuncLogin.setEnabled(canEdit);
		_txtFuncNome.setEnabled(canEdit);
		_txtFuncSenha.setEnabled(canEdit);
	}
	
	private void SetModoAdicao() {
		LimparCampos();
		
		_txtFuncLogin.setEnabled(true);
		_txtFuncNome.setEnabled(true);
		_txtFuncSenha.setEnabled(true);
		
		_add.setEnabled(true);
		_remove.setEnabled(false);
		_save.setEnabled(false);
		_dep.setEnabled(true);
		_tipo.setEnabled(true);
		_inactive.setEnabled(true);
	}
	
	private void PuxarInformacao() {
		Funcionario f = (Funcionario) _funcList.getSelectedValue();
		_txtFuncMatricula.setText(Integer.toString(f.get_id()));
		_txtFuncNome.setText(f.get_name());
		
		String query = "SELECT * FROM CREDENCIAIS WHERE CRED_MATRICULA = ?";
		PreparedStatement s;
		try {
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			s.setInt(1, f.get_id());
			ResultSet r = s.executeQuery();
			if (r.next()) {
				_txtFuncLogin.setText(r.getString("CRED_LOGIN"));
				_txtFuncSenha.setText(r.getString("CRED_SENHA"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DBSync sync = DBSync.GetInstance();
		
		_dep.getModel().setSelectedItem(f.get_dep() <= 0 ? sync.get_publicDepartment() : sync.Departamentos.get(f.get_dep()));
		_inactive.setSelected(!f.get_active());
		_tipo.getModel().setSelectedItem(DBSync.GetInstance().TipoFuncionario.get(f.get_tipo()));
	}
	
	public WorkerService(JList funcs, JButton edit, JButton add, JButton remove, JTextField txtFuncMatricula, JTextField txtFuncNome, JTextField txtFuncSenha, JTextField txtFuncLogin, JComboBox dep, JComboBox tipo, JCheckBox inactive) {
		_txtFuncLogin = txtFuncLogin;
		_txtFuncMatricula = txtFuncMatricula;
		_txtFuncNome = txtFuncNome;
		_txtFuncSenha = txtFuncSenha;
		_funcList = funcs;
		_dep = dep;
		_tipo = tipo;
		_inactive = inactive;
		_appInfo = DBSync.GetInstance().Apps.get(2); // 2 = funcionarios
		
		_save = edit;
		_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_txtFuncNome.getText().isEmpty()  || _txtFuncLogin.getText().isEmpty() || _txtFuncSenha.getText().isEmpty()) {
					return;
				}
				
				EditarFuncionario();
				try {
					DBSync.GetInstance().SyncFuncionarios();
					DBSync.GetInstance().SyncLogs();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AtualizarLista();
			}
		});
		
		_add = add;
		_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_txtFuncNome.getText().isEmpty()  || _txtFuncLogin.getText().isEmpty() || _txtFuncSenha.getText().isEmpty()) {
					return;
				}
				
				AdicionarFuncionario();
				try {
					DBSync.GetInstance().SyncFuncionarios();
					DBSync.GetInstance().SyncLogs();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AtualizarLista();
				LimparCampos();
				_funcList.setSelectedIndex(0);
			}
		});
		
		_remove = remove;
		_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RemoverFuncionario();
				try {
					DBSync.GetInstance().SyncFuncionarios();
					DBSync.GetInstance().SyncLogs();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AtualizarLista();
				LimparCampos();
			}
		});
		
		_txtFuncLogin.setEnabled(false);
		_txtFuncNome.setEnabled(false);
		_txtFuncSenha.setEnabled(false);
		
		_save.setEnabled(false);
		_add.setEnabled(false);
		_remove.setEnabled(false);
		
		_funcList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                	int index = _funcList.getSelectedIndex();
                	if (index == 0) {
                		SetModoAdicao();
                	}
                	else if (index == -1) {
                		 // Prevent accidental exceptions...
                		_txtFuncLogin.setEnabled(false);
                		_txtFuncNome.setEnabled(false);
                		_txtFuncSenha.setEnabled(false);
                		
                		_save.setEnabled(false);
                		_add.setEnabled(false);
                		_remove.setEnabled(false);
                	}
                	else {
                		PuxarInformacao();
                		SetModoEdicao();
                	}
                }
            }
        });
	}
	
	public void OnAppFocus() {
		AtualizarLista();
		LimparCampos();
		_dep.setModel(new DefaultComboBoxModel(DBSync.GetInstance().Departamentos
				.entrySet()
				.stream()
				.filter(x -> x.getValue().get_id() > 1 && x.getValue().get_active()) // 1 = DCMDB
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).values().toArray()));
		_tipo.setModel(new DefaultComboBoxModel(DBSync.GetInstance().TipoFuncionario
				.entrySet()
				.stream()
				.filter(x -> x.getValue().get_id() > Session.GetInstance().User.get_type().get_id())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).values().toArray()));
	}
}
