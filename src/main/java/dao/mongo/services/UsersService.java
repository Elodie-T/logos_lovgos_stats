package dao.mongo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import dao.mongo.entity.LogosLovgosConnection;
import dao.mongo.entity.LovgosMotif;
import dao.mongo.entity.User;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

@Repository
public class UsersService  {
	
	@Autowired
	MongoOperations mongoOps;
	
	@Autowired
	public UsersService(MongoOperations mongoOps) {
		this.mongoOps = mongoOps;
	}

	
	public User getUserByID(String id){
		Query query = new Query(Criteria.where("_id").is(id));
		return  mongoOps.findOne(query, User.class);
	}


	public void updateUserConnection(User user, String connect) {
		user.setConnection(connect);
		mongoOps.save(user);
	}


	
	public void createUser(User user) {
		mongoOps.insert(user);
	}
	
	public void deleteUser(User user) {
		Query query = new Query(Criteria.where("_id").is(user.get_id()));
		mongoOps.remove(query, User.class);
	}



}
