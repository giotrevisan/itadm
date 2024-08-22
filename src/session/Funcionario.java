package session;

public class Funcionario {
	private int _id;
	private String _name;
	private int _dep;
	private int _tipo;
	private Boolean _active;
	
	public Funcionario(int id, String name, int dep, int tipo, Boolean active) {
		_id = id;
		_name = name;
		_dep = dep;
		_tipo = tipo;
		_active = active;
	}
	
	public int get_id() {
		return _id;
	}

	public String get_name() {
		return _name;
	}

	public int get_dep() {
		return _dep;
	}

	public int get_tipo() {
		return _tipo;
	}

	public Boolean get_active() {
		return _active;
	}

	@Override
	public String toString() {
		return _name;
	}
	
}
