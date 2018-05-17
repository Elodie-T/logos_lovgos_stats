package front.elastic.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.LocalDateTime;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.mongo.entity.ConnectionUsers;
import dao.mongo.entity.Geolocalisation;
import dao.mongo.entity.Loves;
import dao.mongo.entity.Session;
import dao.mongo.entity.SessionLibelle;
import dao.mongo.entity.User;
import dao.mongo.services.LovesService;
import dao.mongo.services.SessionService;
import dao.mongo.services.UsersService;
import front.elastic.services.ManageCalculLoveConnex;
import front.elastic.services.ManageConnexion;
import front.elastic.services.ManageUsers;
import front.elastic.services.StatsDynamique;
import front.elastic.users.ElevesLovegos;
import front.elastic.users.HistoriqueConnex;

public class StatElasticSearch {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		ManageUsers m = new ManageUsers();
		ManageConnexion c = new ManageConnexion();
		ManageCalculLoveConnex lc = new ManageCalculLoveConnex();
		StatsDynamique sd = new StatsDynamique();
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		UsersService usersService = ctx.getBean(UsersService.class);
		SessionService sessionService = ctx.getBean(SessionService.class);
		LovesService lovesService = ctx.getBean(LovesService.class);
		
		lc.addFAKElovecalcul();
		
		sd.getdureeMoyenne();
		
		
//		 double nbrVisiteMoyenParJourUpdate = (double) 125/855;
//		 System.out.println(nbrVisiteMoyenParJourUpdate);
//		List<Loves> listeLove = lovesService.getAllLoves();
//		Integer idLove = listeLove.size()+1;
//		Integer id_exp = 11220;
//		Integer id_dest = 13544;
//		Boolean vuParExp = false;
//		Loves love = new Loves(listeLove.size()+1, LocalDate.now(), false, 11220, 13544);
//		sd.addLove(love);
		
//		Geolocalisation geoLoc = new Geolocalisation(12.54, 3.0);
//		sd.addUserSessionById(3, "logos", geoLoc);
//		
//		ConnectionUsers cu =  sessionService.getConnectionsByUserID(3);
//		List<SessionLibelle> sl = cu.getSessions();
//		for(SessionLibelle s : sl) {
//			System.out.println(s.getSession());
//		}
		
//		sd.deconnectionUserById(3);
//		List<User> liste = usersService.getAllUsers();
//		
//		List<User> liste1 = new ArrayList<User>();
//		liste1.add(usersService.getUserByID(3));
//		liste1.add(usersService.getUserByID(4802));
//		liste1.add(usersService.getUserByID(14500));
//		liste1.add(usersService.getUserByID(13544));
//		liste1.add(usersService.getUserByID(12555));
//		liste1.add(usersService.getUserByID(13101));
//		liste1.add(usersService.getUserByID(11544));
//		liste1.add(usersService.getUserByID(13196));
//		for(User u: liste1) {
//			System.out.println(u);
//			m.addUser(u);
//			m.deleteUtilisateur(u.get_id());
//		}
//		sd.deconnectionUserById(1288);	
//		System.out.println(LocalDateTime.now());
		
		
//		List<String> response = lc.updateDureeConnexMoyen();
//		System.out.println("taille :"+response.size());
//		for(String s : response) {
//			System.out.println(s);
//		}
		
		
		
		
//		User u = usersService.getUserByID(13196);
//		String connection = m.getUserConnection(u);
//		System.out.println(connection);
		
//		List<HistoriqueConnex> listeConnex = c.getNbrConnectionAllDates();
//		for(HistoriqueConnex h : listeConnex) {
//			System.out.println(h);
//		c.addHistoriqueConnexion(h);
//		}
		
//		lc.addCalculLoveConnex(listeConnex);
//		lc.addlove();
//		double moyConnex = lc.updateDureeConnexMoyen();
//		System.out.println(moyConnex);
		
//		System.out.println(lc.getDureeConnexMoyen());
//		
//		System.out.println(lc.getNbrConnectionMoyen(listeConnex));

		
//		// UPDATE DOCUMENT
//		UpdateRequest updateRequest = new UpdateRequest();
//		updateRequest.index("eleves_lovegos2");
//		updateRequest.type("eleves");
//		updateRequest.id("1");
//		updateRequest.doc(XContentFactory.jsonBuilder()
//				.startObject()
//				.field("connection", "logos")
//				.endObject());
//		client.update(updateRequest).get();
//
//		GetResponse response1 = client.prepareGet("eleves_lovegos2", "eleves", "1").get();
//		System.out.println(response1);
//		

	}

}
