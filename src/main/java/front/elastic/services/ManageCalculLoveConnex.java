package front.elastic.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import dao.mongo.entity.ConnectionUsers;
import dao.mongo.entity.Session;
import dao.mongo.entity.Sessions;
import dao.mongo.entity.User;
import dao.mongo.services.LovesService;
import dao.mongo.services.SessionService;
import front.elastic.users.Eleves;
import front.elastic.users.ElevesLovegos;
import front.elastic.users.HistoriqueConnex;

public class ManageCalculLoveConnex {

	private TransportClient client;
	private String index = "nbr_love_connex";
	private String type = "default";
	private Integer id_unique = 1;
	private SessionService sessionService;
	private LovesService lovesService;
	private ManageConnexion manageConnex;

	@SuppressWarnings({ "resource", "unchecked" })
	public ManageCalculLoveConnex() throws IOException {

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

	///////////////////////////////////////////////////////////////////////////////////////
	public void addCalculLoveConnex(List<HistoriqueConnex> listeConnex) throws IOException {
		XContentBuilder xb =  XContentFactory.jsonBuilder().startObject();
		xb.field("id",id_unique)
		.field("moyCon", getDureeConnexMoyen())
		.field("nbLoves", lovesService.getAllLoves().size())
		.field("nbVisites", getNbrConnectionMoyen(listeConnex));
		xb.endObject();
		client.prepareIndex(index,type).setSource(xb).get();

	}
	
//	public void addlove() throws IOException {
//		XContentBuilder xb =  XContentFactory.jsonBuilder().startObject();
//		xb.field("moyCon",11303)
//		.field("nbLoves", 30000)
//		.field("nbVisites", 1812);
//		xb.endObject();
//		client.prepareIndex(index,type,"1").setSource(xb).get();
//	}


	public Double getNbrConnectionMoyen(List<HistoriqueConnex> connex) {
		double numerateur=0;
		double denominateur = connex.size();
		for(HistoriqueConnex c : connex) {
			numerateur+=c.getNbConnections();
		}
		return numerateur/denominateur;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	public double getDureeConnexMoyen() {
		long numerateur=0;
		double denominateur=0;
		List<ConnectionUsers> allSessions = sessionService.getAllUserConnections();
		List<HistoriqueConnex> historiqueConnexions =  new ArrayList<HistoriqueConnex>();
				LocalDate startDate = sessionService.getDateConnexionMin();
//		LocalDate startDate = LocalDate.of(2018, 05, 10);
		LocalDate endDate = sessionService.getDateConnexionMax();

		for(LocalDate date= startDate; date.isBefore(endDate); date = date.plusDays(1)) {
			List<Session> sessionsByDate = sessionService.getAllSessionByDate(date);
			for(Session s: sessionsByDate) {
				if(s.getDateDeconnexion() != null) {
					Duration d = getDurationBySession(s);
					numerateur += d.getSeconds();
					denominateur += 1;
				}
			}

		}
		System.out.println("numerateur : "+numerateur+" denominateur : "+denominateur);
		return numerateur/denominateur;
	}
	

	

	public Duration getDurationBySession(Session s) {
		Duration duration = Duration.between(s.getDateConnexion(), s.getDateDeconnexion());
		return duration;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getId_unique() {
		return id_unique;
	}

	public void setId_unique(Integer id_unique) {
		this.id_unique = id_unique;
	}




}
