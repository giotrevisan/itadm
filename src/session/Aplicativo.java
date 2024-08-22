package session;

public class Aplicativo {
	private int _id;
	private String _name;
	
	public Aplicativo(int id, String name) {
		_id = id;
		_name = name;
	}

	public int get_id() {
		return _id;
	}

	public String get_name() {
		return _name;
	}
}
