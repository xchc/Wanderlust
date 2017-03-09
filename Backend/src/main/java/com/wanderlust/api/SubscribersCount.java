package com.wanderlust.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscribersCount {
	private int _count = 0;
	private String _user = null;
	
	public SubscribersCount(final User target, final int count) {
        _count = count;
        _user = target.getUserId();
    }

    @JsonProperty
    public String getUserName() {
        return _user;
    }

    @JsonProperty
    public int getSubscriberCount() {
        return _count;
    }
}
