package com.wanderlust.users;

import com.wanderlust.api.ServiceException;
import com.wanderlust.api.User;
import com.wanderlust.api.UserServiceError;

public class BasicUserIdVerifier implements UserVerifier {

	
	public void verify(final User entry){
		
		final String userId = entry.getUserId();
		
        if( userId == null || userId.length() == 0 ) {
            throw new ServiceException(UserServiceError.INVALID_USER_ID).set("userId", userId);
        }		
	}
}
