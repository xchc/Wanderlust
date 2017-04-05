package com.wanderlust.services;

import java.util.List;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventProfile;
import com.wanderlust.api.EventProfileID;
import com.wanderlust.api.User;

public interface ProfileService extends Service{

	public UserProfile getContentById(UserProfileID id);

	public void publishPost(User user, UserProfile profile);	

	public List<UserProfile> getContentFor(User user, UserProfileID anchor, int limit);

	public List<UserProfile> getContentFor(List<User> users, UserProfileID anchor, int limit);

	public List<EventProfile> getContentFor(Event event, EventProfileID anchor, int limit);

	public List<EventProfile> getContentFor(List<Event> event, EventProfileID anchor, int limit);
}
