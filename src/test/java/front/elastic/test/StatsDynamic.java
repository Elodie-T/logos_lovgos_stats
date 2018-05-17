package front.elastic.test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.concurrent.ExecutionException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.mongo.services.LovesService;
import dao.mongo.services.SessionService;
import dao.mongo.services.UsersService;
import front.elastic.services.ManageCalculLoveConnex;
import front.elastic.services.ManageConnexion;
import front.elastic.services.ManageUsers;
import front.elastic.services.StatsDynamique;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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

public class StatsDynamic {
	
	static List<User> usersConnectes = new ArrayList<User>();
	static List<User> usersDeConnectes = new ArrayList<User>();
	static List<String> plateforme = Arrays.asList("Logos", "Lovegos");
	static StatsDynamique sd;
	static LovesService lovesService;
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		ManageUsers m = new ManageUsers();
		ManageConnexion c = new ManageConnexion();
		ManageCalculLoveConnex lc = new ManageCalculLoveConnex();
		sd = new StatsDynamique();
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		UsersService usersService = ctx.getBean(UsersService.class);
		SessionService sessionService = ctx.getBean(SessionService.class);
		lovesService = ctx.getBean(LovesService.class);


		
		//ajouter un love
//		List<Loves> listeLove = lovesService.getAllLoves();
//		Loves love = new Loves(listeLove.size()+1, LocalDate.now(), false, 11220, 13544);
//		sd.addLove(love);
		
		
		// connecter un user à une plateforme
//		Geolocalisation geoLoc = new Geolocalisation(12.54, 3.0);
//		sd.addUserSessionById(3, "logos", geoLoc);
		
		
		// déconnecter un user by id
//		sd.deconnectionUserById(3);
		
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
					sd.deconnectionUserById(usersConnectes.get(randIndexUser).get_id());
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
						sd.addLove(love);
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
						sd.addLove(love);
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
					sd.addUserSessionById(usersDeConnectes.get(randIndexUser).get_id(),plateforme.get(randIndexPlateforme),geoLoc);
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


