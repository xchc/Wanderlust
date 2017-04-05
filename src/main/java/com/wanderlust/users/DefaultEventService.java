package com.wanderlust.users;

import com.mongodb.*;
import com.wanderlust.MongoBackedService;
import com.wanderlust.api.User;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventServiceError;

import com.wanderlust.api.ServiceException;
import com.wanderlust.configuration.DefaultEventServiceConfiguration;
import com.wanderlust.services.EventService;
import com.wanderlust.services.ServiceImplementation;
import com.yammer.dropwizard.config.Configuration;
import com.wanderlust.configuration.DefaultUserServiceConfiguration;

import java.util.List;
import java.net.UnknownHostException;
import java.util.ArrayList;


@ServiceImplementation(name = "DefaultEventService", configClass = DefaultEventServiceConfiguration.class)

public class DefaultEventService 
extends MongoBackedService implements EventService {

	private static final String EVENT_ID_KEY = "_id";
	private static final String SUBSCRIBER_COUNT_KEY = "_cr";
	private static final String SUBSCRIBING_COUNT_KEY = "_cg";


	private static final BasicDBObject SELECT_EVENT_ID = 
			new BasicDBObject(EVENT_ID_KEY, 1);

	private final DBCollection events;

	DefaultUserServiceConfiguration des = new DefaultUserServiceConfiguration();

	private final DefaultEventServiceConfiguration config;
	private final EventVerifier eventValidator;

	public DefaultEventService(final MongoClientURI dbUri, final DefaultEventServiceConfiguration svcConfig ) throws UnknownHostException {
		super(dbUri, svcConfig);
		Mongo mongo = new MongoClient("localhost", 27017);
		DB dbu = mongo.getDB("wanderlustTest4");
		this.config = svcConfig;  	
		this.events = this.database.getCollection(config.event_collection_name);
		dbu.getCollection("users");
		this.eventValidator = new BasicEventIdVerifier();
	}


	public Event getEventById(final String eventId){

		final DBObject result = this.events.findOne(byEventId(eventId));

		if( result == null )
			throw new ServiceException(
					EventServiceError.UNKNOWN_EVENT).set("eventId", eventId);

		return new Event(result);
	}


	public List<Event> getAllEvent(final int limit) {
		List<Event> results = null;
		DBCursor cursor = this.events.find().limit(limit);
		results = geteventsFromCursor(cursor, EVENT_ID_KEY);
		return results;
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



	public void subscribe(User user, Event toFollow) {



		if(config.store_subscribe_counts_with_event){

			this.events.update(byEventId(toFollow.getEventId()), 
					increment(SUBSCRIBING_COUNT_KEY));

			this.events.update(byEventId(toFollow.getEventId()), 
					increment(SUBSCRIBER_COUNT_KEY)); 

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




	static List<Event> geteventsFromCursor(DBCursor cursor, String fieldKey){
		try{
			List<Event> e = new ArrayList<Event>();
			while(cursor.hasNext()) {
				e.add(new Event((String)cursor.next().get(fieldKey)));
			}
			return e;
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


	@Override
	public List<User> getSubscribers(Event event, int limit) {
		return null;
	}


	@Override
	public void unsubscribe(User from, Event to) {

		
	}




}
