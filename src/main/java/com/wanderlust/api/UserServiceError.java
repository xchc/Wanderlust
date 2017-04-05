package com.wanderlust.api;

import javax.ws.rs.core.Response.Status;

public enum UserServiceError implements ServiceException.ErrorCode{
			
    INVALID_USER_ID(1001),
    USER_ALREADY_EXISTS(1002),
    UNKNOWN_USER(1003);
	
    private final Status response;
    private final int number;
    
    UserServiceError(final Status responseStatus, final int errorNumber){
        this.response = responseStatus;
        this.number = errorNumber;
    }
    
    UserServiceError(final int errorNumber){
        this(Status.INTERNAL_SERVER_ERROR, errorNumber);
    }
    
  
    public int getErrorNumber(){
        return this.number;
    }

    public Status getResponseStatus(){
        return this.response;
    }
}
	