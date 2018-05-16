package front.elastic.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
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
import dao.mongo.services.LovesService;
import dao.mongo.services.SessionService;

public class StatsDynamique {

	private TransportClient client;
	private SessionService sessionService;
	private LovesService lovesService;
	ManageUsers m;
	ManageConnexion c;
	ManageCalculLoveConnex lc;

	@SuppressWarnings("resource")
	public StatsDynamique() throws IOException {
		
		 m = new ManageUsers();
		 c = new ManageConnexion();
		lc = new ManageCalculLoveConnex();

		// se connecter à mongoDB
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		sessionService = ctx.getBean(SessionService.class);
		lovesService = ctx.getBean(LovesService.class);
		

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
		System.out.println("dureeSessionDeconnectees : "+dureeSessionDeconnectees);
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
		
	}
	
	public void addLove(Loves love) throws IOException, InterruptedException, ExecutionException {
		// ajout en BDD
		lovesService.addLove(love);
		
		//augmenter le nbr de love dans elasticSearch
		
				// on récupère la durée moyenne sur ElasticSearch
				int nbrLoveUpdate=0;
				GetResponse getResponse = client.prepareGet(lc.getIndex(),lc.getType(),lc.getId_unique().toString()).execute().actionGet();
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




}
