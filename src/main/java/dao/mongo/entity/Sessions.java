package dao.mongo.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;


public class Sessions {
	
	@DBRef
	public List<SessionLibelle> session;
	
	

	public Sessions(List<SessionLibelle> session) {
		super();
		this.session = session;
	}



//	public Sessions(List<SessionLibelle> session) {
//		super();
//		SessionLibelle[] sessionTab = new SessionLibelle[session.size()];
//		for(SessionLibelle s : session) {
//			for(int i =0; i<session.size();i++) {
//				sessionTab[i]=s;
//			}
//		}
//		this.session = sessionTab;
//	}



	public Sessions() {
		super();
	}

 

	@Override
	public String toString() {
		return "Sessions [session=" + session + "]";
	}



	public List<SessionLibelle> getSessionLibelle() {
		return session;
	}



	public void setSessionLibelle(List<SessionLibelle> session) {
		this.session = session;
	}

//	public List<SessionLibelle> getSessionLibelle() {
//		List<SessionLibelle> sessions = new ArrayList<SessionLibelle>();
//		for (int i= 0; i< session.length;i++) {
//			sessions.add(session[i]);
//		}
//		return sessions;
//	}
//
//	public void setSessionLibelle(List<SessionLibelle> session) {
//		SessionLibelle[] sessionTab = new SessionLibelle[session.size()];
//		for(SessionLibelle s : session) {
//			for(int i =0; i<session.size();i++) {
//				sessionTab[i]=s;
//			}
//		}
//		this.session = sessionTab;
//	}
//	
	

}
