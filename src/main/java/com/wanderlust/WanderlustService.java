package com.wanderlust;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.mongodb.MongoClientURI;
import com.wanderlust.resources.EventResource;
//import com.wanderlust.resources.EventResource;
//import com.wanderlust.client.*;
import com.wanderlust.resources.UserResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class WanderlustService extends Service<WanderlustConfiguration> {


	@JsonAutoDetect(fieldVisibility=Visibility.NONE, creatorVisibility=Visibility.NONE)
	abstract class IgnoreBasicDBObjMap {}


	public static void main(String[] args) throws Exception {
		new WanderlustService().run(args);
	}

	@Override
	public void initialize(Bootstrap<WanderlustConfiguration> configBootstrap) {}

	@Override
	public void run(WanderlustConfiguration config, Environment environment) throws Exception {

		MongoClientURI default_uri = config.mongodb.default_database_uri;

		ServiceManager services = new ServiceManager(config.services, default_uri);
		environment.manage(services);

		environment.addProvider(new ServiceExceptionMapper());

		environment.addResource( new UserResource( services.getContentService(),
				services.getTimelineService(), services.getUserGraphService()) );

		environment.addResource( new EventResource( services.getContentService(),
				services.getTimelineService(), services.getEventGraphService(), services.getUserGraphService() ) );
	}
}
