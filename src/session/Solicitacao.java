package session;

import java.time.LocalDate;

public class Solicitacao {
	private int _id;
	private Funcionario _solicitante;
	private Departamento _dep;
	private Funcionario _responsavel;
	private String _assunto;
	private String _mensagem;
	private LocalDate _inicio;
	private LocalDate _fim;
	private String _temp;
	private String _final;
	
	public Solicitacao(int _id, Funcionario _solicitante, Departamento _dep, Funcionario _responsavel, String _assunto,
			String _mensagem, LocalDate _inicio, LocalDate _fim, String _temp, String _final) {
		super();
		this._id = _id;
		this._solicitante = _solicitante;
		this._dep = _dep;
		this._responsavel = _responsavel;
		this._assunto = _assunto;
		this._mensagem = _mensagem;
		this._inicio = _inicio;
		this._fim = _fim;
		this._temp = _temp;
		this._final = _final;
	}
	
	public int get_id() {
		return _id;
	}
	public Funcionario get_solicitante() {
		return _solicitante;
	}
	public Departamento get_dep() {
		return _dep;
	}
	public Funcionario get_responsavel() {
		return _responsavel;
	}
	public String get_assunto() {
		return _assunto;
	}
	public String get_mensagem() {
		return _mensagem;
	}
	public LocalDate get_inicio() {
		return _inicio;
	}
	public LocalDate get_fim() {
		return _fim;
	}
	public String get_temp() {
		return _temp;
	}
	public String get_final() {
		return _final;
	}

	@Override
	public String toString() {
		return _assunto;
	}
}
