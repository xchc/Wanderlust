package com.wanderlust.users;
import com.mongodb.*;
import com.wanderlust.MongoBackedService;
import com.wanderlust.api.SubscribersCount;
import com.wanderlust.api.SubscribingCount;
import com.wanderlust.api.Event;
import com.wanderlust.api.GeneralError;
import com.wanderlust.api.ServiceException;
import com.wanderlust.api.User;
import com.wanderlust.api.UserServiceError;
import com.wanderlust.configuration.DefaultUserServiceConfiguration;
import com.wanderlust.services.ServiceImplementation;
import com.wanderlust.services.UserService;
import com.yammer.dropwizard.config.Configuration;

import java.util.List;
import java.util.ArrayList;

import org.bson.types.ObjectId;

@ServiceImplementation(name = "DefaultUserService", configClass = DefaultUserServiceConfiguration.class)
public class DefaultUserService 
    extends MongoBackedService implements UserService {

    private static final String USER_ID_KEY = "_id";
    private static final String RIGHT_KEY = "_f";
    private static final String LEFT_KEY = "_t";
    private static final String SUBSCRIBER_COUNT_KEY = "_cr";
    private static final String SUBSCRIBING_COUNT_KEY = "_cg";
    
    private static final BasicDBObject SELECT_USER_ID = 
    		new BasicDBObject(USER_ID_KEY, 1);

    private final DBCollection users;
    private DBCollection subscribers = null;
    private DBCollection subscribing = null;

    private final DefaultUserServiceConfiguration config;
    private final UserVerifier userValidator;

	public DefaultUserService(final MongoClientURI dbUri, final DefaultUserServiceConfiguration svcConfig ) {
        super(dbUri, svcConfig);
        
        this.config = svcConfig;  	
        this.users = this.database.getCollection(config.user_collection_name);
        this.userValidator = new BasicUserIdVerifier();

        if(config.maintain_subscriber_collection){
            this.subscribers = this.database.getCollection(config.subscriber_collection_name);

   
            this.subscribers.createIndex(
                    new BasicDBObject(RIGHT_KEY, 1).append(LEFT_KEY, 1),
                    new BasicDBObject("unique", true ));

            if(config.maintain_reverse_index)
                this.subscribers.createIndex(
                        new BasicDBObject(LEFT_KEY, 1).append(RIGHT_KEY, 1));
        }

        if(config.maintain_subscribing_collection){
            this.subscribing = this.database.getCollection(config.subscribing_collection_name);

            this.subscribing.createIndex(
                    new BasicDBObject(RIGHT_KEY, 1).append(LEFT_KEY, 1),
                    new BasicDBObject("unique", true ));

            if(config.maintain_reverse_index)
                this.subscribing.createIndex(
                        new BasicDBObject(LEFT_KEY, 1).append(RIGHT_KEY, 1));
        }

        if(this.subscribers == null && this.subscribing == null){
            throw new ServiceException(GeneralError.INVALID_CONFIGURATION).
            set("maintain_subscriber_collection", config.maintain_subscriber_collection).
            set("maintain_subscribing_collection", config.maintain_subscribing_collection);
        }
    }

    
    public User getUserById(final String userId){

        final DBObject result = this.users.findOne(byUserId(userId));
        if( result == null )
            throw new ServiceException(
                    UserServiceError.UNKNOWN_USER).set("userId", userId);

        return new User(result);
    }

    
    
    public User CreateUserById(final String newUser) {
        final User user = new User(newUser);
        this.userValidator.verify(user);
        this.users.save( user.toDBObject() );
        return user;
    }

    
    public void createUser(final User newUser){
        try {
            this.userValidator.verify(newUser);
            this.users.insert( newUser.toDBObject() );
        } catch( MongoException e ) {
            throw new ServiceException(
                    UserServiceError.USER_ALREADY_EXISTS).set("userId", newUser.getUserId());
        }
    }

    
	public void verifyUser(String userId) throws ServiceException {
        final DBObject result = this.users.findOne(byUserId(userId), SELECT_USER_ID);

        if( result == null )
            throw new ServiceException(
                    UserServiceError.UNKNOWN_USER).set("userId", userId);
	}

    public List<User> getAllUser(final int limit) {
    	List<User> results = null;
    	DBCursor cursor = this.users.find().limit(limit);
    			results = getUsersFromCursor(cursor, USER_ID_KEY);
    return results;
    }
	
    public List<User> getAllUsersButOne(final User user, final int limit) {
	    BasicDBObject dbo = new BasicDBObject();
	    dbo.put(USER_ID_KEY, new BasicDBObject("$ne", user.getUserId()));
	    DBCursor cursor = this.users.find(dbo).limit(limit);
	    List<User> results = getUsersFromCursor(cursor, USER_ID_KEY);
	    return results;
	}
    
    public List<User> getSubscribers(final User user, final int limit) {
        List<User> results = null;

        if(config.maintain_subscriber_collection){    	    		

            // If there is a follower collection, get the users directly
            DBCursor cursor = this.subscribers.find(
                    byEdgeOwner(user.getUserId()), selectEdgePeer()).limit(limit);
            results = getUsersFromCursor(cursor, LEFT_KEY);

        } else {

            // otherwise get them from the subscribing collection
            DBCursor cursor = this.subscribing.find(
                    byEdgePeer(user.getUserId()), selectEdgeOwner()).limit(limit);    		
            results = getUsersFromCursor(cursor, RIGHT_KEY);
        }

        return results;    
    }

    
    public SubscribersCount getSubscriberCount(final User user) {

        if(config.maintain_subscriber_collection){    	
            return new SubscribersCount(user, (int)this.subscribers.count(
                    byEdgeOwner(user.getUserId())));
        } else {
            return new SubscribersCount(user, (int)this.subscribing.count(
                    byEdgePeer(user.getUserId())));
        }
    }

    
    public List<User> getSubscribing(final User user, final int limit) {
        List<User> results = null;

        if(config.maintain_subscribing_collection){    	    		

            // If there is a subscribing collection, get the users directly
            DBCursor cursor = this.subscribing.find(
                    byEdgeOwner(user.getUserId()), selectEdgePeer()).limit(limit);
            results = getUsersFromCursor(cursor, LEFT_KEY);

        } else {

            // otherwise get them from the follower collection
            DBCursor cursor = this.subscribers.find(
                    byEdgePeer(user.getUserId()), selectEdgeOwner()).limit(limit);    		
            results = getUsersFromCursor(cursor, RIGHT_KEY);
        }

        return results;    
    }

    
    public SubscribingCount getSubscribingCount(final User user) {

        if(config.maintain_subscribing_collection){    	
            return new SubscribingCount(user, (int)this.subscribing.count(
                    byEdgeOwner(user.getUserId())));
        } else {
            return new SubscribingCount(user, (int)this.subscribers.count(
                    byEdgePeer(user.getUserId())));
        }
    }

    
    public void subscribe(User user, User toFollow) {

    	// Use the some edge _id for both edge collections
    	ObjectId edgeId = new ObjectId();
    	
        // create the "subscribing" relationship
        if(config.maintain_subscribing_collection){
            insertEdgeWithId(this.subscribing, edgeId, user, toFollow);
        }

        if(config.maintain_subscribing_collection){
            insertEdgeWithId(this.subscribers, edgeId, toFollow, user);
        }

 
        if(config.store_follow_counts_with_user){

            this.users.update(byUserId(user.getUserId()), 
                    increment(SUBSCRIBING_COUNT_KEY));

            this.users.update(byUserId(toFollow.getUserId()), 
                    increment(SUBSCRIBER_COUNT_KEY));    			
        }    	
    }


    
    public void unsubscribe(User user, User toRemove) {

        // create the "subscribing" relationship
        if(config.maintain_subscribing_collection){
            this.subscribing.remove(makeEdge(user, toRemove));
        }

        if(config.maintain_subscriber_collection){
            this.subscribers.remove(makeEdge(toRemove, user));
        }


        if(config.store_follow_counts_with_user){

            this.users.update(byUserId(user.getUserId()), 
                    decrement(SUBSCRIBING_COUNT_KEY));

            this.users.update(byUserId(toRemove.getUserId()), 
                    decrement(SUBSCRIBER_COUNT_KEY));    				
        }    	
    }

    
    public void removeUser(String userId) {

        User user = new User(userId);
        for( User subscribing : this.getSubscribing(user,Integer.MAX_VALUE)) {
            this.unsubscribe(user, subscribing);
        }
        for( User follower : this.getSubscribers(user,Integer.MAX_VALUE)) {
            this.unsubscribe(follower, user);
        }
        this.users.remove( byUserId(userId) );

    }
    
    
    public Configuration getConfiguration() {
        return this.config;
    }

    private void insertEdgeWithId(DBCollection edgeCollection, ObjectId id, User user, User toFollow) {
        try {
            edgeCollection.insert( makeEdgeWithId(id, user, toFollow));
        } catch( MongoException e ) {
        }
    }


	static List<User> getUsersFromCursor(DBCursor cursor, String fieldKey){
        try{
            List<User> u = new ArrayList<User>();
            while(cursor.hasNext()) {
              
                u.add(new User((String)cursor.next().get(fieldKey)));
               

            }
         
            return u;
        } finally {
            cursor.close();
        }
    }
	

    static DBObject increment(String field) {
        return new BasicDBObject("$inc", new BasicDBObject(field, 1));
    }

    static DBObject decrement(String field) {
        return new BasicDBObject("$inc", new BasicDBObject(field, -1));
    }

    static DBObject byUserId(String user_id) {
        return new BasicDBObject(USER_ID_KEY, user_id);
    }

    static DBObject makeEdge(final User from, final User to) {
        return new BasicDBObject(RIGHT_KEY, 
                from.getUserId()).append(LEFT_KEY, to.getUserId());
    }

    static DBObject makeEdgeWithId(ObjectId id, User from, User to) {
        return new BasicDBObject(USER_ID_KEY, id).append(RIGHT_KEY, 
                from.getUserId()).append(LEFT_KEY, to.getUserId());
	}

    static DBObject byEdgeOwner(String remote) {
        return new BasicDBObject(RIGHT_KEY, remote);
    }
    
    static DBObject byUser(String remote) {
        return new BasicDBObject(USER_ID_KEY, remote);
    }

    static DBObject byEdgePeer(String remote) {
        return new BasicDBObject(LEFT_KEY, remote);
    }

    static DBObject selectEdgePeer() {
        return  new BasicDBObject(LEFT_KEY, 1).append(USER_ID_KEY, 0);
    }

    static DBObject selectEdgeOwner() {
        return  new BasicDBObject(RIGHT_KEY, 1).append(USER_ID_KEY, 0);
    }

	
	public void joinEvent(User from, Event to) {
	}

	
	public void createtEvent(Event event) throws ServiceException {
	}

	
	public void withdrawfromEvent(User from, Event to) {
	}
}
