package com.wanderlust.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DBObject;
import com.wanderlust.util.JSONParam;

import org.bson.types.ObjectId;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfile extends MongoDataObject {

	public static final String ID_KEY = "_id";
	public static final String AUTHOR_KEY = "_a";
	public static final String MESSAGE_KEY = "_m";
	public static final String EVENT_KEY = "_e";
	public static final String DATA_KEY = "_d";
	
    public UserProfile(final DBObject obj) {
        super(obj);
    }

    public UserProfile(final User author, 
    		final String message, final String event, final JSONParam data) {
        super();
        _dbObject.put(ID_KEY, new ObjectId());
        _dbObject.put(AUTHOR_KEY, author.getUserId());
        if(data != null)
        	_dbObject.put(DATA_KEY, data.toDBObject());
        if(event != null)
            _dbObject.put(EVENT_KEY, event);    
        if(message != null)
            _dbObject.put(MESSAGE_KEY, message);        	
    }

    
    @JsonIgnore
	public UserProfileID getContentId() {
		return new UserProfileID(this);
	}

    @JsonIgnore
    public Object getId() {
        return _dbObject.get(ID_KEY);
    }

    @JsonProperty("_id")
    public String getIdAsString() {
        return _dbObject.get("_id").toString();
    }

    @JsonProperty("date")
    public Date getDate() {
        long ms = ((ObjectId)_dbObject.get(ID_KEY)).getTimestamp();
        Date d = new Date();
        d.setTime(ms);
        return d;
    }

    @JsonProperty("author")
    public String getAuthorId() {
        return (String) _dbObject.get(AUTHOR_KEY);
    }

    @JsonProperty("message")
    public String getMessage() {
        return (String) _dbObject.get(MESSAGE_KEY);
    }

    @JsonProperty("event")
    public String getEvent() {
        return (String) _dbObject.get(EVENT_KEY);
    }
    
    @JsonProperty("data")
    public DBObject getContent() {
        return (DBObject) _dbObject.get(DATA_KEY);
    }
}
