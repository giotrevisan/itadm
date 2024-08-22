package application;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;

import login.Login;
import session.DBSync;
import session.Departamento;
import session.Funcionario;
import session.Session;
import session.SessionUser;
import javax.swing.JList;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.AbstractListModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import java.awt.event.FocusAdapter;

public class MainInterface {

	private final String _depTabName = "Departamentos";
	private final String _funcsTabName = "Funcionários";
	private final String _solicTabName = "Solicitações";
	private final String _logsTabName = "Logs";
	
	private JFrame frmAdministracaoTi;
	private Login _l;
	private JTabbedPane _tabbedPane;
	
	private JTextField txtFuncMatricula;
	private JTextField txtFuncNome;
	private JTextField txtFuncSenha;
	private JTextField txtFuncLogin;
	private JLabel _lblName;
	private JLabel _lblCargo;

	private JTextField txtDepId;
	private JTextField txtDepAcronym;
	private JTextField txtDepDesc;
	private JTable logTable;
	
	// Services
	private WorkerService _worker;
	private LogService _log;
	private RequestService _request;
	private DepartmentService _deps;
	
	// Panels
	private JPanel _funcPanel;
	private JPanel _logPanel;
	private JPanel _solicPanel;
	private JPanel _depPanel;
	private JTextField txtSolicAssunto;
	private JTextField txtSolicSolicitante;
	private JTextField txtSolicInicio;
	private JTextField txtSolicDep;
	
	private void AdicionarDepartamentos() {		
		// Departamentos
		//------------------------------------------------------------------------------------------------------------------------
		_depPanel = new JPanel();
		_tabbedPane.addTab(_depTabName, null, _depPanel, null);
		SpringLayout sl__depPanel = new SpringLayout();
		_depPanel.setLayout(sl__depPanel);
		
		JPanel depListPanel = new JPanel();
		sl__depPanel.putConstraint(SpringLayout.WEST, depListPanel, 10, SpringLayout.WEST, _depPanel);
		depListPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		_depPanel.add(depListPanel);
		depListPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel depInfoPanel = new JPanel();
		sl__depPanel.putConstraint(SpringLayout.NORTH, depListPanel, 0, SpringLayout.NORTH, depInfoPanel);
		sl__depPanel.putConstraint(SpringLayout.SOUTH, depListPanel, 0, SpringLayout.SOUTH, depInfoPanel);
		sl__depPanel.putConstraint(SpringLayout.EAST, depListPanel, -6, SpringLayout.WEST, depInfoPanel);
		
		JList depList = new JList();
		depList.setModel(new AbstractListModel() {
			String[] values = new String[] {"+ Adicionar..."};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		depListPanel.add(depList, BorderLayout.CENTER);
		sl__depPanel.putConstraint(SpringLayout.NORTH, depInfoPanel, 10, SpringLayout.NORTH, _depPanel);
		sl__depPanel.putConstraint(SpringLayout.WEST, depInfoPanel, 251, SpringLayout.WEST, _depPanel);
		sl__depPanel.putConstraint(SpringLayout.SOUTH, depInfoPanel, -10, SpringLayout.SOUTH, _depPanel);
		sl__depPanel.putConstraint(SpringLayout.EAST, depInfoPanel, -10, SpringLayout.EAST, _depPanel);
		depInfoPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		_depPanel.add(depInfoPanel);
		SpringLayout sl_depInfoPanel = new SpringLayout();
		depInfoPanel.setLayout(sl_depInfoPanel);
		
		JLabel lblDepDesc = new JLabel("Descrição");
		sl_depInfoPanel.putConstraint(SpringLayout.WEST, lblDepDesc, 10, SpringLayout.WEST, depInfoPanel);
		depInfoPanel.add(lblDepDesc);
		
		JLabel lblDepId = new JLabel("Código");
		sl_depInfoPanel.putConstraint(SpringLayout.NORTH, lblDepId, 13, SpringLayout.NORTH, depInfoPanel);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, lblDepId, 0, SpringLayout.EAST, lblDepDesc);
		depInfoPanel.add(lblDepId);
		
		txtDepId = new JTextField();
		txtDepId.setEditable(false);
		sl_depInfoPanel.putConstraint(SpringLayout.NORTH, txtDepId, -3, SpringLayout.NORTH, lblDepId);
		sl_depInfoPanel.putConstraint(SpringLayout.WEST, txtDepId, 6, SpringLayout.EAST, lblDepId);
		depInfoPanel.add(txtDepId);
		txtDepId.setColumns(10);
		
		txtDepAcronym = new JTextField();
		sl_depInfoPanel.putConstraint(SpringLayout.NORTH, txtDepAcronym, 6, SpringLayout.SOUTH, txtDepId);
		sl_depInfoPanel.putConstraint(SpringLayout.WEST, txtDepAcronym, 0, SpringLayout.WEST, txtDepId);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, txtDepAcronym, 0, SpringLayout.EAST, txtDepId);
		depInfoPanel.add(txtDepAcronym);
		txtDepAcronym.setColumns(10);
		
		txtDepDesc = new JTextField();
		sl_depInfoPanel.putConstraint(SpringLayout.NORTH, txtDepDesc, 6, SpringLayout.SOUTH, txtDepAcronym);
		sl_depInfoPanel.putConstraint(SpringLayout.WEST, txtDepDesc, 0, SpringLayout.WEST, txtDepAcronym);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, txtDepDesc, -10, SpringLayout.EAST, depInfoPanel);
		depInfoPanel.add(txtDepDesc);
		txtDepDesc.setColumns(10);
		
		sl_depInfoPanel.putConstraint(SpringLayout.NORTH, lblDepDesc, 3, SpringLayout.NORTH, txtDepDesc);
		
		JLabel lblDepAcronym = new JLabel("Sigla");
		sl_depInfoPanel.putConstraint(SpringLayout.NORTH, lblDepAcronym, 3, SpringLayout.NORTH, txtDepAcronym);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, lblDepAcronym, 0, SpringLayout.EAST, lblDepDesc);
		depInfoPanel.add(lblDepAcronym);
		
		JCheckBox chckbxDepInactive = new JCheckBox("Inativo");
		sl_depInfoPanel.putConstraint(SpringLayout.NORTH, chckbxDepInactive, 6, SpringLayout.SOUTH, txtDepDesc);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, chckbxDepInactive, 0, SpringLayout.EAST, txtDepDesc);
		depInfoPanel.add(chckbxDepInactive);
		
		JButton btnDepSave = new JButton("Salvar");
		sl_depInfoPanel.putConstraint(SpringLayout.SOUTH, btnDepSave, -10, SpringLayout.SOUTH, depInfoPanel);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, btnDepSave, -10, SpringLayout.EAST, depInfoPanel);
		depInfoPanel.add(btnDepSave);
		
		JButton btnDepRemove = new JButton("Remover");
		sl_depInfoPanel.putConstraint(SpringLayout.SOUTH, btnDepRemove, 0, SpringLayout.SOUTH, btnDepSave);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, btnDepRemove, -6, SpringLayout.WEST, btnDepSave);
		depInfoPanel.add(btnDepRemove);
		
		JButton btnDepAdd = new JButton("Adicionar");
		sl_depInfoPanel.putConstraint(SpringLayout.SOUTH, btnDepAdd, 0, SpringLayout.SOUTH, btnDepSave);
		sl_depInfoPanel.putConstraint(SpringLayout.EAST, btnDepAdd, -6, SpringLayout.WEST, btnDepRemove);
		depInfoPanel.add(btnDepAdd);
		
		_deps = new DepartmentService(depList, btnDepSave, btnDepAdd, btnDepRemove, txtDepId, txtDepAcronym, txtDepDesc, chckbxDepInactive);
	}
	
	private void AdicionarFuncionarios() {
		// Funcionários
		//------------------------------------------------------------------------------------------------------------------------
		_funcPanel = new JPanel();
		SpringLayout sl_funcPanel = new SpringLayout();
		_funcPanel.setLayout(sl_funcPanel);
		
		JPanel funcListPanel = new JPanel();
		sl_funcPanel.putConstraint(SpringLayout.NORTH, funcListPanel, 10, SpringLayout.NORTH, _funcPanel);
		sl_funcPanel.putConstraint(SpringLayout.WEST, funcListPanel, 10, SpringLayout.WEST, _funcPanel);
		sl_funcPanel.putConstraint(SpringLayout.SOUTH, funcListPanel, -10, SpringLayout.SOUTH, _funcPanel);
		funcListPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		_funcPanel.add(funcListPanel);
		funcListPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel funcInfoPanel = new JPanel();
		funcInfoPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sl_funcPanel.putConstraint(SpringLayout.NORTH, funcInfoPanel, 10, SpringLayout.NORTH, _funcPanel);
		sl_funcPanel.putConstraint(SpringLayout.WEST, funcInfoPanel, 251, SpringLayout.WEST, _funcPanel);
		sl_funcPanel.putConstraint(SpringLayout.SOUTH, funcInfoPanel, -10, SpringLayout.SOUTH, _funcPanel);
		sl_funcPanel.putConstraint(SpringLayout.EAST, funcInfoPanel, -10, SpringLayout.EAST, _funcPanel);
		sl_funcPanel.putConstraint(SpringLayout.EAST, funcListPanel, -6, SpringLayout.WEST, funcInfoPanel);
		
		JList funcList = new JList();
		funcList.setModel(new DefaultListModel() {
			String[] values = new String[] {"+ Adicionar..."};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		funcListPanel.add(funcList, BorderLayout.CENTER);
		_funcPanel.add(funcInfoPanel);
		SpringLayout sl_funcInfoPanel = new SpringLayout();
		funcInfoPanel.setLayout(sl_funcInfoPanel);
		
		JLabel lblFuncMatricula = new JLabel("Matrícula");
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, lblFuncMatricula, 10, SpringLayout.NORTH, funcInfoPanel);
		sl_funcInfoPanel.putConstraint(SpringLayout.WEST, lblFuncMatricula, 10, SpringLayout.WEST, funcInfoPanel);
		funcInfoPanel.add(lblFuncMatricula);
		
		txtFuncMatricula = new JTextField();
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, txtFuncMatricula, -3, SpringLayout.NORTH, lblFuncMatricula);
		sl_funcInfoPanel.putConstraint(SpringLayout.WEST, txtFuncMatricula, 6, SpringLayout.EAST, lblFuncMatricula);
		txtFuncMatricula.setEditable(false);
		funcInfoPanel.add(txtFuncMatricula);
		txtFuncMatricula.setColumns(10);
		
		JCheckBox chckbxFuncInactive = new JCheckBox("Inativo");
		funcInfoPanel.add(chckbxFuncInactive);
		
		JLabel lblFuncName = new JLabel("Nome");
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, lblFuncName, 0, SpringLayout.EAST, lblFuncMatricula);
		funcInfoPanel.add(lblFuncName);
		
		JComboBox comboFuncDep = new JComboBox();
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, chckbxFuncInactive, 0, SpringLayout.EAST, comboFuncDep);
		sl_funcInfoPanel.putConstraint(SpringLayout.WEST, comboFuncDep, 0, SpringLayout.WEST, txtFuncMatricula);
		funcInfoPanel.add(comboFuncDep);
		
		txtFuncNome = new JTextField();
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, lblFuncName, 3, SpringLayout.NORTH, txtFuncNome);
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, comboFuncDep, 6, SpringLayout.SOUTH, txtFuncNome);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, comboFuncDep, 0, SpringLayout.EAST, txtFuncNome);
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, txtFuncNome, 6, SpringLayout.SOUTH, txtFuncMatricula);
		sl_funcInfoPanel.putConstraint(SpringLayout.WEST, txtFuncNome, 0, SpringLayout.WEST, txtFuncMatricula);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, txtFuncNome, -10, SpringLayout.EAST, funcInfoPanel);
		funcInfoPanel.add(txtFuncNome);
		txtFuncNome.setColumns(10);
		
		JLabel lblFuncDep = new JLabel("Depto.");
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, lblFuncDep, 4, SpringLayout.NORTH, comboFuncDep);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, lblFuncDep, 0, SpringLayout.EAST, lblFuncMatricula);
		funcInfoPanel.add(lblFuncDep);
		
		JButton btnFuncSave = new JButton("Salvar");
		
		sl_funcInfoPanel.putConstraint(SpringLayout.SOUTH, btnFuncSave, -10, SpringLayout.SOUTH, funcInfoPanel);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, btnFuncSave, -10, SpringLayout.EAST, funcInfoPanel);
		funcInfoPanel.add(btnFuncSave);
		
		JButton btnFuncRemove = new JButton("Remover");
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, btnFuncRemove, 0, SpringLayout.NORTH, btnFuncSave);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, btnFuncRemove, -6, SpringLayout.WEST, btnFuncSave);
		funcInfoPanel.add(btnFuncRemove);
		
		JButton btnFuncAdd = new JButton("Adicionar");
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, btnFuncAdd, 0, SpringLayout.NORTH, btnFuncSave);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, btnFuncAdd, -6, SpringLayout.WEST, btnFuncRemove);
		funcInfoPanel.add(btnFuncAdd);
		
		txtFuncSenha = new JTextField();
		sl_funcInfoPanel.putConstraint(SpringLayout.WEST, txtFuncSenha, 0, SpringLayout.WEST, txtFuncMatricula);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, txtFuncSenha, 0, SpringLayout.EAST, comboFuncDep);
		txtFuncSenha.setColumns(10);
		funcInfoPanel.add(txtFuncSenha);
		
		JLabel lblFuncLogin = new JLabel("Login");
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, lblFuncLogin, 0, SpringLayout.EAST, lblFuncMatricula);
		funcInfoPanel.add(lblFuncLogin);
		
		JLabel lblFuncPwd = new JLabel("Senha");
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, lblFuncPwd, 3, SpringLayout.NORTH, txtFuncSenha);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, lblFuncPwd, 0, SpringLayout.EAST, lblFuncMatricula);
		funcInfoPanel.add(lblFuncPwd);
		
		txtFuncLogin = new JTextField();
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, lblFuncLogin, 3, SpringLayout.NORTH, txtFuncLogin);
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, txtFuncSenha, 6, SpringLayout.SOUTH, txtFuncLogin);
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, txtFuncLogin, 6, SpringLayout.SOUTH, comboFuncDep);
		sl_funcInfoPanel.putConstraint(SpringLayout.WEST, txtFuncLogin, 0, SpringLayout.WEST, txtFuncMatricula);
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, txtFuncLogin, 0, SpringLayout.EAST, comboFuncDep);
		funcInfoPanel.add(txtFuncLogin);
		txtFuncLogin.setColumns(10);
		
		JComboBox comboFuncType = new JComboBox();
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, comboFuncType, -10, SpringLayout.EAST, funcInfoPanel);
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, chckbxFuncInactive, 6, SpringLayout.SOUTH, comboFuncType);
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, comboFuncType, 6, SpringLayout.SOUTH, txtFuncSenha);
		funcInfoPanel.add(comboFuncType);
		
		JLabel lblFuncType = new JLabel("Tipo");
		sl_funcInfoPanel.putConstraint(SpringLayout.EAST, lblFuncType, -102, SpringLayout.EAST, funcInfoPanel);
		sl_funcInfoPanel.putConstraint(SpringLayout.WEST, comboFuncType, 6, SpringLayout.EAST, lblFuncType);
		sl_funcInfoPanel.putConstraint(SpringLayout.NORTH, lblFuncType, 4, SpringLayout.NORTH, comboFuncType);
		funcInfoPanel.add(lblFuncType);
		
		_tabbedPane.addTab(_funcsTabName, null, _funcPanel, null);
		
		_worker = new WorkerService(funcList, btnFuncSave, btnFuncAdd, btnFuncRemove, txtFuncMatricula, txtFuncNome, txtFuncSenha, txtFuncLogin, comboFuncDep, comboFuncType, chckbxFuncInactive);
	}
	
	private void AdicionarSolicitacoes() throws ParseException {
		// Solicitações
		//------------------------------------------------------------------------------------------------------------------------
		_solicPanel = new JPanel();
		_tabbedPane.addTab(_solicTabName, null, _solicPanel, null);
		SpringLayout sl__solicPanel = new SpringLayout();
		_solicPanel.setLayout(sl__solicPanel);
		
		JPanel solicListPanel = new JPanel();
		solicListPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		sl__solicPanel.putConstraint(SpringLayout.WEST, solicListPanel, 10, SpringLayout.WEST, _solicPanel);
		_solicPanel.add(solicListPanel);
		
		JPanel solicInfoPanel = new JPanel();
		sl__solicPanel.putConstraint(SpringLayout.NORTH, solicListPanel, 0, SpringLayout.NORTH, solicInfoPanel);
		sl__solicPanel.putConstraint(SpringLayout.SOUTH, solicListPanel, 0, SpringLayout.SOUTH, solicInfoPanel);
		sl__solicPanel.putConstraint(SpringLayout.EAST, solicListPanel, -6, SpringLayout.WEST, solicInfoPanel);
		solicListPanel.setLayout(new BorderLayout(0, 0));
		
		JList solicList = new JList();
		solicList.setModel(new AbstractListModel() {
			String[] values = new String[] {"+ Adicionar..."};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		solicListPanel.add(solicList);
		solicInfoPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sl__solicPanel.putConstraint(SpringLayout.NORTH, solicInfoPanel, 10, SpringLayout.NORTH, _solicPanel);
		sl__solicPanel.putConstraint(SpringLayout.WEST, solicInfoPanel, 251, SpringLayout.WEST, _solicPanel);
		sl__solicPanel.putConstraint(SpringLayout.SOUTH, solicInfoPanel, -10, SpringLayout.SOUTH, _solicPanel);
		sl__solicPanel.putConstraint(SpringLayout.EAST, solicInfoPanel, -10, SpringLayout.EAST, _solicPanel);
		_solicPanel.add(solicInfoPanel);
		SpringLayout sl_solicInfoPanel = new SpringLayout();
		solicInfoPanel.setLayout(sl_solicInfoPanel);
		
		JLabel lblSolicMsg = new JLabel("Mensagem");
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, lblSolicMsg, 10, SpringLayout.WEST, solicInfoPanel);
		solicInfoPanel.add(lblSolicMsg);
		
		txtSolicSolicitante = new JTextField();
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, txtSolicSolicitante, 10, SpringLayout.NORTH, solicInfoPanel);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, txtSolicSolicitante, -10, SpringLayout.EAST, solicInfoPanel);
		txtSolicSolicitante.setEditable(false);
		solicInfoPanel.add(txtSolicSolicitante);
		txtSolicSolicitante.setColumns(10);
		
		txtSolicAssunto = new JTextField();
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, txtSolicAssunto, 0, SpringLayout.WEST, txtSolicSolicitante);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, txtSolicAssunto, -10, SpringLayout.EAST, solicInfoPanel);
		solicInfoPanel.add(txtSolicAssunto);
		txtSolicAssunto.setColumns(10);
		
		JComboBox comboSolicEstadoTemp = new JComboBox();
		comboSolicEstadoTemp.setModel(new DefaultComboBoxModel(new String[] {"Pendente", "Execução", "Encerrado"}));
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, comboSolicEstadoTemp, -94, SpringLayout.EAST, solicInfoPanel);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, comboSolicEstadoTemp, -10, SpringLayout.EAST, solicInfoPanel);
		solicInfoPanel.add(comboSolicEstadoTemp);
		
		JEditorPane editorSolicMsg = new JEditorPane();
		
		JScrollPane scrollSolicMsg = new JScrollPane();
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicMsg, 3, SpringLayout.NORTH, scrollSolicMsg);
		sl_solicInfoPanel.putConstraint(SpringLayout.SOUTH, scrollSolicMsg, -6, SpringLayout.NORTH, comboSolicEstadoTemp);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, scrollSolicMsg, -10, SpringLayout.EAST, solicInfoPanel);
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, scrollSolicMsg, 6, SpringLayout.SOUTH, txtSolicAssunto);
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, scrollSolicMsg, 0, SpringLayout.WEST, txtSolicSolicitante);
		scrollSolicMsg.setViewportView(editorSolicMsg);
		solicInfoPanel.add(scrollSolicMsg);
		
		JLabel lblSolicSolicitante = new JLabel("Solicitante");
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicSolicitante, 0, SpringLayout.EAST, lblSolicMsg);
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, txtSolicSolicitante, 6, SpringLayout.EAST, lblSolicSolicitante);
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicSolicitante, 13, SpringLayout.NORTH, solicInfoPanel);
		solicInfoPanel.add(lblSolicSolicitante);
		
		JLabel lblSolicAssunto = new JLabel("Assunto");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicAssunto, 3, SpringLayout.NORTH, txtSolicAssunto);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicAssunto, 0, SpringLayout.EAST, lblSolicMsg);
		solicInfoPanel.add(lblSolicAssunto);
		
		txtSolicInicio = new JTextField();
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, txtSolicInicio, 0, SpringLayout.NORTH, comboSolicEstadoTemp);
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, txtSolicInicio, 0, SpringLayout.WEST, txtSolicSolicitante);
		txtSolicInicio.setEditable(false);
		solicInfoPanel.add(txtSolicInicio);
		txtSolicInicio.setColumns(10);
		
		JLabel lblSolicInicio = new JLabel("Início");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicInicio, 4, SpringLayout.NORTH, comboSolicEstadoTemp);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicInicio, 0, SpringLayout.EAST, lblSolicMsg);
		solicInfoPanel.add(lblSolicInicio);
		
		JLabel lblSolicPrazo = new JLabel("Prazo");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicPrazo, 11, SpringLayout.SOUTH, lblSolicInicio);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicPrazo, 0, SpringLayout.EAST, lblSolicMsg);
		solicInfoPanel.add(lblSolicPrazo);
		
		txtSolicDep = new JTextField();
		txtSolicDep.setEditable(false);
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, txtSolicDep, 6, SpringLayout.SOUTH, txtSolicSolicitante);
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, txtSolicDep, 0, SpringLayout.WEST, txtSolicSolicitante);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, txtSolicDep, -10, SpringLayout.EAST, solicInfoPanel);
		solicInfoPanel.add(txtSolicDep);
		txtSolicDep.setColumns(10);
		
		JLabel lblSolicDep = new JLabel("Depto.");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicDep, 3, SpringLayout.NORTH, txtSolicDep);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicDep, 0, SpringLayout.EAST, lblSolicMsg);
		solicInfoPanel.add(lblSolicDep);
		
		JLabel lblSolicEstadoFinal = new JLabel("Estado concreto");
		solicInfoPanel.add(lblSolicEstadoFinal);
		
		JComboBox comboSolicEstadoFinal = new JComboBox();
		comboSolicEstadoFinal.setModel(new DefaultComboBoxModel(new String[] {"Pendente", "Execução", "Encerrado"}));
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicEstadoFinal, 4, SpringLayout.NORTH, comboSolicEstadoFinal);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicEstadoFinal, -6, SpringLayout.WEST, comboSolicEstadoFinal);
		sl_solicInfoPanel.putConstraint(SpringLayout.SOUTH, comboSolicEstadoTemp, -8, SpringLayout.NORTH, comboSolicEstadoFinal);
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, comboSolicEstadoFinal, 0, SpringLayout.WEST, comboSolicEstadoTemp);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, comboSolicEstadoFinal, -10, SpringLayout.EAST, solicInfoPanel);
		solicInfoPanel.add(comboSolicEstadoFinal);
		
		JComboBox comboSolicResponsavel = new JComboBox();
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, txtSolicAssunto, 6, SpringLayout.SOUTH, comboSolicResponsavel);
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, comboSolicResponsavel, 6, SpringLayout.SOUTH, txtSolicDep);
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, comboSolicResponsavel, 0, SpringLayout.WEST, txtSolicDep);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, comboSolicResponsavel, -10, SpringLayout.EAST, solicInfoPanel);
		solicInfoPanel.add(comboSolicResponsavel);
		
		JButton btnSolicSave = new JButton("Salvar");
		sl_solicInfoPanel.putConstraint(SpringLayout.SOUTH, comboSolicEstadoFinal, -6, SpringLayout.NORTH, btnSolicSave);
		sl_solicInfoPanel.putConstraint(SpringLayout.SOUTH, btnSolicSave, -10, SpringLayout.SOUTH, solicInfoPanel);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, btnSolicSave, 0, SpringLayout.EAST, txtSolicSolicitante);
		solicInfoPanel.add(btnSolicSave);
		
		JButton btnSolicAdd = new JButton("Adicionar");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, btnSolicAdd, 0, SpringLayout.NORTH, btnSolicSave);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, btnSolicAdd, -6, SpringLayout.WEST, btnSolicSave);
		solicInfoPanel.add(btnSolicAdd);
		
		JLabel lblSolicEstadoTemp = new JLabel("Estado de execução");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicEstadoTemp, 4, SpringLayout.NORTH, comboSolicEstadoTemp);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicEstadoTemp, -6, SpringLayout.WEST, comboSolicEstadoTemp);
		solicInfoPanel.add(lblSolicEstadoTemp);
		
		JLabel lblSolicResponsavel = new JLabel("Respons.");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, lblSolicResponsavel, 3, SpringLayout.NORTH, comboSolicResponsavel);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, lblSolicResponsavel, 0, SpringLayout.EAST, lblSolicMsg);
		solicInfoPanel.add(lblSolicResponsavel);
		
		MaskFormatter mask = new MaskFormatter("##/##/####");
		JFormattedTextField txtSolicPrazo = new JFormattedTextField(mask);
		txtSolicPrazo.setText("");
		sl_solicInfoPanel.putConstraint(SpringLayout.NORTH, txtSolicPrazo, 6, SpringLayout.SOUTH, txtSolicInicio);
		sl_solicInfoPanel.putConstraint(SpringLayout.WEST, txtSolicPrazo, 6, SpringLayout.EAST, lblSolicPrazo);
		sl_solicInfoPanel.putConstraint(SpringLayout.SOUTH, txtSolicPrazo, 26, SpringLayout.SOUTH, txtSolicInicio);
		sl_solicInfoPanel.putConstraint(SpringLayout.EAST, txtSolicPrazo, 92, SpringLayout.EAST, lblSolicPrazo);
		solicInfoPanel.add(txtSolicPrazo);
		
		_request = new RequestService(solicList, txtSolicSolicitante, txtSolicDep, txtSolicAssunto, txtSolicInicio, txtSolicPrazo, comboSolicResponsavel, comboSolicEstadoTemp, comboSolicEstadoFinal, editorSolicMsg, btnSolicSave, btnSolicAdd);

	}
	
	private void AdicionarLogs() {
		// Logs
		//------------------------------------------------------------------------------------------------------------------------
		_logPanel = new JPanel();
		_tabbedPane.addTab(_logsTabName, null, _logPanel, null);
		_logPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		_logPanel.add(scrollPane, BorderLayout.CENTER);
		
		logTable = new JTable();
		logTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"ID", "App", "Tipo", "Usu\u00E1rio", "Log"
				}
				) {
			Class[] columnTypes = new Class[] {
					Integer.class, String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
					false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		logTable.getColumnModel().getColumn(0).setResizable(false);
		logTable.getColumnModel().getColumn(1).setResizable(false);
		logTable.getColumnModel().getColumn(2).setResizable(false);
		logTable.getColumnModel().getColumn(3).setResizable(false);
		logTable.getColumnModel().getColumn(4).setResizable(false);
		logTable.setAutoResizeMode(logTable.AUTO_RESIZE_OFF);
		scrollPane.setViewportView(logTable);
		
		_log = new LogService(logTable);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainInterface window = new MainInterface();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainInterface() {
		initialize();
		
		try {
			_l = new Login(frmAdministracaoTi, () -> PrepareApplication());
			SpringLayout springLayout = new SpringLayout();
			frmAdministracaoTi.getContentPane().setLayout(springLayout);
			
			_tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			
			springLayout.putConstraint(SpringLayout.NORTH, _tabbedPane, 69, SpringLayout.NORTH, frmAdministracaoTi.getContentPane());
			springLayout.putConstraint(SpringLayout.WEST, _tabbedPane, 0, SpringLayout.WEST, frmAdministracaoTi.getContentPane());
			springLayout.putConstraint(SpringLayout.SOUTH, _tabbedPane, 0, SpringLayout.SOUTH, frmAdministracaoTi.getContentPane());
			springLayout.putConstraint(SpringLayout.EAST, _tabbedPane, 0, SpringLayout.EAST, frmAdministracaoTi.getContentPane());
			_tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			frmAdministracaoTi.getContentPane().add(_tabbedPane);
			
			// Informação de usuário
			//------------------------------------------------------------------------------------------------------------------------
			JPanel infoPanel = new JPanel();
			springLayout.putConstraint(SpringLayout.NORTH, infoPanel, 10, SpringLayout.NORTH, frmAdministracaoTi.getContentPane());
			springLayout.putConstraint(SpringLayout.WEST, infoPanel, 10, SpringLayout.WEST, frmAdministracaoTi.getContentPane());
			springLayout.putConstraint(SpringLayout.SOUTH, infoPanel, -6, SpringLayout.NORTH, _tabbedPane);
			springLayout.putConstraint(SpringLayout.EAST, infoPanel, -10, SpringLayout.EAST, _tabbedPane);
			infoPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			frmAdministracaoTi.getContentPane().add(infoPanel);
			
			_lblName = new JLabel("New label");
			_lblCargo = new JLabel("New label");
			
			GroupLayout gl_infoPanel = new GroupLayout(infoPanel);
			gl_infoPanel.setHorizontalGroup(
				gl_infoPanel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_infoPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_infoPanel.createParallelGroup(Alignment.LEADING)
							.addComponent(_lblName)
							.addComponent(_lblCargo))
						.addContainerGap(531, Short.MAX_VALUE))
			);
			gl_infoPanel.setVerticalGroup(
				gl_infoPanel.createParallelGroup(Alignment.LEADING)
					.addGroup(Alignment.TRAILING, gl_infoPanel.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(_lblName)
						.addGap(1)
						.addComponent(_lblCargo)
						.addContainerGap())
			);
			infoPanel.setLayout(gl_infoPanel);
			
			// Services
			//------------------------------------------------------------------------------------------------------------------------
			_tabbedPane.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					String tabTitle = _tabbedPane.getTitleAt(_tabbedPane.getSelectedIndex());

					if (tabTitle == _funcsTabName && _worker != null) {
						_worker.OnAppFocus();
					}
					else if (tabTitle == _depTabName && _deps != null) {
						_deps.OnAppFocus();
					}
					else if (tabTitle == _solicTabName && _request != null) {
						_request.OnAppFocus();
					}
					else if (tabTitle == _logsTabName && _log != null) {
						_log.OnAppFocus();
					}
					
					try {
						DBSync.GetInstance().SyncAll();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			_l.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAdministracaoTi = new JFrame();
		frmAdministracaoTi.setTitle("Administração T.I.");
		frmAdministracaoTi.setBounds(100, 100, 786, 659);
		frmAdministracaoTi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void PrepareApplication() {
		SessionUser self = Session.GetInstance().User;
		_lblName.setText(self.get_func().get_name() + String.format(" (#%d)", self.get_func().get_id()));
		_lblCargo.setText(self.get_type().get_name() + " - " + self.get_dep().get_desc());

		if (self.get_perms().get_appDepartamentos()) {
			AdicionarDepartamentos();
		}
		
		if (self.get_perms().get_appFuncionarios()) {
			AdicionarFuncionarios();
		}
		
		if (self.get_perms().get_appSolicitacoes() || self.get_dep().get_sigla().contentEquals("DTI")) {
			try {
				AdicionarSolicitacoes();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (self.get_perms().get_appLogs()) {
			AdicionarLogs();
		}
		
		ChangeListener[] tabUpdate = _tabbedPane.getChangeListeners();
		for (int i = 0; i < tabUpdate.length; i++) {
			tabUpdate[i].stateChanged(new ChangeEvent(_tabbedPane));
		}
	}
}
