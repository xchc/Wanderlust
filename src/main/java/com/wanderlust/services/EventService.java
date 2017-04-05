package com.wanderlust.services;

import java.util.List;

import com.wanderlust.api.User;
import com.wanderlust.api.ServiceException;
import com.wanderlust.api.Event;

public interface EventService extends Service
{

    public void verifyEvent(String eventId) throws ServiceException;

    public void createEvent(Event event) throws ServiceException;

    public void removeEvent(String eventId);

    public Event getEventById(String eventId) throws ServiceException;

    public Event getOrCreateEventById(String eventId);

    public void subscribe(User from, Event to);

    public void unsubscribe(User from, Event to);

    public List<User> getSubscribers(Event event, int limit);

    public List<Event> getAllEvents();

    public List<Event> getAllEvent(int limit);
    

}
