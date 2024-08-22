package application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import session.Aplicativo;
import session.DBConnection;
import session.DBSync;
import session.Departamento;
import session.Funcionario;
import session.Session;
import session.SessionUser;
import session.TipoFuncionario;

public class DepartmentService {
	
	private Aplicativo _appInfo;
	private JButton _add;
	private JButton _remove;
	private JButton _save;
	private JTextField _txtDepCodigo;
	private JTextField _txtDepSigla;
	private JTextField _txtDepDesc;
	private JCheckBox _inactive;
	private JList _depList;
	
	private void AdicionarDepartamento() {
		String query = "INSERT INTO DEPARTAMENTO (DEP_SIGLA, DEP_DESC, DEP_ATIVO) VALUES (?, ?, ?)";
		PreparedStatement s;
		try {
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			String sigla = _txtDepSigla.getText();
			s.setString(1, sigla);
			
			String desc = _txtDepDesc.getText();
			s.setString(2, desc);
			s.setBoolean(3, !_inactive.isSelected());
			
			if (s.executeUpdate() > 0) {
				// Insert worked, create credentials...
				s = DBConnection.GetInstance().Connection.prepareStatement("SELECT DEP_CODIGO FROM DEPARTAMENTO WHERE DEP_SIGLA = ?");
				s.setString(1, sigla);
				ResultSet r = s.executeQuery();
				r.next();
				int id = r.getInt(1);
				
				
				// Insert worked, add to log!
				DBSync.GetInstance().AddToLog(_appInfo, "insert", Session.GetInstance().User.get_func().get_name(), String.format("Codigo = %d, Sigla = %s, Descricao = %s, Ativo = %s",
						id,
						sigla,
						desc,
						(!_inactive.isSelected()) ? "true" : "false"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void RemoverDepartamento() {
		String query = "DELETE FROM DEPARTAMENTO WHERE DEP_CODIGO = ?";
		PreparedStatement s;
		try {
			int id = Integer.parseInt(_txtDepCodigo.getText());
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			s.setInt(1, id);
			
			if (s.executeUpdate() > 0) {
				// Delete worked, add to log!
				Departamento d = DBSync.GetInstance().Departamentos.get(id);
				DBSync.GetInstance().AddToLog(_appInfo, "delete", Session.GetInstance().User.get_func().get_name(), String.format("Codigo = %d, Sigla = %s, Descricao = %s",
						id,
						d.get_sigla(),
						d.get_desc()));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void EditarDepartamento() {
		int id = Integer.parseInt(_txtDepCodigo.getText());
		String desc = _txtDepDesc.getText();
		String sigla = _txtDepSigla.getText();
		
		String query = "UPDATE DEPARTAMENTO SET DEP_DESC = ?, DEP_SIGLA = ?, DEP_ATIVO = ? WHERE DEP_CODIGO = ?";
		PreparedStatement s;
		try {
			s = DBConnection.GetInstance().Connection.prepareStatement(query);
			s.setString(1, desc);
			s.setString(2, sigla);
			s.setBoolean(3, !_inactive.isSelected());
			s.setInt(4, id);
			
			if (s.executeUpdate() > 0) {
				// Update worked, add to log!
				DBSync.GetInstance().AddToLog(_appInfo, "update", Session.GetInstance().User.get_func().get_name(), String.format("Codigo = %d, Sigla = %s, Descricao = %s, Ativo = %s",
						id,
						sigla,
						desc,
						(!_inactive.isSelected()) ? "true" : "false"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void LimparCampos() {
		_txtDepCodigo.setText("");
    	_txtDepDesc.setText("");
    	_txtDepSigla.setText("");
    	_inactive.setSelected(false);
	}
	
	private void AtualizarLista() {
		_depList.setListData(DBSync.GetInstance().Departamentos
				.entrySet()
				.stream()
				.filter(x -> x.getValue().get_id() != 1) // 1 = DCMDB
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).values().toArray());
	}
	
	private void SetModoEdicao() {
		int id = Integer.parseInt(_txtDepCodigo.getText());
		SessionUser self = Session.GetInstance().User;
		boolean isNotMyDep = self.get_dep().get_id() != id;
		boolean isIT = id == 2;

		boolean canEditOtherDepOrAmSU = (isNotMyDep && !isIT) || self.AmISuperUser();
		boolean canEditOtherDepButNotThis = (isNotMyDep || self.AmISuperUser()) && !isIT;
    	_txtDepDesc.setEnabled(canEditOtherDepOrAmSU);
    	_txtDepSigla.setEnabled(canEditOtherDepButNotThis);
		_add.setEnabled(false);
		_remove.setEnabled(canEditOtherDepButNotThis);
		_save.setEnabled(canEditOtherDepOrAmSU);
		_inactive.setEnabled(canEditOtherDepButNotThis);
	}
	
	private void SetModoAdicao() {
		LimparCampos();
    	_txtDepDesc.setEnabled(true);
    	_txtDepSigla.setEnabled(true);
		_add.setEnabled(true);
		_remove.setEnabled(false);
		_save.setEnabled(false);
		_inactive.setEnabled(true);
	}
	
	private void PuxarInformacao() {
		Departamento d = (Departamento) _depList.getSelectedValue();
		_txtDepCodigo.setText(Integer.toString(d.get_id()));
		_txtDepSigla.setText(d.get_sigla());
		_txtDepDesc.setText(d.get_desc());
		_inactive.setSelected(!d.get_active());
	}
	
	public DepartmentService(JList deps, JButton edit, JButton add, JButton remove, JTextField txtDepCodigo, JTextField txtDepSigla, JTextField txtDepDesc, JCheckBox inactive) {
		_depList = deps;
		_save = edit;
		_remove = remove;
		_add = add;
		_txtDepCodigo = txtDepCodigo;
		_txtDepDesc = txtDepDesc;
		_txtDepSigla = txtDepSigla;
		_inactive = inactive;
		_appInfo = DBSync.GetInstance().Apps.get(1); // 1 = departamentos
		
		_save = edit;
		_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_txtDepDesc.getText().isEmpty()  || _txtDepSigla.getText().isEmpty()) {
					return;
				}
				
				EditarDepartamento();
				try {
					DBSync.GetInstance().SyncDepartamentos();
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
				if (_txtDepDesc.getText().isEmpty()  || _txtDepSigla.getText().isEmpty()) {
					return;
				}
				
				AdicionarDepartamento();
				try {
					DBSync.GetInstance().SyncDepartamentos();
					DBSync.GetInstance().SyncFuncionarios();
					DBSync.GetInstance().SyncLogs();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AtualizarLista();
				LimparCampos();
				_depList.setSelectedIndex(0);
			}
		});
		
		_remove = remove;
		_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RemoverDepartamento();
				try {
					DBSync.GetInstance().SyncDepartamentos();
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
		
		_save.setEnabled(false);
		_add.setEnabled(false);
		_remove.setEnabled(false);
		
		_depList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                	int index = _depList.getSelectedIndex();
                	if (index == 0) {
                		SetModoAdicao();
                	}
                	else if (index == -1) {
                		 // Prevent accidental exceptions...
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
	}
}
