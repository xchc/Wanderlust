package com.wanderlust.api;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;

public class ExtendedUserService {

	ObjectId id;

	public static DBObject showUserInfo (String userid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject dbObj = dbc.findOne(query);
		return dbObj;
	}

	public static DBObject showUserRequests (String userid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject dbObj = dbc.findOne(query, new BasicDBObject("fr",1));
		return dbObj;
	}

	public static DBObject showUserLanguages (String userid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject dbObj = dbc.findOne(query, new BasicDBObject("lng",1));
		return dbObj;
	}
	
	public static DBObject showUserInterests (String userid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject dbObj = dbc.findOne(query, new BasicDBObject("int",1));
		return dbObj;
	}
	
	public static BasicDBObject targetUser(String userid){
		BasicDBObject query = new BasicDBObject();      
		query.put("_id", userid);
		return query;
	}
	
	
	public static ArrayList<DBObject> showUserFn() throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject fno = new BasicDBObject();
		ArrayList<DBObject> list =  new ArrayList<DBObject>();
		fno.put("fn", 1);
		fno.put("ln", 1);
		fno.put("coord", 1);
		DBCursor cursor = dbc.find(query, fno);
		while(cursor.hasNext()) {
			list.add(cursor.next());
		}
		return list;
	}

	public static DBObject updateUserAge (String userid, String Age) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("age", Age));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}
	
	
	public static DBObject updateUserCoord (String userid, List<String> latlon) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("coord", latlon));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateUserFName (String userid, String FirstName) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("fn", FirstName));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateUserLName (String userid, String LastName) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("ln", LastName));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateUserAbout (String userid, String About) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("Abt", About));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}


	public static DBObject updateUserLanguage (String userid, List<String> Language) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$addToSet", new BasicDBObject("lng", Language));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateUserOrigin (String userid, String Origin) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("org", Origin));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}


	public static void updateFriendStatus (String senderId, String receiverId, Boolean status) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(receiverId);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject(senderId, status));
		DBObject update2 = new BasicDBObject();
		update2.put("$addToSet", new BasicDBObject("fr", senderId));	
		dbc.findAndModify(query, update);
		dbc.findAndModify(query, update2);
	}

	public static void removeRequestFromProfile (String senderId, String receiverId) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(receiverId);
		DBObject update = new BasicDBObject();
		update.put("$unset", new BasicDBObject(senderId, ""));	
    	dbc.update(query, update);	
	}

	public static void removeListRequestFromProfile (String senderId, String receiverId) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(receiverId);
		DBObject update = new BasicDBObject();
		update.put("$pull", new BasicDBObject("fr", senderId));	
    	dbc.update(query, update);	
	}
	

	public static Boolean checkIfRequestExist (String senderId, String receiverId) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		DBObject query = new BasicDBObject(senderId, new BasicDBObject( "$ne", "").append("$exists", true)).append("_id", receiverId);
		DBCursor result = dbc.find(query);
		if (result.size() > 0) {return true;}
		else {return false;}
	}


	public static DBObject updateUserInterests (String userid, List<String> interests) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$addToSet", new BasicDBObject("int", interests));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateUserPlaces(String userid, List<String> Places) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetUser(userid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("plc", Places));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBCollection initializeDB () throws UnknownHostException {
		Mongo mongo = new MongoClient("localhost", 27017);
		DB db = mongo.getDB("wanderlustTest4");
		DBCollection dbc = null ;
		dbc = db.getCollection(User.COLLECTION_NAME);
		return dbc;
	}
}