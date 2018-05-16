package dao.mongo.entity;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "historique")
public class ConnectionUsers {
	
	@Id
	private Integer _id;
	private List<SessionLibelle> sessions;
	
	
	
	public ConnectionUsers() {
		super();
	}


	public ConnectionUsers(Integer _id, List<SessionLibelle> sessions) {
		super();
		this._id = _id;
		this.sessions = sessions;
	}





	@Override
	public String toString() {
		return "ConnectionUsers [_id=" + _id + ", sessions=" + sessions + "]";
	}


	public Integer get_id() {
		return _id;
	}


	public void set_id(Integer _id) {
		this._id = _id;
	}


	public List<SessionLibelle> getSessions() {
		return sessions;
	}


	public void setSessions(List<SessionLibelle> sessions) {
		this.sessions = sessions;
	}
	
	
	
	

}
