package com.wanderlust.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DBObject;
import com.wanderlust.util.JSONParam;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event extends MongoDataObject {

	public static final String ID_KEY = "_id";
	public static final String DATA_KEY = "_d";
	public static final String COLLECTION_NAME = "events";
	
    public Event() {
        super();
    }

    public Event(DBObject eventData) {
        super(eventData);
    }

    public Event(String eventId) {
        super();
        _dbObject.put(ID_KEY, eventId);
    }

    public Event(String eventId, JSONParam eventData) {
        super();
        _dbObject.put(ID_KEY, eventId);
        if(eventData != null)
        	_dbObject.put(DATA_KEY, eventData.toDBObject());
    }
    
    @JsonProperty("_id")
    public String getEventId() {
        return (String)_dbObject.get(ID_KEY);
    }

    @JsonProperty("_id")
    public void setEventId(String eventId) {
        _dbObject.put(ID_KEY, eventId);
    }

    @JsonProperty("_d")
    public DBObject getEventData() {
        return (DBObject)_dbObject.get(DATA_KEY);
    }

    @JsonProperty("_d")
    public void setEventData(JSONParam eventData) {
        if(eventData != null) {
            _dbObject.put(DATA_KEY,eventData.toDBObject());
        }
    }
}
