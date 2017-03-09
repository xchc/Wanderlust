package com.wanderlust.users;

import com.wanderlust.api.Event;
import com.wanderlust.api.ServiceException;
import com.wanderlust.api.UserServiceError;

public class BasicEventIdVerifier implements EventVerifier {

	
	public void verify(final Event entry){
		
		final String eventId = entry.getEventId();
		
        if( eventId == null || eventId.length() == 0 ) {
            throw new ServiceException(UserServiceError.INVALID_USER_ID).set("userId", eventId);
        }		
	}
}
