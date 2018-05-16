package dao.mongo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import dao.mongo.entity.SuiviCours;

@Repository
public class SuiviCoursService  {
	
	@Autowired
	MongoOperations mongoOps;
	
	@Autowired
	public SuiviCoursService(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}

	
	public SuiviCours getSuiviCoursByID(Integer id){
		Query query = new Query(Criteria.where("_id").is(id));
		return  mongoOps.findOne(query, SuiviCours.class);
	}
	
	public List<SuiviCours> getAllSuiviCours() {
		return mongoOps.findAll(SuiviCours.class);
	}

	public List<SuiviCours> getSuiviCoursByIdCours(Integer id) {
		Query query = new Query(Criteria.where("cours").is(id));
		return mongoOps.find(query, SuiviCours.class);
	}



}