package com.wanderlust.content;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileError;
import com.wanderlust.api.ServiceException;

public class EventValidator implements EventValidation {

    public void validateContent(UserProfile proposal) {
        final String event = proposal.getEvent();

        if( event == null || event.length() == 0 ) {
            throw new ServiceException(UserProfileError.INVALID_CONTENT).set("event", event);
        }
    }
}

