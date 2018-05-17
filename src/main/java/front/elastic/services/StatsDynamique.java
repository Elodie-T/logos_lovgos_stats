package front.elastic.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import dao.mongo.entity.ConnectionUsers;
import dao.mongo.entity.Geolocalisation;
import dao.mongo.entity.Loves;
import dao.mongo.entity.Session;
import dao.mongo.entity.SessionLibelle;
import dao.mongo.entity.Sessions;
import dao.mongo.entity.User;
import dao.mongo.services.LovesService;
import dao.mongo.services.SessionService;
import dao.mongo.services.UsersService;

public class StatsDynamique {

	private TransportClient client;
	private SessionService sessionService;
	private LovesService lovesService;
	private UsersService usersService;
	ManageUsers m;
	ManageConnexion c;
	ManageCalculLoveConnex lc;
	final List<User> usersConnectes = new ArrayList<User>();
	final List<User> usersDeConnectes = new ArrayList<User>();
	final List<String> plateforme = Arrays.asList("Logos", "Lovegos");

	@SuppressWarnings("resource")
	public StatsDynamique() throws IOException {
		
		 m = new ManageUsers();
		 c = new ManageConnexion();
		lc = new ManageCalculLoveConnex();

		// se connecter à mongoDB
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		sessionService = ctx.getBean(SessionService.class);
		lovesService = ctx.getBean(LovesService.class);
		usersService = ctx.getBean(UsersService.class);
		

		// se connecter à Elastic Search
		try {
			client= new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
					.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9301));

		} catch (UnknownHostException e) {
			System.out.println("Erreur de connection à Elastic Search");
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////	///////////////////////////////////////////////////////////////////////////////////////
	public void deconnectionUserById(Integer id) throws IOException, InterruptedException, ExecutionException {
		//mise à jour dans mongo collection historique
		Double dureeSessionDeconnectees= sessionService.deconnectUserById(id);
//		System.out.println("dureeSessionDeconnectees : "+dureeSessionDeconnectees);
		//mise à jour dans ElasticSearch : le user n'est plus connecte
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(m.getIndex());
		updateRequest.type(m.getType());
		updateRequest.id(id.toString());
		updateRequest.doc(XContentFactory.jsonBuilder()
				.startObject()
				.field("connection", "none")
				.endObject());
		client.update(updateRequest).get();
		updateDureeConnexMoyen(dureeSessionDeconnectees);
		
	}
	
	public void addUserSessionById(Integer id, String plateforme, Geolocalisation geoLoc) throws InterruptedException, ExecutionException, IOException {
		Session session = new Session(plateforme, LocalDateTime.now(), LocalDateTime.of(1900,01,01,0,0), geoLoc);
		SessionLibelle sessionLibelle = new SessionLibelle(session);
		ConnectionUsers cu = sessionService.getConnectionsByUserID(id);
		int nbrSession = cu.getSessions().size();
		cu.getSessions().add(sessionLibelle);
		// ajout en BDD
		sessionService.addSessionToUser(cu);
		
		//mise à jour dans ElasticSearch : le user est connecte
				UpdateRequest updateRequest = new UpdateRequest();
				updateRequest.index(m.getIndex());
				updateRequest.type(m.getType());
				updateRequest.id(id.toString());
				updateRequest.doc(XContentFactory.jsonBuilder()
						.startObject()
						.field("connection", plateforme)
						.endObject());
				client.update(updateRequest).get();
				
				
			// on veut update le nbr de visite moyen par jour sur ElasticSearch
//				LocalDate startDate = sessionService.getDateConnexionMin();
//				LocalDate endDate = LocalDate.now();
//				
//				int nbrJour =(int) ChronoUnit.DAYS.between(startDate,endDate);
//				// on récupère le nbr de jour moyen sur elasticSearch
//				int nbrVisite=0;
//				GetResponse getResponse = client.prepareGet(lc.getIndex(),lc.getType(),lc.getId_unique().toString()).execute().actionGet();
//				System.out.println(getResponse.getSourceAsString());
//			    Map<String, Object> source = getResponse.getSource();
//			    for (Map.Entry<String, Object> entry : source.entrySet())
//			    {
//			    	if (entry.getKey().equals("nbVisites")) {
//			    		nbrVisite = Integer.parseInt(entry.getValue().toString()) +1;
//			    	}
//			    }
//			    System.out.println("nbr visite : " +nbrVisite + "   ,   nbr jour : "+nbrJour);
//			   double nbrVisiteMoyenParJourUpdate = (double) nbrVisite/nbrJour;
//			   System.out.println("moyenne : " +nbrVisiteMoyenParJourUpdate);
//			// update sur ElasticSearch
//			    UpdateRequest updateRequest1 = new UpdateRequest();
//			    updateRequest1.index(lc.getIndex());
//			    updateRequest1.type(lc.getType());
//			    updateRequest1.id(lc.getId_unique().toString());
//			    updateRequest1.doc(XContentFactory.jsonBuilder()
//						.startObject()
//						.field("nbVisites", nbrVisiteMoyenParJourUpdate)
//						.endObject());
//				client.update(updateRequest1).get();
			   
			   
		
	}
	
	public void addLove(Loves love) throws IOException, InterruptedException, ExecutionException {
		// ajout en BDD
		lovesService.addLove(love);
		
		//augmenter le nbr de love dans elasticSearch
		
				// on récupère la durée moyenne sur ElasticSearch
				int nbrLoveUpdate=0;
				GetResponse getResponse = client.prepareGet(lc.getIndex(),lc.getType(),lc.getId_unique().toString()).execute().actionGet();
				System.out.println(getResponse.getSourceAsString());
			    Map<String, Object> source = getResponse.getSource();
			    for (Map.Entry<String, Object> entry : source.entrySet())
			    {
			    	if (entry.getKey().equals("nbLoves")) {
			    		nbrLoveUpdate = Integer.parseInt(entry.getValue().toString()) +1;
			    	}
			    }
			    
			 // update sur ElasticSearch
			    UpdateRequest updateRequest = new UpdateRequest();
				updateRequest.index(lc.getIndex());
				updateRequest.type(lc.getType());
				updateRequest.id(lc.getId_unique().toString());
				updateRequest.doc(XContentFactory.jsonBuilder()
						.startObject()
						.field("nbLoves", nbrLoveUpdate)
						.endObject());
				client.update(updateRequest).get();
	}

	private void updateDureeConnexMoyen(Double dureeSessionDeconnectees) throws IOException, InterruptedException, ExecutionException {
		
		// on récupère la durée moyenne sur ElasticSearch
		double moyConnex=0;
		GetResponse getResponse = client.prepareGet(lc.getIndex(),lc.getType(), lc.getId_unique().toString()).execute().actionGet();
		System.out.println(getResponse.getSourceAsString());
	    Map<String, Object> source = getResponse.getSource();
	    for (Map.Entry<String, Object> entry : source.entrySet())
	    {
	    	if (entry.getKey().equals("moyCon")) {
	    		moyConnex = Double.parseDouble(entry.getValue().toString());
	    	}
	    }
	    double moyenneConnexUpdate=  (moyConnex+dureeSessionDeconnectees)/2;
	    
	    // update sur ElasticSearch
	    UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(lc.getIndex());
		updateRequest.type(lc.getType());
		updateRequest.id(lc.getId_unique().toString());
		updateRequest.doc(XContentFactory.jsonBuilder()
				.startObject()
				.field("moyCon", moyenneConnexUpdate)
				.endObject());
		client.update(updateRequest).get();
	    
	}
	
	public void getdureeMoyenne() {
		double moyConnex=0;
		GetResponse getResponse = client.prepareGet(lc.getIndex(),lc.getType(), "1").execute().actionGet();
		System.out.println(getResponse.getSourceAsString());
	    Map<String, Object> source = getResponse.getSource();
	    for (Map.Entry<String, Object> entry : source.entrySet())
	    {
	    	if (entry.getKey().equals("moyCon")) {
	    		moyConnex = Double.parseDouble(entry.getValue().toString());
	    		System.out.println("moyenne connexion : "+moyConnex);
	    	}
	    }
	}

	public void simulation() {
		
		
		List<ConnectionUsers> allSessions = sessionService.getAllUserConnections();
		for(ConnectionUsers cu : allSessions) {
			List<SessionLibelle> sessions = cu.getSessions();
			for(SessionLibelle sl : sessions) {
				User user = usersService.getUserByID(cu.get_id());
				if(sl.getSession().getDateDeconnexion()==null || sl.getSession().getDateDeconnexion().equals(LocalDateTime.of(1900,01,01,0,0))) {
					usersConnectes.add(user);
				}else {
					usersDeConnectes.add(user);
				}
			}
		}
		
		TimerTask taskDeconnex = new TimerTask() {
			public void run() {
				 int randIndexUser = new Random().nextInt(usersConnectes.size());
				 try {
					deconnectionUserById(usersConnectes.get(randIndexUser).get_id());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				 usersConnectes.remove(randIndexUser);
				 usersDeConnectes.add(usersConnectes.get(randIndexUser));
				 System.out.println("le user n° "+ usersConnectes.get(randIndexUser).get_id()+" s'est déconnecté");
			}
		};
		
		TimerTask taskaddLoves = new TimerTask() {
			public void run() {
				List<Loves> listeLove = lovesService.getAllLoves();
				Random r = new Random();
				int premierEleveLovegos = 10001;
				int dernierEleveLovegos = 15000;
				int randomEleveLovegos1 = r.nextInt(dernierEleveLovegos-premierEleveLovegos) + premierEleveLovegos;
				int randomEleveLovegos2 = r.nextInt(dernierEleveLovegos-premierEleveLovegos) + premierEleveLovegos;
				if(randomEleveLovegos1 != randomEleveLovegos2) {
					Loves love = new Loves(listeLove.size()+1, LocalDate.now(), false, randomEleveLovegos1, randomEleveLovegos2);
					System.out.println("le user n° "+ randomEleveLovegos1+" a envoyé un love au user n° "+randomEleveLovegos2);
					 try {
						addLove(love);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					Loves love = new Loves(listeLove.size()+1, LocalDate.now(), false, randomEleveLovegos1, randomEleveLovegos2+1);
					System.out.println("le user n° "+ randomEleveLovegos1+" a envoyé un love au user n° "+randomEleveLovegos2);
					 try {
						addLove(love);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		
		TimerTask taskConnex = new TimerTask() {
			public void run() {
				 int randIndexUser = new Random().nextInt(usersDeConnectes.size());
				 int randIndexPlateforme = new Random().nextInt(plateforme.size());
				 Random rand = new Random();
				 int rand1 = rand.nextInt(15-1) + 1;
				 double latitude = 49+rand1;
				 double longitude = 2+rand1;
				 Geolocalisation geoLoc = new Geolocalisation(latitude, longitude);
				 try {
					addUserSessionById(usersDeConnectes.get(randIndexUser).get_id(),plateforme.get(randIndexPlateforme),geoLoc);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				 usersDeConnectes.remove(randIndexUser);
				 usersConnectes.add(usersDeConnectes.get(randIndexUser));
				 System.out.println("le user n° "+ usersDeConnectes.get(randIndexUser).get_id()+" s'est connecté");
			}
		};

		Timer timer = new Timer();
		Random rand = new Random();
		
		while (true) {
		timer.scheduleAtFixedRate(taskDeconnex, 5, rand.nextInt((10000 - 2000) + 2000) + 1);
		timer.scheduleAtFixedRate(taskConnex, 5, rand.nextInt((10000 - 2000) + 2000) + 1);
		timer.scheduleAtFixedRate(taskaddLoves, 5, rand.nextInt((10000 - 2000) + 2000) + 1);
		}
	}


}
