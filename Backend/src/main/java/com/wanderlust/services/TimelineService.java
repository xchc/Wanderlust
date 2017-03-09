package com.wanderlust.services;

import java.util.List;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventProfile;
import com.wanderlust.api.EventProfileID;
import com.wanderlust.api.User;

public interface TimelineService extends Service{

	public void post(User sender, UserProfile content);

	public List<UserProfile> getPostsBy(User user, int limit);

	public List<UserProfile> getPostsBy(User user, UserProfileID anchor, int limit);

	public List<EventProfile> getPostsBy(Event event, EventProfileID anchor, int limit);
	
	public void event(User sender, UserProfile content);

	public List<UserProfile> getEventsBy(User user, int limit);

	public List<UserProfile> getEventsBy(User user, UserProfileID anchor, int limit);

	public List<UserProfile> getFeedFor(User user, int limit);

	public List<UserProfile> getFeedFor(User user, UserProfileID anchor, int limit);

}
