package com.wanderlust.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.mongodb.MongoClientURI;
import com.wanderlust.api.SubscribersCount;
import com.wanderlust.api.SubscribingCount;
import com.wanderlust.api.Event;
import com.wanderlust.api.ServiceException;
import com.wanderlust.api.User;
import com.wanderlust.api.UserServiceError;
import com.wanderlust.services.ServiceImplementation;
import com.wanderlust.services.ResetService;
import com.wanderlust.services.UserService;
import com.yammer.dropwizard.config.Configuration;

@ServiceImplementation(name = "InMemoryUserService")
public class InMemoryUserService implements UserService, ResetService {

	private Map<String, User> users = new HashMap<String, User>();
	private Map<String, Set<User>> subscriberIndex = new HashMap<String, Set<User>>();
	private Map<String, Set<User>> subscribingIndex = new HashMap<String, Set<User>>();

	public InMemoryUserService(final MongoClientURI dbUri){}

	
	public User getOrCreateUserById(String userId) {
		User user = getUserById(userId);
		if(user == null) {
			user = new User(userId);
			createUser(user);
		}
		return user;
	}

	
	public void createUser(User user) {
		String userId = user.getUserId();
		users.put(userId, user);
		subscriberIndex.put(userId, new HashSet<User>());
		subscribingIndex.put(userId, new HashSet<User>());
	}

	
	public void removeUser(String userId) {
		users.remove(userId);

	}

	
	public User getUserById(String userId) {
		return users.get(userId);
	}

	
	public void verifyUser(String userId) throws ServiceException {

		if(this.users.containsKey(userId) == false)
			throw new ServiceException(
					UserServiceError.UNKNOWN_USER).set("userId", userId);
	}

	
	public void subscribe(User from, User to) {
		Set<User> subscribing =  subscribingIndex.get(from.getUserId());
		Set<User> subscriber =  subscriberIndex.get(to.getUserId());

		subscribing.add(to);
		subscriber.add(from);       
	}


	
	public void joinEvent(User from, Event to) {
		//Set<User> subscribing =  subscribingIndex.get(from.getUserId());
		Set<User> subscriber =  subscriberIndex.get(to.getEventId());

		//subscribing.add(to);
		subscriber.add(from);       
	}

	
	public void unsubscribe(User from, User to) {
		Set<User> subscribing =  subscribingIndex.get(from.getUserId());
		Set<User> subscriber =  subscriberIndex.get(to.getUserId());

		subscribing.remove(to);
		subscriber.remove(from);       
	}

	
	public SubscribersCount getSubscriberCount(User u) {
		return new SubscribersCount(u, subscriberIndex.get(u.getUserId()).size());
	}

	
	public List<User> getSubscribers(User u, int limit) {
		return new ArrayList<User>(subscriberIndex.get(u.getUserId()));
	}

	
	public SubscribingCount getSubscribingCount(User u) {
		return new SubscribingCount(u, subscribingIndex.get(u.getUserId()).size());
	}

	
	public List<User> getSubscribing(User u, int limit) {
		return new ArrayList<User>(subscribingIndex.get(u.getUserId()));
	}

	
	public void reset(){
		users.clear();
		subscriberIndex.clear();
		subscribingIndex.clear();
	}

	
	public Configuration getConfiguration() {

		// No configuration
		return null;
	}

	
	public void shutdown(long timeout, TimeUnit unit) {
		// nothing to do        
	}

	
	public void createtEvent(Event event) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	
	public void withdrawfromEvent(User from, Event to) {
		// TODO Auto-generated method stub
		
	}


}
