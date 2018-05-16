package front.elastic.test;

import java.io.IOException;
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

public class StatsDynamic {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		ManageUsers m = new ManageUsers();
		ManageConnexion c = new ManageConnexion();
		ManageCalculLoveConnex lc = new ManageCalculLoveConnex();
		StatsDynamique sd = new StatsDynamique();
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		UsersService usersService = ctx.getBean(UsersService.class);
		SessionService sessionService = ctx.getBean(SessionService.class);
		LovesService lovesService = ctx.getBean(LovesService.class);


		
		//ajouter un love
		List<Loves> listeLove = lovesService.getAllLoves();
		Loves love = new Loves(listeLove.size()+1, LocalDate.now(), false, 11220, 13544);
		sd.addLove(love);
		
		
		// connecter un user à une plateforme
		Geolocalisation geoLoc = new Geolocalisation(12.54, 3.0);
		sd.addUserSessionById(3, "logos", geoLoc);
		
		
		// déconnecter un user by id
		sd.deconnectionUserById(3);
		
		
		
		
		
	}


























}


