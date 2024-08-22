package session;

public class Permissoes {
	private int _id;
	private Boolean _appDepartamentos;
	private Boolean _appFuncionarios;
	private Boolean _appSolicitacoes;
	private Boolean _appLogs;
	
	public Permissoes(int id, Boolean deps, Boolean funcs, Boolean solics, Boolean logs) {
		_id = id;
		_appDepartamentos = deps;
		_appFuncionarios = funcs;
		_appSolicitacoes = solics;
		_appLogs = logs;
	}

	public int get_id() {
		return _id;
	}

	public Boolean get_appDepartamentos() {
		return _appDepartamentos;
	}

	public Boolean get_appFuncionarios() {
		return _appFuncionarios;
	}

	public Boolean get_appSolicitacoes() {
		return _appSolicitacoes;
	}

	public Boolean get_appLogs() {
		return _appLogs;
	}
	
}
