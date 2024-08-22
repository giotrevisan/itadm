package login;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import javax.swing.JFrame;

import session.*;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPasswordField;

public class Login {

	private Runnable _postLogin;
	private JDialog _invalid;
	private JFrame _frmLogin;
	private JFrame _parent;
	private JTextField _txtLogin;
	private JPasswordField _txtPassword;

	private void ValidateLogin() {
		if (Session.GetInstance().User == null) {
			// Did not manage to connect!
			_invalid.setVisible(true);
		} else {
			_invalid.setVisible(false);
			_invalid.dispose();
			_invalid = null;
			
			_postLogin.run();
			_parent.setVisible(true);
			_frmLogin.setVisible(false);
		}
	}
	
	private void GetLoginInformation() throws SQLException {
		String query = "SELECT CRED_MATRICULA FROM CREDENCIAIS WHERE CRED_LOGIN = ? AND CRED_SENHA = ?";
		
		PreparedStatement stmt = DBConnection.GetInstance().Connection.prepareStatement(query);
		stmt.setString(1, _txtLogin.getText());
		stmt.setString(2, new String(_txtPassword.getPassword()));
		
		// Query will always return a single value so no need to encapsulate within a while loop...
		ResultSet r = stmt.executeQuery();
		if (r.next()) {
			// Has a result!
			int id = r.getInt(1);
			
			Funcionario f = DBSync.GetInstance().Funcionarios.get(id);
			Session.GetInstance().CreateSessionUserFromWorker(f);

			if (f.get_active()) {
				// Sync solic
				DBSync.GetInstance().SyncSolicitacoes();				
			}
		}
		else {
			// No result, which means the credentials are incorrect!
			Session.GetInstance().User = null;
		}
	}

	/**
	 * Create the application.
	 */
	public Login(JFrame parent, Runnable postLogin) {
		_postLogin = postLogin;
		
		try {
			initialize();
			DBSync sync = DBSync.GetInstance();
			sync.SyncDepartamentos();
			sync.SyncFuncionarios();
			sync.SyncLogs();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_parent = parent;
		_invalid = new login.InvalidCredentials(() -> ClearCredentials());

		_frmLogin.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				System.exit(0);
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	private void initialize() throws SQLException {
		_frmLogin = new JFrame();
		_frmLogin.setTitle("Login");
		_frmLogin.setBounds(100, 100, 450, 300);
		_frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frmLogin.setLocationRelativeTo(null);
		
		JLabel lblUser = new JLabel("Usuário");
		JLabel lblPassword = new JLabel("Senha");
		
		_txtLogin = new JTextField();
		_txtLogin.setColumns(10);
		
		Login self = this;
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GetLoginInformation();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				ValidateLogin();
			}
		});
		
		_frmLogin.getRootPane().setDefaultButton(btnLogin);
		
		_txtPassword = new JPasswordField();
		
		GroupLayout groupLayout = new GroupLayout(_frmLogin.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(131)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnLogin, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblUser)
								.addComponent(lblPassword))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(_txtLogin, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
								.addComponent(_txtPassword))))
					.addGap(130))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(95)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(_txtLogin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblUser))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(_txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPassword))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLogin)
					.addContainerGap(94, Short.MAX_VALUE))
		);
		_frmLogin.getContentPane().setLayout(groupLayout);
	}

	public void ClearCredentials() {
		_txtLogin.setText("");
		_txtPassword.setText("");
		
		_txtLogin.grabFocus();
	}

	public void setVisible(Boolean b) {
		_frmLogin.setVisible(b);
	}
}
