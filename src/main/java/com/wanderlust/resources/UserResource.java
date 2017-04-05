package com.wanderlust.resources;
import javax.ws.rs.*;


import javax.ws.rs.core.MediaType;
import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.mongodb.DBObject;
import com.wanderlust.api.ExtendedUserService;
import com.wanderlust.api.SubscribingCount;
import com.wanderlust.api.User;
import com.wanderlust.services.ProfileService;
import com.wanderlust.services.TimelineService;
import com.wanderlust.services.UserService;
import com.wanderlust.util.JSONParam;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserService userGraph;
	private final TimelineService feedService;
	private final ProfileService contentService;

	public UserResource(ProfileService content, TimelineService feed, UserService users) {
		this.userGraph = users;
		this.feedService = feed;
		this.contentService = content;
	}

	/*  	
    @GET
    @Path("/{user_id}")
    public User get(@PathParam("user_id") String user_id ) {

        User user = this.userGraph.getUserById(user_id);
        return user;
    }
	 */


	@GET
	@Path("/{user_id}")
	public DBObject getUserInfo(@PathParam("user_id") String user_id ) throws UnknownHostException {
		DBObject result = ExtendedUserService.showUserInfo(user_id);
		return result;
	}
	
	@GET
	@Path("/{user_id}/requests")
	public DBObject getUserReq(@PathParam("user_id") String user_id ) throws UnknownHostException {
		DBObject result = ExtendedUserService.showUserRequests(user_id);
		return result;
	}
	
	@GET
	@Path("/{user_id}/Languages")
	public DBObject getUserLang(@PathParam("user_id") String user_id ) throws UnknownHostException {
		DBObject result = ExtendedUserService.showUserLanguages(user_id);
		return result;
	}
	
	
	@GET
	@Path("/{user_id}/Interests")
	public DBObject getUserInt(@PathParam("user_id") String user_id ) throws UnknownHostException {
		DBObject result = ExtendedUserService.showUserInterests(user_id);
		return result;
	}

	
	
	@GET
	@Path("/AllUsers")
	public ArrayList<DBObject> getUserinfoFN() throws UnknownHostException {
	return ExtendedUserService.showUserFn();

	}

	@PUT
	@Path("/{user_id}/age")
	public DBObject setUserAge(
			@PathParam("user_id") String userId, @QueryParam("age")String age) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserAge(userId, age);
		return result;
	}


	@PUT
	@Path("/{user_id}/about")
	public DBObject setUserDescription(
			@PathParam("user_id") String userId, @QueryParam("description")String description) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserAbout(userId, description);
		return result;
	}

	@PUT
	@Path("/{user_id}/language")
	public DBObject setUserLanguage(
			@PathParam("user_id") String userId, @QueryParam("language")List<String> language) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserLanguage(userId, language);
		return result;
	}

	@PUT
	@Path("/{user_id}/fname")
	public DBObject setUserNfame(
			@PathParam("user_id") String userId, @QueryParam("fn")String fn) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserFName(userId, fn);
		return result;
	}

	@PUT
	@Path("/{user_id}/coordinates")
	public DBObject setUserCoordinates(
			@PathParam("user_id") String user_id, @QueryParam("latlon") List<String> latlon) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserCoord(user_id, latlon);
		return result;
	}
	
	@PUT
	@Path("/{user_id}/lname")
	public DBObject setUserlName(
			@PathParam("user_id") String userId, @QueryParam("ln")String ln) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserLName(userId, ln);
		return result;
	}


	@PUT
	@Path("/{user_id}/interests")
	public DBObject setUserlInterests(
			@PathParam("user_id") String userId, @QueryParam("interests") List<String> interests) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserInterests(userId, interests);
		return result;
	}


	@PUT
	@Path("/{user_id}/origin")
	public DBObject setUserOrigin(
			@PathParam("user_id") String userId, @QueryParam("origin")String origin) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserOrigin(userId, origin);
		return result;
	}


	@PUT
	@Path("/{user_id}/places")
	public DBObject setUserPlaces(
			@PathParam("user_id") String userId, @QueryParam("places")List<String> places) throws UnknownHostException {
		DBObject result = ExtendedUserService.updateUserPlaces(userId, places);
		return result;
	}

/*
	@GET
	@Path("/AllUsers")
	public List<User> getAll(@QueryParam("limit") int limit) {
		return this.userGraph.getAllUser(limit);
	}
*/
	
	
	@GET
	@Path("/{user_id}/AllOtherUsers")
	public List<User> getAllOther(@PathParam("user_id") String user_id, 
			@DefaultValue("50") @QueryParam("limit") int limit) {
		return this.userGraph.getAllUsersButOne(new User(user_id), limit);
	}

	@POST
	@Path("/{user_id}")
	public User create(
			@PathParam("user_id") String userId, 
			@QueryParam("user_data") JSONParam userData ) {

		User newUser = new User(userId, userData);
		this.userGraph.createUser(newUser);
		return newUser;
	}

	@PUT
	@Path("/{user_id}")
	public User updateUser(
			@PathParam("user_id") String userId, 
			@QueryParam("user_data") JSONParam userData ) {
		this.userGraph.verifyUser(userId);
		User updatedUser = new User(userId, userData);
		return updatedUser;
	}


	@POST
	@Path("/{user_id}/subscribeRequest/{target}")
	public void sendRequest(
			@PathParam("user_id") String senderId, 
			@PathParam("target") String receiverId) {
		this.userGraph.verifyUser(senderId);
		this.userGraph.verifyUser(receiverId);
		Boolean f = false;
		try {
			ExtendedUserService.updateFriendStatus(senderId, receiverId, f);
		} catch (UnknownHostException e) {
		}
	}

	@PUT
	@Path("/{target}/RRequest/{user_id}")
	public void rejectRequest(
			@PathParam("user_id") String senderId, 
			@PathParam("target") String receiverId) {
		this.userGraph.verifyUser(senderId);
		this.userGraph.verifyUser(receiverId);
		try {
			if (ExtendedUserService.checkIfRequestExist(senderId, receiverId)){
				ExtendedUserService.removeRequestFromProfile(senderId, receiverId);
				ExtendedUserService.removeListRequestFromProfile(senderId, receiverId);}
		} catch (UnknownHostException e) {
		}
	}

	@PUT
	@Path("/{target}/ARequest/{user_id}")
	public void acceptRequest(
			@PathParam("user_id") String senderId, 
			@PathParam("target") String receiverId) {
		this.userGraph.verifyUser(senderId);
		this.userGraph.verifyUser(receiverId);    
		try {
			if (ExtendedUserService.checkIfRequestExist(senderId, receiverId)){
				//ExtendedUserService.updateFriendStatus(senderId, receiverId, f);
				this.userGraph.subscribe( new User(senderId), new User(receiverId) );
				ExtendedUserService.removeRequestFromProfile(senderId, receiverId);
				ExtendedUserService.removeListRequestFromProfile(senderId, receiverId);}
		} catch (UnknownHostException e) {
		}
	}


	@DELETE
	@Path("/{user_id}")
	public void delete(
			@PathParam("user_id") String userId, 
			@QueryParam("user_data") JSONParam userData ) {

		this.userGraph.removeUser(userId);
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
	@Path("/{user_id}/publicSubscribing/{target}")
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
		User user = new User(user_id);
		UserProfile newContent = new UserProfile(user, message, event, data);

		this.contentService.publishPost(user, newContent);
		this.feedService.post(user, newContent);
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
		return this.feedService.getTimelineFor(new User(user_id), anchor, limit);
	}


}
