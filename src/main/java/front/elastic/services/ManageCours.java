package front.elastic.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import dao.mongo.entity.ConnectionUsers;
import dao.mongo.entity.Cours;
import dao.mongo.entity.Session;
import dao.mongo.entity.Sessions;
import dao.mongo.entity.SuiviCours;
import dao.mongo.entity.User;
import dao.mongo.services.SessionService;
import dao.mongo.services.SuiviCoursService;
import front.elastic.users.ElevesLovegos;
import front.elastic.users.HistoriqueConnex;
import front.elastic.users.Utilisateur;

public class ManageCours {
	
	private TransportClient client;
	private String index = "cours";
	private String type = "default";
	private SuiviCoursService suiviCoursService;

	@SuppressWarnings({ "resource", "unchecked" })
	public ManageCours() throws IOException {

		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		suiviCoursService = ctx.getBean(SuiviCoursService.class);


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
	
	public void addCours(Cours cours) throws IOException {
		XContentBuilder xb =  XContentFactory.jsonBuilder().startObject();
		List<SuiviCours> listeCours = suiviCoursService.getSuiviCoursByIdCours(cours.get_id());
		xb.field("titre", cours.getTitre())
		.field("langue", cours.getNiveau().getLangue().getLibelle())
		.field("nbInscrits", listeCours.size());
		xb.endObject();
		client.prepareIndex(index,type, cours.get_id().toString()).setSource(xb).get();
	}
	
}