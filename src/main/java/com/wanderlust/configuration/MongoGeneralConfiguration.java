package com.wanderlust.configuration;

import com.mongodb.MongoClientURI;
import com.yammer.dropwizard.config.Configuration;

public class MongoGeneralConfiguration extends Configuration {

    public MongoClientURI default_database_uri = new MongoClientURI("mongodb://localhost:27017/");
}
