package com.wanderlust;

import java.util.concurrent.TimeUnit;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.wanderlust.api.DBError;
import com.wanderlust.api.ServiceException;
import com.wanderlust.configuration.MongoServiceConfiguration;
import com.wanderlust.services.Service;

public abstract class MongoBackedService implements Service {

    protected final MongoClient client;
    protected final DB database;
    
    public MongoBackedService(
            MongoClientURI defaultURI, 
            MongoServiceConfiguration config) {
        
        MongoClientURI uri = defaultURI;
        if(config.database_uri.isEmpty() == false){
            uri = new MongoClientURI(config.database_uri);
        }
        
        String databaseName = uri.getDatabase();
        if(databaseName == null || databaseName.isEmpty()){
            databaseName = config.database_name;
        }
        
        try {
            this.client = new MongoClient(uri);
            this.database = client.getDB(databaseName);
        } catch (Exception e) {
            throw ServiceException.wrap(e, DBError.CANNOT_CONNECT);
        }
    }

    public void shutdown(long timeout, TimeUnit unit) {
        if(this.client != null){
            client.close();
        }        
    }

}
