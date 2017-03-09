package com.wanderlust.resources;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.wanderlust.api.Event;
import com.wanderlust.api.EventProfile;
import com.wanderlust.api.EventProfileID;
import com.wanderlust.api.SubscribersCount;
//import com.wanderlust.api.SubscribingCount;
import com.wanderlust.api.User;
import com.wanderlust.services.ProfileService;
import com.wanderlust.services.EventService;
import com.wanderlust.services.TimelineService;
import com.wanderlust.services.UserService;
import com.wanderlust.util.JSONParam;

import java.util.List;

@Path("{user_id}/events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private final EventService eventGraph;
    private final TimelineService feedService;
    private final ProfileService contentService;
    private final UserService userGraph; 

    public EventResource(ProfileService content, TimelineService feed, EventService events, UserService users) {
        this.eventGraph = events;
        this.feedService = feed;
        this.contentService = content;
        this.userGraph = users;
    }

    
    @GET
    @Path("/{event_id}")
    public Event get(@PathParam("event_id") String event_id, @PathParam("user_id") String user_id ) {
    	this.userGraph.verifyUser(user_id);
    	Event event = this.eventGraph.getEventById(event_id);
        return event;
    }

    @PUT
    @Path("/{event_id}")
    public Event create(
            @PathParam("event_id") String eventId, 
            @QueryParam("event_data") JSONParam eventData ) {

    	Event newEvent = new Event(eventId, eventData);
        this.eventGraph.createEvent(newEvent);
        return newEvent;
    }

    @DELETE
    @Path("/{event_id}")
    public void delete(
            @PathParam("event_id") String eventId, 
            @QueryParam("event_data") JSONParam eventData ) {
        this.eventGraph.removeEvent(eventId);
    }

    @GET
    @Path("/{event_id}/subscriber_count")
    public SubscribersCount getFollowerCount(@PathParam("user_id") String user_id ) {
        this.userGraph.verifyUser(user_id);
        return this.userGraph.getSubscriberCount(new User(user_id));
    }

    @GET
    @Path("/{event_id}/subscribers")
    public List<User> getSubscribers(@PathParam("event_id") String event_id, @PathParam("user_id") String user_id,
            @DefaultValue("50") @QueryParam("limit") int limit) {
        this.eventGraph.verifyEvent(event_id);
        return this.eventGraph.getSubscribers(new Event(event_id), limit);
    }

    @GET
    @Path("/{user_id}/subscribing")
    public List<User> getFriends(@PathParam("user_id") String user_id,
            @DefaultValue("50") @QueryParam("limit") int limit) {
        this.userGraph.verifyUser(user_id);
        return this.userGraph.getSubscribing(new User(user_id), limit);
    }


    @PUT
    @Path("/{user_id}/subscribing/{target}")
    public void subscribe(@PathParam("user_id") String user_id,
    		@PathParam("target") String to_follow ) {
        this.userGraph.verifyUser(user_id);
        this.eventGraph.verifyEvent(to_follow);
        this.eventGraph.subscribe( new User(user_id), new Event(to_follow) );
    }

    @DELETE
    @Path("/{event_id}/subscribing/{target}")
    public void unfollow(@PathParam("user_id") String user_id,
            @PathParam("target") String to_unfollow ) {
        this.userGraph.verifyUser(user_id);
        this.userGraph.verifyUser(to_unfollow);
        this.userGraph.unsubscribe( new User(user_id), new User(to_unfollow) );
    }

    @POST
    @Path("/{event_id}/eventInfo")
    public UserProfileID send(
            @PathParam("user_id") String user_id,
            @QueryParam("message") String message,
            @QueryParam("event") String event,
            @QueryParam("content") JSONParam data ) {

        this.userGraph.verifyUser(user_id);
        User author = new User(user_id);
        UserProfile newContent = new UserProfile(author, message, event, data);
        this.contentService.publishContent(author, newContent);
        this.feedService.post(author, newContent);
        return newContent.getContentId();
    }

    @GET
    @Path("/{event_id}/eventInfo")
    public List<EventProfile> getPosts(@PathParam("event_id") String event_id,
            @DefaultValue("50") @QueryParam("limit") int limit,
            @QueryParam("anchor") EventProfileID anchor ) {
        this.eventGraph.verifyEvent(event_id);
        return this.feedService.getPostsBy(new Event(event_id), anchor, limit);
    }

    
}
