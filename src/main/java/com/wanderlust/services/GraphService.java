package com.wanderlust.services;

import com.mongodb.MongoClientURI;
import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventProfile;
import com.wanderlust.api.EventProfileID;
import com.wanderlust.api.User;
import com.wanderlust.configuration.PageLimitConfiguration;
import com.yammer.dropwizard.config.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ServiceImplementation(
        name = "GraphService", 
        dependencies = {UserService.class, ProfileService.class},
        configClass = PageLimitConfiguration.class)
public class GraphService implements TimelineService {

    private final ProfileService profile;
    private final UserService usergraph;  
    private final PageLimitConfiguration config;

    public GraphService(final MongoClientURI dbUri, final UserService usergraph, 
            final ProfileService profile, final PageLimitConfiguration svcConfig) {
        this.profile = profile;
        this.usergraph = usergraph;
    	this.config = svcConfig;  	
    }
    
    public void post(final User sender, final UserProfile profile) {
    }
    
    public List<UserProfile> getPostsBy(final User user, final UserProfileID target, final int limit) {
        return this.profile.getContentFor(user, target, limit);
    }
    
    public List<EventProfile> getPostsBy(final Event event, final EventProfileID target, final int limit) {
        return this.profile.getContentFor(event, target, limit);
    }
    
    public List<UserProfile> getTimelineFor(final User user, final UserProfileID target, final int limit) {
        List<User> following = this.usergraph.getSubscribing(user, config.page_limit);
        return this.profile.getContentFor(following, target, limit);
    }    

    public Configuration getConfiguration() {
        return this.config;
    }

    public List<UserProfile> getPostsBy(User user, int limit) {
        return this.getPostsBy(user, null, limit);
    }

    public List<EventProfile> getPostsBy(Event event, int limit) {
        return this.getPostsBy(event, null, limit);
    }
    
    public void event(final User sender, final UserProfile profile) {
}
    

    public List<UserProfile> getEventsBy(final User user, final UserProfileID target, final int limit) {
    return this.profile.getContentFor(user, target, limit);
}
    

    public List<UserProfile> getEventsBy(User user, int limit) {
        return this.getEventsBy(user, null, limit);
    }
    

    public List<UserProfile> getTimelineFor(User user, int limit) {
        return this.getTimelineFor(user, null, limit);
    }


    public void shutdown(long timeout, TimeUnit unit) {
    }
}
