package com.wanderlust.services;

import java.util.List;

import com.wanderlust.api.SubscribersCount;
import com.wanderlust.api.SubscribingCount;
import com.wanderlust.api.Event;
import com.wanderlust.api.ServiceException;
import com.wanderlust.api.User;

public interface UserService extends Service
{
    public void verifyUser(String userId) throws ServiceException;

    public void createUser(User user) throws ServiceException;

    public void removeUser(String userId);

    public User getUserById(String userId) throws ServiceException;

    public User CreateUserById(String userId);

    public List<User> getAllUser(int limit);
    
    public void subscribe(User from, User user);
    
    public void createtEvent(Event event) throws ServiceException;
    
    public void joinEvent(User from, Event to);
    
    public void withdrawfromEvent(User from, Event to);

    public void unsubscribe(User from, User to);

    public SubscribersCount getSubscriberCount(User user);

    public List<User> getSubscribers(User user, int limit);

    public SubscribingCount getSubscribingCount(User u);

    public List<User> getSubscribing(User user, int limit);

    public List<User> getAllUsersButOne(User user, int limit);
}
