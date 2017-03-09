package com.wanderlust.users;

import com.mongodb.*;
import com.wanderlust.MongoBackedService;
import com.wanderlust.api.User;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventServiceError;
import com.wanderlust.api.FrameworkError;
import com.wanderlust.api.ServiceException;
import com.wanderlust.configuration.DefaultEventServiceConfiguration;
import com.wanderlust.services.EventService;
import com.wanderlust.services.ServiceImplementation;
import com.yammer.dropwizard.config.Configuration;

import java.util.List;
import java.util.ArrayList;

import org.bson.types.ObjectId;

@ServiceImplementation(name = "DefaultEventService", configClass = DefaultEventServiceConfiguration.class)
public class DefaultEventService 
    extends MongoBackedService implements EventService {

    private static final String EVENT_ID_KEY = "_id";
    private static final String EDGE_OWNER_KEY = "_f";
    private static final String EDGE_PEER_KEY = "_t";
    private static final String SUBSCRIBER_COUNT_KEY = "_cr";
    private static final String SUBSCRIBING_COUNT_KEY = "_cg";
    
    private static final BasicDBObject SELECT_EVENT_ID = 
    		new BasicDBObject(EVENT_ID_KEY, 1);

    private final DBCollection events;
    private DBCollection subscribers = null;
    private DBCollection subscribing = null;

    private final DefaultEventServiceConfiguration config;
    private final EventVerifier eventValidator;

	public DefaultEventService(final MongoClientURI dbUri, final DefaultEventServiceConfiguration svcConfig ) {
        super(dbUri, svcConfig);
        
        this.config = svcConfig;  	
        this.events = this.database.getCollection(config.event_collection_name);
        this.eventValidator = new BasicEventIdVerifier();

        // establish the follower collection and create indices as configured
        if(config.maintain_subscriber_collection){
            this.subscribers = this.database.getCollection(config.subscriber_collection_name);

            
            this.subscribers.createIndex(
                    new BasicDBObject(EDGE_OWNER_KEY, 1).append(EDGE_PEER_KEY, 1),
                    new BasicDBObject("unique", true ));

            if(config.maintain_reverse_index)
                this.subscribers.createIndex(
                        new BasicDBObject(EDGE_PEER_KEY, 1).append(EDGE_OWNER_KEY, 1));
        }

        // also establish subscribing collection if configured
        if(config.maintain_subscribing_collection){
            this.subscribing = this.database.getCollection(config.subscribing_collection_name);

            this.subscribing.createIndex(
                    new BasicDBObject(EDGE_OWNER_KEY, 1).append(EDGE_PEER_KEY, 1),
                    new BasicDBObject("unique", true ));

            if(config.maintain_reverse_index)
                this.subscribing.createIndex(
                        new BasicDBObject(EDGE_PEER_KEY, 1).append(EDGE_OWNER_KEY, 1));
        }

        if(this.subscribers == null && this.subscribing == null){
            throw new ServiceException(FrameworkError.INVALID_CONFIGURATION).
            set("maintain_follower_collection", config.maintain_subscriber_collection).
            set("maintain_subscribing_collection", config.maintain_subscribing_collection);
        }
    }

    
    public Event getEventById(final String eventId){

        final DBObject result = this.events.findOne(byEventId(eventId));

        if( result == null )
            throw new ServiceException(
                    EventServiceError.UNKNOWN_EVENT).set("eventId", eventId);

        return new Event(result);
    }

    
    public Event getOrCreateEventById(final String newEvent) {
        final Event event = new Event(newEvent);
        this.eventValidator.verify(event);
        this.events.save( event.toDBObject() );
        return event;
    }

    
    public void createEvent(final Event newEvent){
        try {
            this.eventValidator.verify(newEvent);
            this.events.insert( newEvent.toDBObject() );
        } catch( MongoException e ) {
            throw new ServiceException(
                    EventServiceError.EVENT_ALREADY_EXISTS).set("eventId", newEvent.getEventId());
        }
    }

    
	public void verifyEvent(String eventId) throws ServiceException {
        final DBObject result = this.events.findOne(byEventId(eventId), SELECT_EVENT_ID);

        if( result == null )
            throw new ServiceException(
                    EventServiceError.UNKNOWN_EVENT).set("eventId", eventId);
	}

    
    public List<User> getSubscribers(final Event event, final int limit) {
        List<User> results = null;

        if(config.maintain_subscriber_collection){    	    		

            DBCursor cursor = this.subscribers.find(
                    byEdgeOwner(event.getEventId()), selectEdgePeer()).limit(limit);
            results = getusersFromCursor(cursor, EDGE_PEER_KEY);

        } 


        return results;    
    }

    


    
    public void subscribe(User user, Event toFollow) {


    	ObjectId edgeId = new ObjectId();
    	
        // create the "subscribing" relationship
        if(config.maintain_subscribing_collection){
            insertEdgeWithId(this.subscribing, edgeId, user, toFollow);
        }

        if(config.store_subscribe_counts_with_event){

            this.events.update(byEventId(toFollow.getEventId()), 
                    increment(SUBSCRIBING_COUNT_KEY));
	
      }    	
    }


    
    public void unsubscribe(User user, Event toRemove) {

        // create the "subscribing" relationship
        if(config.maintain_subscribing_collection){
            this.subscribing.remove(makeEdge(toRemove, user));
        }


        if(config.store_subscribe_counts_with_event){



            this.events.update(byEventId(toRemove.getEventId()), 
                    decrement(SUBSCRIBER_COUNT_KEY));    				
        }    	
    }

    
    public void removeEvent(String eventId) {

        Event event = new Event(eventId);

        for( User subscriber : this.getSubscribers(event, Integer.MAX_VALUE)) {
            this.unsubscribe(subscriber, event);
        }
        this.events.remove( byEventId(eventId) );

    }
    
    
    public Configuration getConfiguration() {
        return this.config;
    }

    private void insertEdgeWithId(DBCollection edgeCollection, ObjectId id, User user, Event toFollow) {
        try {
            edgeCollection.insert( makeEdgeWithId(id, user, toFollow));
        } catch( MongoException e ) {
        }
    }


	static List<User> getusersFromCursor(DBCursor cursor, String fieldKey){
        try{
            List<User> subscribers = new ArrayList<User>();
            while(cursor.hasNext()) {
                subscribers.add(new User((String)cursor.next().get(fieldKey)));
            }
            return subscribers;
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

    static DBObject byEventId(String event_id) {
        return new BasicDBObject(EVENT_ID_KEY, event_id);
    }

    static DBObject makeEdge(final Event from, final User toRemove) {
        return new BasicDBObject(EDGE_OWNER_KEY, 
                from.getEventId()).append(EDGE_PEER_KEY, toRemove.getUserId());
    }

    static DBObject makeEdgeWithId(ObjectId id, User from, Event to) {
        return new BasicDBObject(EVENT_ID_KEY, id).append(EDGE_OWNER_KEY, 
                from.getUserId()).append(EDGE_PEER_KEY, to.getEventId());
	}

    static DBObject byEdgeOwner(String remote) {
        return new BasicDBObject(EDGE_OWNER_KEY, remote);
    }

    static DBObject byEdgePeer(String remote) {
        return new BasicDBObject(EDGE_PEER_KEY, remote);
    }

    static DBObject selectEdgePeer() {
        return  new BasicDBObject(EDGE_PEER_KEY, 1).append(EVENT_ID_KEY, 0);
    }

    static DBObject selectEdgeOwner() {
        return  new BasicDBObject(EDGE_OWNER_KEY, 1).append(EVENT_ID_KEY, 0);
    }




	@Override
	public List<Event> getAllEvents() {
		 DBCursor cursor = this.events.find();
		    List<Event> results = new ArrayList<Event>();
		    while (cursor.hasNext()) 
		    {
		        results.add((Event) cursor.next());         
		    }
		  return results;
	}


	
	
}
