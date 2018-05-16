package front.elastic.test;

import java.io.IOException;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.mongo.entity.Cours;
import dao.mongo.entity.User;
import dao.mongo.services.CoursService;
import dao.mongo.services.SuiviCoursService;
import dao.mongo.services.UsersService;
import front.elastic.services.ManageCalculLoveConnex;
import front.elastic.services.ManageConnexion;
import front.elastic.services.ManageCours;
import front.elastic.services.ManageUsers;
import front.elastic.users.HistoriqueConnex;

public class StatsElasticFinal {

	public static void main(String[] args) throws IOException {
		ManageUsers m = new ManageUsers();
		ManageConnexion c = new ManageConnexion();
		ManageCalculLoveConnex lc = new ManageCalculLoveConnex();
		ManageCours mcours = new ManageCours();
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("mongo-context.xml");
		UsersService usersService = ctx.getBean(UsersService.class);
		CoursService coursService = ctx.getBean(CoursService.class);
		SuiviCoursService suiviCoursService = ctx.getBean(SuiviCoursService.class);
		
		
		//ajouter tous les utilisateurs (tous types confondus) : création de l'index users
//		List<User> liste = usersService.getAllUsers();
//		for(User u: liste) {
//			System.out.println(u);
//			m.addUser(u);
//		}
//		
//		//ajouter tous les historiques de connexions : création de l'index connex_historique
//		List<HistoriqueConnex> listeConnex = c.getNbrConnectionAllDates();
//		for(HistoriqueConnex h : listeConnex) {
//			System.out.println(h);
//			c.addHistoriqueConnexion(h);
//		}
//		
//		// ajouter le nbr de loves total, le nbr moyen de visite par jour et la durée moyenne de connexion : création de l'index nbr_love_connex
//		lc.addCalculLoveConnex(listeConnex);
			
		//ajouter tous les cours : creation de l'index cours
				List<Cours> listeCours = coursService.getAllCours();
				for(Cours cours : listeCours) {
					System.out.println(cours);
					mcours.addCours(cours);
				}
		
		// si besoin de supprimer un user selon son id
//			m.deleteUtilisateur(u.get_id());
		
		
			
			
			
	}

}
