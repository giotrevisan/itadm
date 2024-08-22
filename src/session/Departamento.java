package session;

public class Departamento {

	private int _id;
	private String _sigla;
	private String _desc;
	private Boolean _active;
	
	public Departamento(int id, String sigla, String desc, Boolean active) {
		_id = id;
		_sigla = sigla;
		_desc = desc;
		_active = active;
	}
	
	public int get_id() {
		return _id;
	}

	public String get_sigla() {
		return _sigla;
	}

	public String get_desc() {
		return _desc;
	}

	public Boolean get_active() {
		return _active;
	}

	@Override
	public String toString() {
		return _desc;
	}
}
