package session;

public class TipoFuncionario {
	private int _id;
	private String _name;
	
	public TipoFuncionario(int id, String name) {
		_id = id;
		_name = name;
	}
	
	public int get_id() {
		return _id;
	}

	public String get_name() {
		return _name;
	}

	@Override
	public String toString() {
		return _name;
	}
}
