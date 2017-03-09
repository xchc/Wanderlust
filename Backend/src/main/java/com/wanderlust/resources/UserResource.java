package com.wanderlust.resources;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;

import com.wanderlust.api.SubscribersCount;
import com.wanderlust.api.SubscribingCount;
import com.wanderlust.api.User;
import com.wanderlust.services.ProfileService;

import com.wanderlust.services.TimelineService;
import com.wanderlust.services.UserService;
import com.wanderlust.util.JSONParam;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserService userGraph;
    private final TimelineService feedService;
    private final ProfileService contentService;

    public UserResource(ProfileService content, TimelineService feed, UserService users) {
        this.userGraph = users;
        this.feedService = feed;
        this.contentService = content;
        //this.eventService = event;
    }

    @GET
    @Path("/{user_id}")
    public User get(@PathParam("user_id") String user_id ) {

        User user = this.userGraph.getUserById(user_id);
        return user;
    }

    @PUT
    @Path("/{user_id}")
    public User create(
            @PathParam("user_id") String userId, 
            @QueryParam("user_data") JSONParam userData ) {

        User newUser = new User(userId, userData);
        this.userGraph.createUser(newUser);
        return newUser;
    }

    @DELETE
    @Path("/{user_id}")
    public void delete(
            @PathParam("user_id") String userId, 
            @QueryParam("user_data") JSONParam userData ) {

        this.userGraph.removeUser(userId);
    }

    @GET
    @Path("/{user_id}/subscriber_count")
    public SubscribersCount getSubscriberCount(@PathParam("user_id") String user_id ) {
        this.userGraph.verifyUser(user_id);
        return this.userGraph.getSubscriberCount(new User(user_id));
    }

    @GET
    @Path("/{user_id}/subscribers")
    public List<User> getSubscribers(@PathParam("user_id") String user_id,
            @DefaultValue("50") @QueryParam("limit") int limit) {
        this.userGraph.verifyUser(user_id);
        return this.userGraph.getSubscribers(new User(user_id), limit);
    }

    @GET
    @Path("/{user_id}/subscribing")
    public List<User> getSubscribing(@PathParam("user_id") String user_id,
            @DefaultValue("50") @QueryParam("limit") int limit) {
        this.userGraph.verifyUser(user_id);
        return this.userGraph.getSubscribing(new User(user_id), limit);
    }

    @GET
    @Path("/{user_id}/subscribing_count")
    public SubscribingCount getSubscribingCount(@PathParam("user_id") String user_id ) {
        this.userGraph.verifyUser(user_id);
        return this.userGraph.getSubscribingCount(new User(user_id));
    }

    @PUT
    @Path("/{user_id}/subscribing/{target}")
    public void subscribe(@PathParam("user_id") String user_id,
    		@PathParam("target") String to_follow ) {
        this.userGraph.verifyUser(user_id);
        this.userGraph.verifyUser(to_follow);
        this.userGraph.subscribe( new User(user_id), new User(to_follow) );
    }
    
    
    @DELETE
    @Path("/{user_id}/subscribing/{target}")
    public void unfollow(@PathParam("user_id") String user_id,
            @PathParam("target") String to_unfollow ) {
        this.userGraph.verifyUser(user_id);
        this.userGraph.verifyUser(to_unfollow);
        this.userGraph.unsubscribe( new User(user_id), new User(to_unfollow) );
    }

    @POST
    @Path("/{user_id}/posts")
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
    @Path("/{user_id}/posts")
    public List<UserProfile> getPosts(@PathParam("user_id") String user_id,
            @DefaultValue("50") @QueryParam("limit") int limit,
            @QueryParam("anchor") UserProfileID anchor ) {
        this.userGraph.verifyUser(user_id);
        return this.feedService.getPostsBy(new User(user_id), anchor, limit);
    }

    @GET
    @Path("/{user_id}/messageBoard")
    public List<UserProfile> getTimeline(@PathParam("user_id") String user_id,
            @DefaultValue("50") @QueryParam("limit") int limit,
            @QueryParam("anchor") UserProfileID anchor ) {
        this.userGraph.verifyUser(user_id);
        return this.feedService.getFeedFor(new User(user_id), anchor, limit);
    }

    
}
