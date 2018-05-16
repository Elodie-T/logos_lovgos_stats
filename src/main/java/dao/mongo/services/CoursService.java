package dao.mongo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import dao.mongo.entity.Cours;

@Repository
public class CoursService  {
	
	@Autowired
	MongoOperations mongoOps;
	
	@Autowired
	public CoursService(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}

	
	public Cours getCoursByID(Integer id){
		Query query = new Query(Criteria.where("_id").is(id));
		return mongoOps.findOne(query, Cours.class);
	}
	
	public List<Cours> getAllCours() {
		return mongoOps.findAll(Cours.class);
	}

}