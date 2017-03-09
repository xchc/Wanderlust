package com.wanderlust.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscribingCount {
	
	private int _count = 0;
	private String _user = null;
	
	public SubscribingCount(final User target, final int count) {
        _count = count;
        _user = target.getUserId();
    }


    @JsonProperty
    public String getUserName() {
        return _user;
    }


    @JsonProperty
    public int getSubscribingCount() {
        return _count;
    }
}
