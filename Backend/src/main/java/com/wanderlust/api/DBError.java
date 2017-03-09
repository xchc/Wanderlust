package com.wanderlust.api;

import javax.ws.rs.core.Response.Status;

public enum DBError implements ServiceException.ErrorCode{
			
    CANNOT_CONNECT(4001);
	
    private final Status response;
    private final int number;
    
    DBError(final Status responseStatus, final int errorNumber){
        this.response = responseStatus;
        this.number = errorNumber;
    }
    
    DBError(final int errorNumber){
        this(Status.INTERNAL_SERVER_ERROR, errorNumber);
    }
    
    public int getErrorNumber(){
        return this.number;
    }
    
  
    public Status getResponseStatus(){
        return this.response;
    }
}	