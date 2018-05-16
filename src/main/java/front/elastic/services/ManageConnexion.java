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
import dao.mongo.entity.Session;
import dao.mongo.entity.Sessions;
import dao.mongo.entity.User;
import dao.mongo.services.SessionService;
import front.elastic.users.ElevesLovegos;
import front.elastic.users.HistoriqueConnex;

public class ManageConnexion {
	
	private TransportClient client;
	private String index = "connex_historique";
	private String type = "default";
	private SessionService sessionService;

	@SuppressWarnings({ "resource", "unchecked" })
	public ManageConnexion() throws IOException {

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
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	public List<HistoriqueConnex> getNbrConnectionAllDates(){
		List<ConnectionUsers> allSessions = sessionService.getAllUserConnections();
		List<HistoriqueConnex> historiqueConnexions =  new ArrayList<HistoriqueConnex>();
		LocalDate startDate = sessionService.getDateConnexionMin();
//		LocalDate startDate = LocalDate.of(2018, 05, 10);
		LocalDate endDate = sessionService.getDateConnexionMax();
		
		for(LocalDate date= startDate; date.isBefore(endDate); date = date.plusDays(1)) {
			List<Session> sessionsByDate = sessionService.getAllSessionByDate(date);
			HistoriqueConnex h = new HistoriqueConnex(date, sessionsByDate.size());
			historiqueConnexions.add(h);
		}
		return historiqueConnexions;
		
	}
	
	public void addHistoriqueConnexion(HistoriqueConnex u) throws IOException {
		XContentBuilder xb =  XContentFactory.jsonBuilder().startObject();
		xb.field("dateJour", u.getDateJour())
		.field("nbConnections", u.getNbConnections());
		xb.endObject();
		client.prepareIndex(index,type).setSource(xb).get();

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
	
	

	
	
	
	
}
