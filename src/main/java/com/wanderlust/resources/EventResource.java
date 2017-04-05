package com.wanderlust.resources;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


import com.mongodb.DBObject;
import com.wanderlust.api.Event;

import com.wanderlust.api.ExtendedEventService;
import com.wanderlust.api.ExtendedUserService;
import com.wanderlust.services.ProfileService;
import com.wanderlust.services.EventService;
import com.wanderlust.services.TimelineService;
import com.wanderlust.services.UserService;
import com.wanderlust.util.JSONParam;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	private final EventService eventGraph;

	private final UserService userGraph; 

	public EventResource(ProfileService content, TimelineService feed, EventService events, UserService users) {
		this.eventGraph = events;
		this.userGraph = users;
	}

	@GET
	@Path("/{event_id}")
	public DBObject getEventInfo(@PathParam("event_id") String event_id ) throws UnknownHostException {
		DBObject result = ExtendedEventService.showEventInfo(event_id);
		return result;
	}
/*
	@GET
	@Path("/AllEvents")
	public List<Event> getAll(@QueryParam("limit") int limit) {
		return this.eventGraph.getAllEvent(limit);
	}
*/

	@GET
	@Path("/AllEvents")
	public ArrayList<DBObject> getEventInfo2() throws UnknownHostException {
	return ExtendedEventService.showEventInform();

	}
	
	@PUT
	@Path("/{event_id}/about")
	public DBObject setEventAbout(
			@PathParam("event_id") String event_id, @QueryParam("about")String about) throws UnknownHostException {
		DBObject result = ExtendedEventService.updateEventAbout(event_id, about);
		return result;
	}

	@PUT
	@Path("/{event_id}/author")
	public DBObject setEventAuthor(
			@PathParam("event_id") String event_id, @QueryParam("auth")String author) throws UnknownHostException {
		DBObject result = ExtendedEventService.updateEventAuthor(event_id, author);
		return result;
	}

	@PUT
	@Path("/{event_id}/location")
	public DBObject setEventLocation(
			@PathParam("event_id") String event_id, @QueryParam("loc")String location) throws UnknownHostException {
		DBObject result = ExtendedEventService.updateEventLocation(event_id, location);
		return result;
	}

	@PUT
	@Path("/{event_id}/name")
	public DBObject setEventName(
			@PathParam("event_id") String event_id, @QueryParam("n")String name) throws UnknownHostException {
		DBObject result = ExtendedEventService.updateEventName(event_id, name);
		return result;
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
	public void deleteEvent(
			@PathParam("event_id") String eventId) throws UnknownHostException {
		ExtendedEventService.removeEvent(eventId);
	}

	@DELETE
	@Path("/{event_id}/members")
	public void deleteMember(
			@PathParam("event_id") String eventId, @QueryParam("remove") String userid) throws UnknownHostException {
		ExtendedEventService.removeEventMember(eventId, userid);
	}

	@GET
	@Path("/{event_id}/members")
	public List<String> getMembers(@PathParam("event_id") String event_id,
			@DefaultValue("50") @QueryParam("limit") int limit) throws UnknownHostException {
		ArrayList<String> result = ExtendedEventService.showEventMembers(event_id);
		return result;
	}

	@PUT
	@Path("/{event_id}/member")
	public DBObject setEventMembers(
			@PathParam("event_id") String event_id, @QueryParam("member") String userid) throws UnknownHostException {          
		this.userGraph.verifyUser(userid);
		DBObject result = ExtendedEventService.updateEventMember(event_id, userid);
		return result;
	}

	@PUT
	@Path("/{event_id}/coordinates")
	public DBObject setEventCoordinates(
			@PathParam("event_id") String event_id, @QueryParam("latlon") List<String> latlon) throws UnknownHostException {
		DBObject result = ExtendedEventService.updateEventCoord(event_id, latlon);
		return result;
	}

	@PUT
	@Path("/{event_id}/languages")
	public DBObject setEventLanguages(
			@PathParam("event_id") String event_id, @QueryParam("languages") String languages) throws UnknownHostException {
		DBObject result = ExtendedEventService.updateEventLanguages(event_id, languages);
		return result;
	}
}
