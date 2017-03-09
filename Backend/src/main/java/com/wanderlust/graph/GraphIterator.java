package com.wanderlust.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wanderlust.api.User;

public class GraphIterator {

    public User user = null;

    public final Set<Long> subscriptions = new HashSet<Long>();

    public List<User> getSubscribers() {
        List<User> users = new ArrayList<User>();
        for( long i : subscriptions ) {
            users.add( new User(String.valueOf(i)));
        }
        return users;
    }

}
