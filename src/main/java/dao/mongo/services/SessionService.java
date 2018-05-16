package dao.mongo.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import dao.mongo.entity.ConnectionUsers;
import dao.mongo.entity.Session;
import dao.mongo.entity.SessionLibelle;
import dao.mongo.entity.Sessions;
import dao.mongo.entity.User;

@Repository
public class SessionService {
	
	@Autowired
	MongoOperations mongoOps;
	
	@Autowired
	public SessionService(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}
	
	public ConnectionUsers getConnectionsByUserID(Integer id){
		Query query = new Query(Criteria.where("_id").is(id));
		return  mongoOps.findOne(query, ConnectionUsers.class);
	}
	
	public List<ConnectionUsers> getAllUserConnections() {
		return mongoOps.findAll(ConnectionUsers.class);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	public List<Session> getAllSessionByDate(LocalDate date) {
		 List<ConnectionUsers> sessionsUsers = mongoOps.findAll(ConnectionUsers.class);
		 List<Session> allSessionsByDate = new ArrayList<Session>();
		 for( ConnectionUsers s : sessionsUsers) {
			List<SessionLibelle> listeSessions = s.getSessions();
			for(int i =0; i< listeSessions.size(); i++) {
				LocalDateTime dateTime = listeSessions.get(i).getSession().getDateConnexion();
				LocalDate dateSession = LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
				if(dateSession.equals(date)) {
					allSessionsByDate.add(listeSessions.get(i).getSession());
//					System.out.println("user_id : " +s.get_id() +" sessions : "+listeSessions[i].getSession());
				}
			}
		 }
		return allSessionsByDate;
	}
	
	public LocalDate getDateConnexionMin() {
		Query query = new Query();
		query.limit(1);
		query.with(new Sort(Sort.Direction.ASC, "sessions.session.dateConnexion"));
		List<ConnectionUsers> sessionsUsers = mongoOps.find(query, ConnectionUsers.class);
		LocalDate dateMin = LocalDate.of(3000, 01, 01);
		for( ConnectionUsers s : sessionsUsers) {
			List<SessionLibelle> listeSessions = s.getSessions();
			for(int i =0; i< listeSessions.size(); i++) {
				LocalDateTime dateTime = listeSessions.get(i).getSession().getDateConnexion();
				LocalDate dateSession = LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
				if(dateSession.isBefore(dateMin)) {
					dateMin=dateSession;
				}
			}
		 }
		return dateMin;
		
	}
	
	public LocalDate getDateConnexionMax() {
		Query query = new Query();
		query.limit(1);
		query.with(new Sort(Sort.Direction.DESC, "sessions.session.dateConnexion"));
		List<ConnectionUsers> sessionsUsers = mongoOps.find(query, ConnectionUsers.class);
		LocalDate dateMax = LocalDate.of(1900,01,01);
		for( ConnectionUsers s : sessionsUsers) {
			List<SessionLibelle> listeSessions = s.getSessions();
			for(int i =0; i< listeSessions.size(); i++) {
				LocalDateTime dateTime = listeSessions.get(i).getSession().getDateConnexion();
				LocalDate dateSession = LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
				if(dateSession.isAfter(dateMax)) {
					dateMax=dateSession;
				}
			}
		 }
		return dateMax;
		
	}
	
	public Double deconnectUserById(Integer id){
		long duration = 0;
		long nbrSessionDeconnex=0;
		ConnectionUsers sessions = getConnectionsByUserID(id);
		for(int i=0;i< sessions.getSessions().size();i++) {
			if(sessions.getSessions().get(i).getSession().getDateDeconnexion() == null || sessions.getSessions().get(i).getSession().getDateDeconnexion().equals(LocalDateTime.of(1900,01,01,0,0))) {
				sessions.getSessions().get(i).getSession().setDateDeconnexion(LocalDateTime.now());
				
				if(sessions.getSessions().get(i).getSession().getDateConnexion().isAfter(LocalDateTime.now())) {
					duration = duration + (Duration.between(sessions.getSessions().get(i).getSession().getDateConnexion(),sessions.getSessions().get(i).getSession().getDateDeconnexion()).getSeconds())*(-1);
				}else {
				duration += Duration.between(sessions.getSessions().get(i).getSession().getDateConnexion(),sessions.getSessions().get(i).getSession().getDateDeconnexion()).getSeconds();
				}
				nbrSessionDeconnex ++;
			}
		}
		Double moyenneDureeConnex = (double) (duration/nbrSessionDeconnex);
		// mise à jour dans mongo
		mongoOps.save(sessions);
		return moyenneDureeConnex; 
	}
	
	public void addSessionToUser(ConnectionUsers cu) {
		mongoOps.save(cu,"historique");
	}

}
