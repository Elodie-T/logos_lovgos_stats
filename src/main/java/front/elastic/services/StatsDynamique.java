package front.elastic.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import dao.mongo.entity.Session;
import dao.mongo.entity.SessionLibelle;
import dao.mongo.entity.Sessions;
import dao.mongo.services.SessionService;

public class StatsDynamique {

	private TransportClient client;
	private SessionService sessionService;
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
	
	public void addUserSessionById(Integer id, String plateforme, Geolocalisation geoLoc) {
		Session session = new Session(plateforme, LocalDateTime.now(), null, geoLoc);
		SessionLibelle sessionLibelle = new SessionLibelle(session);
		ConnectionUsers cu = sessionService.getConnectionsByUserID(id);
		int nbrSession = cu.getSessions().getSessionLibelle().size();
		for(SessionLibelle s : cu.getSessions().getSessionLibelle()) {
			System.out.println(s);
		}
		cu.getSessions().getSessionLibelle().add(sessionLibelle);
		sessionService.addSessionToUser(cu);
	}

	private void updateDureeConnexMoyen(Double dureeSessionDeconnectees) throws IOException, InterruptedException, ExecutionException {
		
		// on récupère la durée moyenne sur ElasticSearch
		double moyConnex=0;
		GetResponse getResponse = client.prepareGet(lc.getIndex(),lc.getType(), "1").execute().actionGet();
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
