package session;

public class Session {
	
	private static Session _instance;
	public SessionUser User;
	
	private Session() {
		
	}
	
	public void CreateSessionUserFromWorker(Funcionario f) {
		if (f.get_active()) {
			DBSync sync = DBSync.GetInstance();
			Departamento d = sync.Departamentos.get(f.get_dep());
			if (d.get_id() == 0) {
				d = DBSync.GetInstance().get_publicDepartment();
			}
			
			TipoFuncionario t = sync.TipoFuncionario.get(f.get_tipo());
			Permissoes p = sync.Permissoes.get(t.get_id());
			
			User = new SessionUser(f, d, t, p);
		}
		else {
			User = null;
		}
	}
	
	public static Session GetInstance() {
		if (_instance == null) {
			_instance = new Session();
		}
		
		return _instance;
	}
}
