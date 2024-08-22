package session;

import java.sql.*;
import java.util.Properties;

public class DBConnection {
	
	private String _url;
	private String _username;
	private String _password;
	private static DBConnection _instance;
	public Connection Connection;
	
	private DBConnection() {
		_url = "jdbc:postgresql://localhost/lprc5";
		_username = "postgres";
		_password = "postgres";
		try {
			this.Connection = DriverManager.getConnection(_url + String.format("?user=%s&password=%s&client_ecoding=utf8", _username, _password));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		Connection.close();
	}
	
	public static synchronized DBConnection GetInstance() {
		if (_instance == null) {
			_instance = new DBConnection();
		}
		
		return _instance;
	}
}
