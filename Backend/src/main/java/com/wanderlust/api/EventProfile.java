package com.wanderlust.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DBObject;
import com.wanderlust.util.JSONParam;

import org.bson.types.ObjectId;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventProfile extends MongoDataObject {

	public static final String ID_KEY = "_id";
	public static final String AUTHOR_KEY = "_a";
	public static final String MESSAGE_KEY = "_m";
	public static final String DATA_KEY = "_d";
	
    public EventProfile(final DBObject obj) {
        super(obj);
    }

    public EventProfile(final Event author, 
    		final String message, final String event, final JSONParam data) {
        super();
        _dbObject.put(ID_KEY, new ObjectId());
        _dbObject.put(AUTHOR_KEY, author.getEventId());
        if(data != null)
        	_dbObject.put(DATA_KEY, data.toDBObject());
        if(message != null)
            _dbObject.put(MESSAGE_KEY, message);        	
    }

    
    @JsonIgnore
	public EventProfileID getContentId() {
		return new EventProfileID(this);
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

    
    @JsonProperty("data")
    public DBObject getContent() {
        return (DBObject) _dbObject.get(DATA_KEY);
    }
}
