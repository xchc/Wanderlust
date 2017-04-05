package com.wanderlust.api;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.wanderlust.util.DoubleKeyPair;

import org.bson.types.ObjectId;

public class ExtendedEventService {

	ObjectId id;

	public static DBObject showEventInfo (String eventid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject dbObj = dbc.findOne(query);
		return dbObj;
	}

	public static BasicDBObject targetEvent(String eventid){
		BasicDBObject query = new BasicDBObject();      
		query.put("_id", eventid);
		return query;
	}

	public static DBObject updateEventLocation (String eventid, String Location) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("loc", Location));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateEventName (String eventid, String Name) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("en", Name));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}


	public static void removeEvent(String eventid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		dbc.remove(query);
	}

	public static DBObject updateEventAbout (String eventid, String About) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("Abt", About));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateEventLanguage (String eventid, String Language) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("lng", Language));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateEventAuthor (String eventid, String Author) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("auth", Author));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateEventCoord (String eventid, List<String> latlon) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$set", new BasicDBObject("coord", latlon));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}

	public static DBObject updateEventLanguages(String eventid, String Languages) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$push", new BasicDBObject("lngs", Languages));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}


	public static ArrayList<String> showEventMembers(String eventid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		BasicDBObject field = new BasicDBObject();
		field.put("mmb", 1);
		ArrayList<String> result = new ArrayList<String>();
		DBCursor cursor = dbc.find(query,field);
		while (cursor.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor.next();
			result.add(obj.getString("mmb"));
		}
		return result;
	}

	public static DBObject updateEventMember(String eventid, String userid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$push", new BasicDBObject("mmb", userid));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}


	public static void removeEventMember(String eventid, String userid) throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$pull", new BasicDBObject("mmb", userid));	
		dbc.update(query, update);
	}


	public static DBCollection initializeDB () throws UnknownHostException {
		Mongo mongo = new MongoClient("localhost", 27017);
		DB db = mongo.getDB("wanderlustTest4");
		DBCollection dbc = null ;
		dbc = db.getCollection(Event.COLLECTION_NAME);
		return dbc;
	}


	public static DBCollection testInitializeDB (String DBname, String CollectionName) throws UnknownHostException {
		Mongo mongo = new MongoClient("localhost", 27017);
		DB db = mongo.getDB(DBname);
		DBCollection dbc = null ;
		dbc = db.getCollection(CollectionName);
		return dbc;
	}

	public static void TestRemoveEventMember(String eventid, String userid) throws UnknownHostException {
		DBCollection dbc = testInitializeDB("testWanderlust","events");
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$pull", new BasicDBObject("mmb", userid));	
		dbc.update(query, update);
	}
	
	public static ArrayList<DBObject> showEventInform() throws UnknownHostException {
		DBCollection dbc = initializeDB();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject fno = new BasicDBObject();
		ArrayList<DBObject> list =  new ArrayList<DBObject>();
		fno.put("coord", 1);
		DBCursor cursor = dbc.find(query, fno);
		while(cursor.hasNext()) {
			list.add(cursor.next());
		}
		return list;
	}
	
	public static DBObject TestUpdateEventMember(String eventid, String userid) throws UnknownHostException {
		DBCollection dbc = testInitializeDB("testWanderlust","events");
		BasicDBObject query = targetEvent(eventid);
		DBObject update = new BasicDBObject();
		update.put("$push", new BasicDBObject("mmb", userid));	
		DBObject dbObj = dbc.findAndModify(query, update);
		return dbObj;
	}
	
	/* 	public static void main(String [ ] args) throws UnknownHostException
	 
	{
		DBCollection dbc = testInitializeDB("testWanderlust","events");
		//dbc.getCollection("events").count(new Document("_id", 10))
		System.out.println(dbc.count(TestUpdateEventMember("new_event", "test_user")));
		//dbc.count(TestUpdateEventMember("new_event", "test_user"));
		//TestRemoveEventMember("new_event", "test_user");

	}
	*/

}