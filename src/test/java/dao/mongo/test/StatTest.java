package dao.mongo.test;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.joda.time.Duration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.mongo.entity.ConnectionUsers;
import dao.mongo.entity.Loves;
import dao.mongo.entity.Session;
import dao.mongo.entity.User;
import dao.mongo.services.LovesService;
import dao.mongo.services.SessionService;
import dao.mongo.services.UsersService;
import front.elastic.services.ManageConnexion;
import front.elastic.users.HistoriqueConnex;


public class StatTest {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException  {
				ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
				UsersService usersService = ctx.getBean(UsersService.class);
				SessionService usersConnection = ctx.getBean(SessionService.class);
				LovesService lovesService = ctx.getBean(LovesService.class);
				
				ManageConnexion mc = new ManageConnexion();
				
				Loves love = lovesService.getLovesByID(4);
				System.out.println(love);
				
				
				
//				ConnectionUsers c = usersConnection.getConnectionsByUserID(14500);
//				System.out.println(c);
				
//				List<ConnectionUsers> liste = usersConnection.getAllUserConnections();
//				for(ConnectionUsers u: liste) {
//					System.out.println(u);
//				}
				
//				LocalDate d = LocalDate.of(2017,02,25);
//				List<Session> sessions = usersConnection.getAllSessionByDate(d);
//				for(Session s :  sessions) {
//					System.out.println(s);
//				}
				
				
//				List<HistoriqueConnex> historiqueConnexions =  new ArrayList<HistoriqueConnex>();
//				historiqueConnexions = mc.getNbrConnectionAllDates();
//				for(HistoriqueConnex h : historiqueConnexions) {
//					System.out.println(h);
//				}
				
//				System.out.println(usersConnection.getDateConnexionMin());
//				System.out.println(usersConnection.getDateConnexionMax());
				
				
//				LocalDateTime date = LocalDateTime.of(2017,02,25,15,11,22);
//				LocalDate d1 = LocalDate.of(date.getYear(),date.getMonth(),date.getDayOfMonth());
//				System.out.println((d1));
				
				
				
		//		List<String> coordinates = new ArrayList<String>();
		//		coordinates.add("15.45");
		//		coordinates.add("16.58");
		//		
		//		List<String> motifs = new ArrayList<String>();
		//		motifs.add(LovgosMotif.Amuser.getLovgosMotif());
		//		motifs.add(LovgosMotif.PratiquerLangues.getLovgosMotif());
		//		
		//		User u1 = new User(LogosLovgosConnection.Logos.getLogosLovgosConnection(), UserType.Student.getUserType()
		//				, "20/03/2001", Gender.Male.getGender(), LovgosInscription.Inscrit.getLovgosInscription()
		//				, coordinates, motifs, "19");
		//		
		////		usersService.createUser(u1);
		//		
//			User u2 = usersService.getUserByID(14500);
//			System.out.println(u2);
//			
//			List<User> liste = usersService.getAllUsers();
//			for(User u: liste) {
//				System.out.println(u);
//			}

		//	usersService.updateUserConnection(u2,LogosLovgosConnection.NotConnected.getLogosLovgosConnection());

		//	usersService.deleteUser(u2);
				

	}
}
