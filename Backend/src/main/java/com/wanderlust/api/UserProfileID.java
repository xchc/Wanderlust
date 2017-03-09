package com.wanderlust.api;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DBObject;

public class UserProfileID extends MongoDataObject {
	
	
    public UserProfileID(final String stringId) {
        super();
        this._dbObject.put(UserProfile.ID_KEY, new ObjectId(stringId));
                
    }

    public UserProfileID(final DBObject obj) {
        super(obj);
    }

    public UserProfileID(final UserProfile profile) {
        super();
        this._dbObject.put(UserProfile.ID_KEY, profile.getId());
        this._dbObject.put(UserProfile.AUTHOR_KEY, profile.getAuthorId());
    }

    public UserProfileID(final ObjectId profileId) {
        super();
        this._dbObject.put(UserProfile.ID_KEY, profileId);
    }

    @JsonIgnore
    public Object getId() {
        return _dbObject.get(UserProfile.ID_KEY);
    }

    @JsonProperty("_id")
    public String getIdAsString() {
        return _dbObject.get(UserProfile.ID_KEY).toString();
    }

    @JsonProperty("author")
    public String getAuthorId() {
        return (String) _dbObject.get(UserProfile.AUTHOR_KEY);
    }
}
