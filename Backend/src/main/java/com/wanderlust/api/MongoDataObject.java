package com.wanderlust.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public abstract class MongoDataObject {
	
	@Override
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof MongoDataObject))
            return false;

        MongoDataObject rhs = (MongoDataObject) obj;
        return this._dbObject.equals(rhs._dbObject);
	}

	@Override
	public int hashCode() {
		return _dbObject.hashCode();
	}

	@Override
	public String toString() {
		return _dbObject.toString();
	}

	protected DBObject _dbObject = null;
	
	public MongoDataObject(DBObject dbObj){
		if(dbObj != null)
			_dbObject = dbObj;
		else
			_dbObject = new BasicDBObject();
	}
	
	public MongoDataObject(){
		_dbObject = new BasicDBObject();
	}
	
	@JsonIgnore
	public DBObject toDBObject(){		
		return _dbObject;
	}	
}
