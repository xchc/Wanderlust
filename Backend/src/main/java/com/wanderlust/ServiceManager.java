package com.wanderlust;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClientURI;
//import com.wanderlust.services.AsyncService;
import com.wanderlust.services.ProfileService;
import com.wanderlust.services.EventService;
import com.wanderlust.services.TimelineService;
import com.wanderlust.services.Service;
import com.wanderlust.services.UserService;
import com.yammer.dropwizard.lifecycle.Managed;

public class ServiceManager implements Managed{

    private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    public static final String MODEL_KEY = "model";
    public static final String ASYNC_SERVICE_KEY = "async_service";
    public static final String FEED_SERVICE_KEY = "feed_service";
    public static final String CONTENT_SERVICE_KEY = "content_service";
    public static final String USER_SERVICE_KEY = "user_graph_service";
    public static final String EVENT_SERVICE_KEY = "event_graph_service";
    public static final String FEED_PROCESSING_KEY = "feed_processing";

    private static final String DEFAULT_FEED_PROCESSING = null;
    private static final String DEFAULT_ASYNC_SERVICE = null;
    private static final String DEFAULT_FEED_SERVICE = "GraphConstraint";
    private static final String DEFAULT_CONTENT_SERVICE = "DefaultContentService";
    private static final String DEFAULT_USER_SERVICE = "DefaultUserService";
    private static final String DEFAULT_EVENT_SERVICE = "DefaultEventService";
    
    private static final long SERVICE_SHUTDOWN_TIMEOUT = 30; // Seconds;
    
    private final Map<String, Object> svcConfig;
    private final ServiceFactory factory;
    private final MongoClientURI defaultDbUri;

    public ServiceManager(Map<String, Object> svcConfig, MongoClientURI defaultUri) {

        this.svcConfig = svcConfig;
        this.factory = new ServiceFactory();
        this.defaultDbUri = defaultUri;
        
        logger.info("Initializing configured services");
       
        Map<String, Object> asyncServiceConfig = getServiceConfig(ASYNC_SERVICE_KEY, DEFAULT_ASYNC_SERVICE);
        if(asyncServiceConfig != null){
        
        }
        
        // Load the configured UserGraphService implementation
        Map<String, Object> userServiceConfig = getServiceConfig(USER_SERVICE_KEY, DEFAULT_USER_SERVICE);
        factory.createAndRegisterService(
                UserService.class, userServiceConfig, this.defaultDbUri);
                
        // Load the configured ContentService implementation
        Map<String, Object> contentServiceConfig = getServiceConfig(CONTENT_SERVICE_KEY, DEFAULT_CONTENT_SERVICE);
        factory.createAndRegisterService(
                ProfileService.class, contentServiceConfig, this.defaultDbUri);
        
        // Load the configured UserGraphService implementation
        Map<String, Object> eventServiceConfig = getServiceConfig(EVENT_SERVICE_KEY, DEFAULT_EVENT_SERVICE);
        factory.createAndRegisterService(
                EventService.class, eventServiceConfig, this.defaultDbUri);
        
        // Load the configured FeedService implementation passing
        // the UserGraph and Content service as arguments
        Map<String, Object> feedServiceConfig = getServiceConfig(FEED_SERVICE_KEY, DEFAULT_FEED_SERVICE);
        factory.createAndRegisterService(
                TimelineService.class, feedServiceConfig, this.defaultDbUri);
        
        // Load the configured feed processor
        Map<String, Object> feedProcessorConfig = getServiceConfig(FEED_PROCESSING_KEY, DEFAULT_FEED_PROCESSING);
        if(feedProcessorConfig != null){
            
            factory.createAndRegisterService(
                    TimelineService.class, feedProcessorConfig, this.defaultDbUri);      
        }
    }
    
   // public AsyncService getAsyncService() {
  //      return factory.getService(AsyncService.class);
   // }

    public UserService getUserGraphService() {
        return factory.getService(UserService.class);
    }

    public ProfileService getContentService() {
        return factory.getService(ProfileService.class);
    }

    public TimelineService getFeedService() {
        return factory.getService(TimelineService.class);
    }
    
    public EventService getEventGraphService() {
        return factory.getService(EventService.class);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getServiceConfig(final String serviceKey, final String defaultServiceKey){
        Map<String, Object> configItem = (Map<String, Object>) svcConfig.get(serviceKey);
        
        if(configItem == null && defaultServiceKey != null){
            configItem = new LinkedHashMap<String, Object>();
            configItem.put(MODEL_KEY, defaultServiceKey);
        }
        
        return configItem;
    }

    public void start() throws Exception {
    }

    public void stop() throws Exception {
        logger.info("Stopping configured services");            
        List<? extends Service> services = factory.getServiceList();


        
        for(Service service : services)
            service.shutdown(SERVICE_SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        logger.info("All services shut down successfully");
    }

}
