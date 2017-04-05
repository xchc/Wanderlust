package com.wanderlust.services;

import java.util.concurrent.TimeUnit;

import com.yammer.dropwizard.config.Configuration;

public interface Service {
    
    public Configuration getConfiguration();  
    
    public void shutdown(long timeout, TimeUnit unit);
}
