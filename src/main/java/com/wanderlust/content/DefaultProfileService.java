package com.wanderlust.content;

import com.mongodb.*;
import com.wanderlust.MongoBackedService;
import com.wanderlust.api.*;
import com.wanderlust.configuration.DefaultContentServiceConfiguration;
import com.wanderlust.services.ProfileService;
import com.wanderlust.services.ServiceImplementation;
import com.yammer.dropwizard.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ServiceImplementation(
		name = "DefaultContentService", 
		configClass = DefaultContentServiceConfiguration.class)
public class DefaultProfileService 
extends MongoBackedService implements ProfileService{

	private DBCollection content = null;
	private MessageValidation contentValidator = null;
	private final DefaultContentServiceConfiguration config;

	public DefaultProfileService(final MongoClientURI dbUri, final DefaultContentServiceConfiguration svcConfig ) {
		super(dbUri, svcConfig);
		this.config = svcConfig;
		this.content = this.database.getCollection(config.content_collection_name);


		this.contentValidator = new MessageValidator();
	}
	public enum SortOrder {
		ASCENDING(1),
		DESCENDING(-1);

		private int value;

		private SortOrder(final int value){
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public List<UserProfile> getContentFor(User user, UserProfileID target, int limit) {

		if(limit == 0 || (target == null && limit < 0)){
			return Collections.emptyList();
		}

		DBCursor contentCursor = null;
		SortOrder order = null;

		if(target == null){
			contentCursor = this.content.find(byUserId(user));  
			order = SortOrder.DESCENDING;
		}
		else if(limit > 0){
			contentCursor = this.content.find(byUserAfterContentId(user, target));
			order = SortOrder.DESCENDING;
		}
		else{
			contentCursor = this.content.find(byUserBeforeContentId(user, target));
			order = SortOrder.ASCENDING;
		}

		return getFromCursor(contentCursor, order, Math.abs(limit));
	}


	public List<UserProfile> getContentFor(List<User> users, UserProfileID target, int limit) {


		if(users == null || users.isEmpty()){
			return Collections.emptyList();
		}


		if(target == null && limit < 0){
			return Collections.emptyList();
		}

		DBCursor contentCursor = null;
		SortOrder order = null;

		if(target == null){
			contentCursor = this.content.find(byUserList(users));  
			order = SortOrder.DESCENDING;
		}
		else if(limit > 0){
			contentCursor = this.content.find(byUserListAfterContentId(users, target));
			order = SortOrder.DESCENDING;
		}
		else{
			contentCursor = this.content.find(byUserListBeforeContentId(users, target));
			order = SortOrder.ASCENDING;
		}

		return getFromCursor(contentCursor, order, Math.abs(limit));
	}

	public UserProfile getContentById(final UserProfileID id) {
		DBObject target = this.content.findOne(byContentId(id));

		if( target == null )
			throw new ServiceException(
					UserProfileError.CONTENT_NOT_FOUND).set("contentId", id);

		return new UserProfile(target);
	}

	public void publishPost(User user, UserProfile content) {
		this.contentValidator.validateContent(content);
		this.content.insert(content.toDBObject());
	}	

	public Configuration getConfiguration() {
		return this.config;
	}

	private static List<UserProfile> getFromCursor(DBCursor results, SortOrder order, int limit) {
		List<UserProfile> contentList = new ArrayList<UserProfile>();

		results.sort( new BasicDBObject(UserProfile.ID_KEY, order.getValue()));
		results.limit(-limit);

		while(results.hasNext()) {
			final DBObject obj = results.next();
			final UserProfile content = new UserProfile(obj);
			contentList.add(content);
		}


		if(order == SortOrder.ASCENDING){
			Collections.reverse(contentList);
		}

		return contentList;
	}

	private static BasicDBObject byContentId(UserProfileID id) {
		return new BasicDBObject(UserProfile.ID_KEY, id.getId());
	}

	private static BasicDBObject byUserAfterContentId(User user, UserProfileID id) {
		return new BasicDBObject(UserProfile.AUTHOR_KEY, user.getUserId()).
				append(UserProfile.ID_KEY, new BasicDBObject("$lt", id.getId()));
	}

	private static BasicDBObject byUserBeforeContentId(User user, UserProfileID id) {
		return new BasicDBObject(UserProfile.AUTHOR_KEY, user.getUserId()).
				append(UserProfile.ID_KEY, new BasicDBObject("$gt", id.getId()));
	}

	private static BasicDBObject byUserId(User user) {
		return new BasicDBObject(UserProfile.AUTHOR_KEY, user.getUserId());
	}

	private static BasicDBObject byUserList(List<User> users) {

		BasicDBList id_list = new BasicDBList();
		for( User user : users )
			id_list.add( user.getUserId() );

		BasicDBObject in = new BasicDBObject("$in", id_list);
		return new BasicDBObject(UserProfile.AUTHOR_KEY, in);
	}

	private static BasicDBObject byUserListAfterContentId(List<User> users, UserProfileID id) {

		BasicDBList id_list = new BasicDBList();
		for( User user : users )
			id_list.add( user.getUserId() );

		BasicDBObject in = new BasicDBObject("$in", id_list);
		return new BasicDBObject(UserProfile.AUTHOR_KEY, in).
				append(UserProfile.ID_KEY, new BasicDBObject("$lt", id.getId()));
	}

	private static BasicDBObject byUserListBeforeContentId(List<User> users, UserProfileID id) {

		BasicDBList id_list = new BasicDBList();
		for( User user : users )
			id_list.add( user.getUserId() );

		BasicDBObject in = new BasicDBObject("$in", id_list);
		return new BasicDBObject(UserProfile.AUTHOR_KEY, in).
				append(UserProfile.ID_KEY, new BasicDBObject("$gt", id.getId()));
	}

	@Override
	public List<EventProfile> getContentFor(Event event, EventProfileID target, int limit) {
		return null;

	}


	@Override
	public List<EventProfile> getContentFor(List<Event> event, EventProfileID target, int limit) {
		return null;
	}
}
