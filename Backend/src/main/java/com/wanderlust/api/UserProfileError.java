package com.wanderlust.api;

import javax.ws.rs.core.Response.Status;

public enum UserProfileError implements ServiceException.ErrorCode{
			
    INVALID_CONTENT(Status.FORBIDDEN, 2001),
    CONTENT_NOT_FOUND(Status.NOT_FOUND, 2002);
	
    private final Status response;
    private final int number;
    
    UserProfileError(final Status responseStatus, final int errorNumber){
        this.response = responseStatus;
        this.number = errorNumber;
    }
    
    UserProfileError(final int errorNumber){
        this(Status.INTERNAL_SERVER_ERROR, errorNumber);
    }
    
 
    public int getErrorNumber(){
        return this.number;
    }
    
    public Status getResponseStatus(){
        return this.response;
    }
}
	