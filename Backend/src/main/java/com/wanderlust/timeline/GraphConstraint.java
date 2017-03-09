package com.wanderlust.timeline;

import com.mongodb.MongoClientURI;
import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventProfile;
import com.wanderlust.api.EventProfileID;
import com.wanderlust.api.User;
import com.wanderlust.configuration.PageLimitConfiguration;
import com.wanderlust.services.ProfileService;
import com.wanderlust.services.TimelineService;
import com.wanderlust.services.ServiceImplementation;
import com.wanderlust.services.UserService;
import com.yammer.dropwizard.config.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ServiceImplementation(
        name = "GraphConstraint", 
        dependencies = {UserService.class, ProfileService.class},
        configClass = PageLimitConfiguration.class)
public class GraphConstraint implements TimelineService {

    private final ProfileService content;
    private final UserService usergraph;  
    private final PageLimitConfiguration config;

    public GraphConstraint(final MongoClientURI dbUri, final UserService usergraph, 
            final ProfileService content, final PageLimitConfiguration svcConfig) {
        this.content = content;
        this.usergraph = usergraph;
    	this.config = svcConfig;  	
    }
    
    public void post(final User sender, final UserProfile content) {
    }
    
    public List<UserProfile> getPostsBy(final User user, final UserProfileID anchor, final int limit) {
        return this.content.getContentFor(user, anchor, limit);
    }
    
    public List<EventProfile> getPostsBy(final Event event, final EventProfileID anchor, final int limit) {
        return this.content.getContentFor(event, anchor, limit);
    }
    
    public List<UserProfile> getFeedFor(final User user, final UserProfileID anchor, final int limit) {
        List<User> following = this.usergraph.getSubscribing(user, config.page_limit);
        return this.content.getContentFor(following, anchor, limit);
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
    
    public void event(final User sender, final UserProfile content) {
}
    

    public List<UserProfile> getEventsBy(final User user, final UserProfileID anchor, final int limit) {
    return this.content.getContentFor(user, anchor, limit);
}
    

    public List<UserProfile> getEventsBy(User user, int limit) {
        return this.getEventsBy(user, null, limit);
    }
    

    public List<UserProfile> getFeedFor(User user, int limit) {
        return this.getFeedFor(user, null, limit);
    }


    public void shutdown(long timeout, TimeUnit unit) {
        // Nothing to do !
    }
}
