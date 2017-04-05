package com.wanderlust.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;

import com.mongodb.MongoClientURI;
import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventProfile;
import com.wanderlust.api.EventProfileID;
import com.wanderlust.api.User;
import com.wanderlust.services.ProfileService;
import com.wanderlust.services.ServiceImplementation;
import com.wanderlust.util.ContentListHelper;
import com.wanderlust.util.ListUser;
import com.yammer.dropwizard.config.Configuration;

@ServiceImplementation(name = "AppProfileService")
public class UserProfileService implements ProfileService {

	private ConcurrentHashMap<ObjectId, UserProfile> contentIndex = new ConcurrentHashMap<ObjectId, UserProfile>();
	private ConcurrentHashMap<User, List<UserProfile>> userContentLists = new ConcurrentHashMap<User, List<UserProfile>>();
	private MessageValidator postValidator = new MessageValidator();

	public UserProfileService(final MongoClientURI dbUri){}


	public UserProfile getContentById(UserProfileID id) {
		UserProfile result = contentIndex.get(id.getId());        
		return result;
	}


	public void publishPost(User user, UserProfile profile) {
		postValidator.validateContent(profile);
		contentIndex.put((ObjectId)profile.getId(), profile);
		List<UserProfile> usersContent = userContentLists.get(user);

		if(usersContent == null){
			usersContent = new ArrayList<UserProfile>();
			userContentLists.put(user, usersContent);
		}

		usersContent.add(profile);
	}


	public List<UserProfile> getContentFor(User user, UserProfileID anchor, int limit) {

		List<UserProfile> usersContent = userContentLists.get(user);        
		if(usersContent != null){
			return ContentListHelper.extractContent(usersContent, anchor, limit, true);
		}

		return Collections.emptyList();
	}


	public List<UserProfile> getContentFor(List<User> users, UserProfileID anchor, int limit) {

		if(anchor == null && limit < 0){
			return Collections.emptyList();
		}

		List<ListUser<UserProfile>> walkers = new ArrayList<ListUser<UserProfile>>(users.size());

		for(User user : users){
			List<UserProfile> usersContent = userContentLists.get(user);                
			if(usersContent != null){
				walkers.add(ContentListHelper.getContentWalker(usersContent, anchor, limit));
			}
		}

		return ContentListHelper.merge(walkers, limit);
	}


	public Configuration getConfiguration() {
		return null;
	}


	public void shutdown(long timeout, TimeUnit unit) {
	}


	public void reset() 
	{
		this.contentIndex.clear();
		this.userContentLists.clear();
	}


	@Override
	public List<EventProfile> getContentFor(Event event, EventProfileID anchor, int limit) {
		return null;
	}


	@Override
	public List<EventProfile> getContentFor(List<Event> event, EventProfileID anchor, int limit) {
		return null;
	}    
}
