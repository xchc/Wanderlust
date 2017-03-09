package com.wanderlust.content;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileError;
import com.wanderlust.api.ServiceException;

public class MessageValidator implements MessageValidation {

    public void validateContent(UserProfile proposal) {
        final String message = proposal.getMessage();

        if( message == null || message.length() == 0 ) {
            throw new ServiceException(UserProfileError.INVALID_CONTENT).set("message", message);
        }
    }
}
