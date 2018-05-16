package front.elastic.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONObject;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.mongo.entity.ConnectionUsers;
import dao.mongo.entity.Session;
import dao.mongo.entity.SessionLibelle;
import dao.mongo.entity.User;
import dao.mongo.services.SessionService;
import dao.mongo.services.UsersService;
import front.elastic.users.Eleves;
import front.elastic.users.ElevesLovegos;
import front.elastic.users.Utilisateur;
import io.netty.handler.codec.json.JsonObjectDecoder;

public class ManageUsers {

	private TransportClient client;
	private String index = "users";
	private String type = "user";
	private SessionService sessionService;

	@SuppressWarnings({ "resource", "unchecked" })
	public ManageUsers() throws IOException {

		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		sessionService = ctx.getBean(SessionService.class);


		// se connecter à Elastic Search
		try {
			client= new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
					.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9301));

			// on indique que les coordonnées géographiques sont de type geo_point
			String url = "http://localhost:9200/"+index;
			if(new DefaultHttpClient().execute(new HttpGet(url)).getStatusLine().getStatusCode()==404) {
				HttpClient client1 = new DefaultHttpClient();
				HttpPut put = new HttpPut(url);
				put.setHeader("Content-Type","application/json");
				StringEntity entity = new StringEntity("{\"mappings\": {\"user\": {\"properties\": {\"geoLocation\": {\"type\": \"geo_point\"}}}}}");
				put.setEntity(entity);
				HttpResponse response1= client1.execute(put);
				System.out.println(response1.getStatusLine().getStatusCode());

				BufferedReader rd = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while((line=rd.readLine()) != null) {
					result.append(line);
				}
				System.out.println(result.toString());
			}
		} catch (UnknownHostException e) {
			System.out.println("Erreur de connection à Elastic Search");
			e.printStackTrace();
		}

	}

	public void addUser(User u) throws IOException {
		if(u.getType().equalsIgnoreCase("eleve")) {
			addEleve(u);
		} else if (u.getType().equalsIgnoreCase("eleveLovegos")) {
			addElevesLovegos(u);
		}else {
			addProf(u);
		}
	}

	public void deleteUtilisateur(Integer id) {
		client.prepareDelete(index, type,id.toString()).get();
	}



	//méthodes pour ajouter un user quel que soit son type
	private void addElevesLovegos(User user) throws IOException {
		ElevesLovegos u = convertToEleveLovegosElastic(user);
		XContentBuilder xb =  XContentFactory.jsonBuilder().startObject();
		xb.field("connection", u.getConnection())
		.field("type", u.getType())
		.field("age", u.getAge())
		.field("genre",u.getGenre())
		.field("geoLocation",u.getGeoLoc())
		.field("statutPremium",u.isStatutPremium());

		xb.startArray("motifs");
		for (String o : u.getMotifs()) {
			xb.value(o);
		}
		xb.endArray();

		xb.endObject();
		client.prepareIndex(index,type, u.getId().toString()).setSource(xb).get();

	}

	private void addEleve(User user) throws IOException {
		Eleves e = convertToEleveElastic(user);
		XContentBuilder xb =  XContentFactory.jsonBuilder().startObject();
		xb.field("connection", e.getConnection())
		.field("type", e.getType())
		.field("age", e.getAge())
		.field("genre",e.getGenre())
		.field("geoLocation",e.getGeoLoc())
		.field("statutPremium",e.isStatutPremium());
		xb.endObject();
		client.prepareIndex(index,type, e.getId().toString()).setSource(xb).get();

	}

	private void addProf(User user) throws IOException {
		Utilisateur prof = convertToProfElastic(user);
		XContentBuilder xb =  XContentFactory.jsonBuilder().startObject();
		xb.field("connection", prof.getConnection())
		.field("type", prof.getType())
		.field("age", prof.getAge())
		.field("genre",prof.getGenre())
		.field("geoLocation",prof.getGeoLoc());
		xb.endObject();
		client.prepareIndex(index,type, prof.getId().toString()).setSource(xb).get();
	}

	
	private Utilisateur convertToProfElastic(User user) {
		GeoPoint geo = new GeoPoint(user.getGeoLoc().getLat(),user.getGeoLoc().getLon());
		int age = calculateAge(user.getDateNaissance(),LocalDate.now());
		return new Utilisateur(user.get_id(), getUserConnection(user), user.getType(), age, user.getGenre(), geo);
	}

	private Eleves convertToEleveElastic(User user) {
		GeoPoint geo = new GeoPoint(user.getGeoLoc().getLat(),user.getGeoLoc().getLon());
		int age = calculateAge(user.getDateNaissance(),LocalDate.now());
		return new Eleves(user.get_id(), getUserConnection(user), user.getType(), age, user.getGenre(), geo, user.getStatutPremium());
	}

	private ElevesLovegos convertToEleveLovegosElastic(User user) {
		List<String> motifs = new ArrayList<String>();
		for (int i=0;i< user.getMotif().length;i++) {
			motifs.add(user.getMotif()[i]);
		}
		GeoPoint geo = new GeoPoint(user.getGeoLoc().getLat(),user.getGeoLoc().getLon());
		int age = calculateAge(user.getDateNaissance(),LocalDate.now());
		return new ElevesLovegos(user.get_id(), getUserConnection(user), user.getType(), age, user.getGenre(), geo, user.getStatutPremium(),motifs);
	}

	///////////////////////////////////////////////////////////////////////////////////////
	public String getUserConnection(User u) {
		ConnectionUsers sessions = sessionService.getConnectionsByUserID(u.get_id());
		boolean connected = false;
		if(sessions != null) {
			for(int i=0;i< sessions.getSessions().size();i++) {
				SessionLibelle listeSessions = sessions.getSessions().get(i) ;
				if(listeSessions.getSession().getDateDeconnexion() == null) {
					connected = true;
					for(int j=i+1;j< sessions.getSessions().size();j++) {
						SessionLibelle listeSessionsSuite = sessions.getSessions().get(j);
						if(listeSessionsSuite.getSession().getDateDeconnexion() == null 
								&& !listeSessions.getSession().getPlateforme().equalsIgnoreCase(listeSessionsSuite.getSession().getPlateforme())) {
							return "both";
						}
					}
					return listeSessions.getSession().getPlateforme();
				}
			}
		}
		if(!connected) {
			return "none";
		}
		return "";
	}

	private static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
		if ((birthDate != null) && (currentDate != null)) {
			return Period.between(birthDate, currentDate).getYears();
		} else {
			return 0;
		}
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
