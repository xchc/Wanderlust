package com.wanderlust.api;

import javax.ws.rs.core.Response.Status;

public enum EventServiceError implements ServiceException.ErrorCode{
			
    INVALID_EVENT_ID(1001),
    EVENT_ALREADY_EXISTS(1002),
    UNKNOWN_EVENT(1003);
	
    private final Status response;
    private final int number;
    
    EventServiceError(final Status responseStatus, final int errorNumber){
        this.response = responseStatus;
        this.number = errorNumber;
    }
    
    EventServiceError(final int errorNumber){
        this(Status.INTERNAL_SERVER_ERROR, errorNumber);
    }
    
  
    public int getErrorNumber(){
        return this.number;
    }

    public Status getResponseStatus(){
        return this.response;
    }
}
	