package session;

public class SessionUser {
	private Funcionario _func;
	private Departamento _dep;
	private TipoFuncionario _type;
	private Permissoes _perms;
	private Boolean _su;
	
	public SessionUser(Funcionario f, Departamento d, TipoFuncionario t, Permissoes p) {
		_func = f;
		_dep = d;
		_type = t;
		_perms = p;
		
		_su = f.get_id() == 1;
	}

	public Funcionario get_func() {
		return _func;
	}

	public Departamento get_dep() {
		return _dep;
	}

	public TipoFuncionario get_type() {
		return _type;
	}

	public Permissoes get_perms() {
		return _perms;
	}
	
	public Boolean AmISuperUser() {
		return _su;
	}
	
}
