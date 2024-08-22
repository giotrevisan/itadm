package session;

public class Log {
	private int _id;
	private String _app;
	private String _actionType;
	private String _user;
	private String _changes;
	
	public Log(int id, String app, String actionType, String user, String changes) {
		_id = id;
		_app = app;
		_actionType = actionType;
		_user = user;
		_changes = changes;
	}

	public int get_id() {
		return _id;
	}

	public String get_app() {
		return _app;
	}

	public String get_actionType() {
		return _actionType;
	}

	public String get_user() {
		return _user;
	}

	public String get_changes() {
		return _changes;
	}
	
}
