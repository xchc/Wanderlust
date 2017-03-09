package com.wanderlust.api;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DBObject;

public class EventProfileID extends MongoDataObject {
	
	
    public EventProfileID(final String stringId) {
        super();
        this._dbObject.put(EventProfile.ID_KEY, new ObjectId(stringId));
                
    }

    public EventProfileID(final DBObject obj) {
        super(obj);
    }

    public EventProfileID(final EventProfile profile) {
        super();
        this._dbObject.put(EventProfile.ID_KEY, profile.getId());
        this._dbObject.put(EventProfile.AUTHOR_KEY, profile.getAuthorId());
    }

    public EventProfileID(final ObjectId profileId) {
        super();
        this._dbObject.put(EventProfile.ID_KEY, profileId);
    }

    @JsonIgnore
    public Object getId() {
        return _dbObject.get(EventProfile.ID_KEY);
    }

    @JsonProperty("_id")
    public String getIdAsString() {
        return _dbObject.get(EventProfile.ID_KEY).toString();
    }

    @JsonProperty("author")
    public String getAuthorId() {
        return (String) _dbObject.get(EventProfile.AUTHOR_KEY);
    }
}
