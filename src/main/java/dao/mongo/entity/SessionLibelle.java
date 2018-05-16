package dao.mongo.entity;

public class SessionLibelle {
	
	private Session session;

	public SessionLibelle(Session session) {
		super();
		this.session = session;
	}

	public SessionLibelle() {
		super();
	}

	@Override
	public String toString() {
		return "SessionLibelle [session=" + session + "]";
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	
	

}
