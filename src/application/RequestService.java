package application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.CharsetEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import session.DBConnection;
import session.Aplicativo;
import session.DBSync;
import session.Departamento;
import session.Funcionario;
import session.Session;
import session.SessionUser;
import session.Solicitacao;
import session.TipoFuncionario;

public class RequestService {
	
	private Aplicativo _appInfo;
	private JTextField _txtSolicSolicitante;
	private JTextField _txtSolicDepartamento;
	private JTextField _txtSolicAssunto;
	private JTextField _txtSolicDataInicio;
	private JTextField _txtSolicDataFim;
	private JComboBox _comboSolicResponsavel;
	private JComboBox _comboSolicEstadoTemp;
	private JComboBox _comboSolicEstadoFinal;
	private JEditorPane _paneSolicMsg;
	private JButton _btnSolicSave;
	private JButton _btnSolicAdd;
	private JList _solicList;
	
	private boolean MovimentarSolicitacao() {
//		SOLIC_SOLICITANTE INT NOT NULL,
//		SOLIC_DEPARTAMENTO INT,
//		SOLIC_RESPONSAVEL INT NOT NULL,
//		SOLIC_ASSUNTO VARCHAR NOT NULL,
//		SOLIC_MSG VARCHAR,
//		SOLIC_DATAINICIO DATE NOT NULL,
//		SOLIC_DATAFIM DATE,
//		SOLIC_ESTADO_TEMP VARCHAR NOT NULL,
//		SOLIC_ESTADO_FINAL VARCHAR NOT NULL,
//		
//		
		String query = "UPDATE SOLICITACOES SET SOLIC_RESPONSAVEL = ?, SOLIC_MSG = ?, SOLIC_DATAFIM = ?, SOLIC_ESTADO_TEMP = ?, SOLIC_ESTADO_FINAL = ? WHERE SOLIC_ID = ?";
		PreparedStatement s;
		try {
			SessionUser self = Session.GetInstance().User;
			Departamento depObj = self.get_dep();
			Funcionario funcObj = self.get_func();
			Funcionario respObj = (Funcionario)_comboSolicResponsavel.getModel().getSelectedItem();
			String assunto = _txtSolicAssunto.getText();
			String msg = _paneSolicMsg.getText();
			LocalDate inicio = LocalDate.parse(_txtSolicDataInicio.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
			LocalDate prazo = LocalDate.parse(_txtSolicDataFim.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
			String temp = (String) _comboSolicEstadoTemp.getModel().getSelectedItem();
			String fim = (String) _comboSolicEstadoFinal.getModel().getSelectedItem();
			
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			s.setInt(1, respObj.get_id());
			s.setString(2, msg);
			s.setDate(3, java.sql.Date.valueOf(prazo));
			s.setString(4, temp);
			s.setString(5, fim);
			s.setInt(6, ((Solicitacao)_solicList.getSelectedValue()).get_id());
			if (s.executeUpdate() > 0) {
				// Update worked, add to log!
				DBSync.GetInstance().AddToLog(_appInfo, "update", funcObj.get_name(), String.format("Departamento = %s, Resp. = %s, Assunto = %s, Mensagem = %s, Inicio = %s, Prazo = %s, Exec. = %s, Final = %s",
						depObj.get_sigla(),
						respObj.toString(),
						assunto,
						msg,
						inicio,
						prazo,
						temp,
						fim));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return ((String) _comboSolicEstadoFinal.getModel().getSelectedItem()).contentEquals("Encerrado");
		
	}
	
	private void AdicionarSolicitacao() {
		String query = "INSERT INTO SOLICITACOES (SOLIC_SOLICITANTE, SOLIC_DEPARTAMENTO, SOLIC_RESPONSAVEL, SOLIC_ASSUNTO, SOLIC_MSG, SOLIC_DATAINICIO, SOLIC_DATAFIM, SOLIC_ESTADO_TEMP, SOLIC_ESTADO_FINAL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement s;
		try {
			SessionUser self = Session.GetInstance().User;
			Departamento depObj = self.get_dep();
			Funcionario funcObj = self.get_func();
			Funcionario respObj = (Funcionario)_comboSolicResponsavel.getModel().getSelectedItem();
			String assunto = _txtSolicAssunto.getText();
			String msg = _paneSolicMsg.getText();
			LocalDate inicio = LocalDate.parse(_txtSolicDataInicio.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
			LocalDate prazo = LocalDate.parse(_txtSolicDataFim.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
			String temp = (String) _comboSolicEstadoTemp.getModel().getSelectedItem();
			String fim = (String) _comboSolicEstadoFinal.getModel().getSelectedItem();
			
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			s.setInt(1, funcObj.get_id());
			s.setInt(2, depObj.get_id());
			s.setInt(3, respObj.get_id());
			s.setString(4, assunto);
			s.setString(5, msg);
			s.setDate(6, java.sql.Date.valueOf(inicio));
			s.setDate(7, java.sql.Date.valueOf(prazo));
			s.setString(8, temp);
			s.setString(9, fim);
			if (s.executeUpdate() > 0) {
				// Insert worked, add to log!
				DBSync.GetInstance().AddToLog(_appInfo, "insert", funcObj.get_name(), String.format("Departamento = %s, Resp. = %s, Assunto = %s, Mensagem = %s, Inicio = %s, Prazo = %s, Exec. = %s, Final = %s",
						depObj.get_sigla(),
						respObj.toString(),
						assunto,
						msg,
						inicio,
						prazo,
						temp,
						fim));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void AtualizarLista() {
		_solicList.setListData(DBSync.GetInstance().Solics.values().toArray());
	}
	
	private void PuxarInformacao() {
		Solicitacao s = (Solicitacao) _solicList.getSelectedValue();
		
		_txtSolicSolicitante.setText(s.get_solicitante().get_name());
		_txtSolicDepartamento.setText(s.get_dep().get_desc());
		_txtSolicAssunto.setText(s.get_assunto());
		_txtSolicDataInicio.setText(s.get_inicio().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")));
		_txtSolicDataFim.setText(s.get_fim().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")));
		_comboSolicResponsavel.getModel().setSelectedItem(s.get_responsavel());
		_comboSolicEstadoTemp.getModel().setSelectedItem(s.get_temp());
		_comboSolicEstadoFinal.getModel().setSelectedItem(s.get_final());
		_paneSolicMsg.setText(s.get_mensagem());
	}
	
	private void DisableAll() {
		// Prevent accidental exceptions...
		_txtSolicAssunto.setEnabled(false);
		_txtSolicDataFim.setEnabled(false);
		_comboSolicResponsavel.setEnabled(false);
		_comboSolicEstadoTemp.setEnabled(false);
		_comboSolicEstadoFinal.setEnabled(false);
		_paneSolicMsg.setEnabled(false);
		
		_btnSolicAdd.setEnabled(false);
		_btnSolicSave.setEnabled(false);
	}
	
	private void LimparCampos() {
		_txtSolicSolicitante.setText("");
		_txtSolicDepartamento.setText("");
		_txtSolicAssunto.setText("");
		_txtSolicDataInicio.setText("");
		_txtSolicDataFim.setText("");
		_paneSolicMsg.setText("");
		_comboSolicResponsavel.getModel().setSelectedItem("");
		_comboSolicEstadoTemp.getModel().setSelectedItem("");
		_comboSolicEstadoFinal.getModel().setSelectedItem("");
	}
	
	private void SetModoAdicao() {
		LimparCampos();
		
		SessionUser self = Session.GetInstance().User;
		_txtSolicSolicitante.setText(self.get_func().get_name());
		_txtSolicDepartamento.setText(self.get_dep().get_desc());
		
		_txtSolicAssunto.setEnabled(true);
		_txtSolicDataInicio.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")));
		_txtSolicDataFim.setEnabled(true);
		_comboSolicResponsavel.setEnabled(true);
		_comboSolicEstadoTemp.setEnabled(false);
		_comboSolicEstadoTemp.getModel().setSelectedItem("Pendente");
		_comboSolicEstadoFinal.setEnabled(false);
		_comboSolicEstadoFinal.getModel().setSelectedItem("Pendente");
		_paneSolicMsg.setEnabled(true);
		
		_btnSolicAdd.setEnabled(true);
		_btnSolicSave.setEnabled(false);
	}
	
	private void SetModoEdicao() {
		SessionUser self = Session.GetInstance().User;
		boolean isChief = self.get_type().get_id() <= 3;
		boolean isSolicitorOrSu = self.AmISuperUser() || (self.get_func().get_name().contentEquals(_txtSolicSolicitante.getText()));
		boolean isIt = self.get_dep().get_sigla().contentEquals("DTI");
		boolean isAssigned = self.get_func().get_name().contentEquals(((Funcionario)_comboSolicResponsavel.getModel().getSelectedItem()).get_name());

		_txtSolicAssunto.setEnabled(self.AmISuperUser());
		_txtSolicDataFim.setEnabled(isSolicitorOrSu);
		_comboSolicResponsavel.setEnabled(self.AmISuperUser() || (isIt && isChief));
		_comboSolicEstadoTemp.setEnabled(self.AmISuperUser() || (isIt && isAssigned));
		_comboSolicEstadoFinal.setEnabled(isSolicitorOrSu);
		_paneSolicMsg.setEnabled(isSolicitorOrSu);
		_btnSolicSave.setEnabled(isSolicitorOrSu || (isIt && (isAssigned || isChief)));
		_btnSolicAdd.setEnabled(false);
	}
	
	public RequestService(JList solicList, JTextField txtSolicSolicitante ,JTextField txtSolicDepartamento, JTextField txtSolicAssunto, JTextField txtSolicDataInicio, JTextField txtSolicDataFim, JComboBox comboSolicResponsavel, JComboBox comboSolicEstadoTemp, JComboBox comboSolicEstadoFinal, JEditorPane paneSolicMsg ,JButton btnSolicSave ,JButton btnSolicAdd) {
		_solicList = solicList;
		_txtSolicSolicitante = txtSolicSolicitante;
		_txtSolicDepartamento = txtSolicDepartamento;
		_txtSolicAssunto = txtSolicAssunto;
		_txtSolicDataInicio = txtSolicDataInicio;
		_txtSolicDataFim = txtSolicDataFim;
		_comboSolicResponsavel = comboSolicResponsavel;
		_comboSolicEstadoTemp = comboSolicEstadoTemp;
		_comboSolicEstadoFinal = comboSolicEstadoFinal;
		_paneSolicMsg = paneSolicMsg;
		_appInfo = DBSync.GetInstance().Apps.get(3); // 3 = Solicitacoes
		
		_btnSolicSave = btnSolicSave;
		_btnSolicSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					LocalDate d = LocalDate.parse(_txtSolicDataFim.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
					if (d.isBefore(LocalDate.now())) {
						return;
					}
				}
				catch (DateTimeParseException e1) {
					return;
				}
				
				if (_txtSolicAssunto.getText().isEmpty() || _txtSolicDataFim.getText().isEmpty() || _paneSolicMsg.getText().isEmpty()) {
					return;
				}
				
				boolean encerrado = MovimentarSolicitacao();
				try {
					DBSync.GetInstance().SyncSolicitacoes();
					DBSync.GetInstance().SyncLogs();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AtualizarLista();
				
				if (encerrado) {
					LimparCampos();
				}
			}
		});
		
		
		_btnSolicAdd = btnSolicAdd;
		_btnSolicAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					LocalDate d = LocalDate.parse(_txtSolicDataFim.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
					if (d.isBefore(LocalDate.now())) {
						return;
					}
				}
				catch (DateTimeParseException e1) {
					return;
				}
				
				if (_txtSolicAssunto.getText().isEmpty() || _txtSolicDataFim.getText().isEmpty() || _paneSolicMsg.getText().isEmpty()) {
					return;
				}
				
				AdicionarSolicitacao();
				try {
					DBSync.GetInstance().SyncSolicitacoes();
					DBSync.GetInstance().SyncLogs();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AtualizarLista();
				LimparCampos();
				_solicList.setSelectedIndex(0);
			}
		});
		
		_solicList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                	Solicitacao s = (Solicitacao) _solicList.getSelectedValue();
                	if (_solicList.getSelectedIndex() == -1) {
                		DisableAll();
                	}
                	else {
                		int id = s.get_id();
                    	if (id == 0) {
                    		SetModoAdicao();
                    	}
                    	else {
                    		PuxarInformacao();
                    		SetModoEdicao();
                    	}
                	}
                }
            }
        });
	}
	
	public void OnAppFocus() {
		LimparCampos();
		AtualizarLista();
		DisableAll();
		_comboSolicResponsavel.setModel(new DefaultComboBoxModel(DBSync.GetInstance().Funcionarios
				.entrySet()
				.stream()
				.filter(x -> DBSync.GetInstance().Departamentos.get(x.getValue().get_dep()).get_sigla().contentEquals("DTI") && x.getValue().get_active())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).values().toArray()));
	}

}
