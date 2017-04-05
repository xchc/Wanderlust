package com.wanderlust.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.wanderlust.api.GeneralError;
import com.wanderlust.api.ServiceException;

import java.util.Map;

public class JSONParam {
	
    private DBObject dbObject = null;

    public JSONParam(String json){
        try {
        	dbObject = (DBObject) JSON.parse(json);
        }
        catch (Exception ex) {
            throw ServiceException.wrap(ex, GeneralError.CANNOT_PARSE_JSON).
            	set("json", json);
        }
    }

    @JsonCreator
    public JSONParam(Map<String,Object> props) {
        dbObject = new BasicDBObject();
        dbObject.putAll(props);
    }

    public DBObject toDBObject() {
        return dbObject;
    }
}
