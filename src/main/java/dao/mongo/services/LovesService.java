package dao.mongo.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import dao.mongo.entity.Loves;
import dao.mongo.entity.User;


@Repository
public class LovesService  {
	
	@Autowired
	MongoOperations mongoOps;
	
	@Autowired
	public LovesService(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}

	
	public Loves getLovesByID(Integer id){
		Query query = new Query(Criteria.where("idLove").is(id));
		return  mongoOps.findOne(query, Loves.class);
	}
	
	public List<Loves> getAllLoves(){
		return mongoOps.findAll(Loves.class);
	}
	
	public void addLove(Loves love) {
		mongoOps.save(love);
	}
	



}
