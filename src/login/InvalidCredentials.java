package login;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InvalidCredentials extends JDialog {

	private Runnable _clearCreds;
	private static final long serialVersionUID = 1L;
	
	private void Close() {
		_clearCreds.run();
		setVisible(false);
		pack();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
	}

	/**
	 * Create the dialog.
	 */
	public InvalidCredentials(Runnable clearCreds) {
		_clearCreds = clearCreds;
		
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			  @Override
			  public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				  Close();
			  }
		});

		setTitle("Erro");
		setBounds(100, 100, 275, 172);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		super.setLocationRelativeTo(null);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel label = new JLabel("Login ou Senha incorretos. Favor tentar novamente");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label);
		
		JPanel btnPanel = new JPanel();
		panel.add(btnPanel, BorderLayout.SOUTH);
		btnPanel.setLayout(new BorderLayout(0, 0));
		
		JButton btnConfirm = new JButton("Confirmar");
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Close();
			}
		});
		btnPanel.add(btnConfirm);
		
		super.getRootPane().setDefaultButton(btnConfirm);
		
		setMinimumSize(new Dimension(275, 172));
		pack();
	}
}
