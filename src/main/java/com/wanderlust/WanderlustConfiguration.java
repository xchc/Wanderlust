package com.wanderlust;

import java.util.LinkedHashMap;
import java.util.Map;

import com.wanderlust.configuration.MongoGeneralConfiguration;
import com.yammer.dropwizard.config.Configuration;

public class WanderlustConfiguration extends Configuration {  
	public MongoGeneralConfiguration mongodb = new MongoGeneralConfiguration();
	public Map<String, Object> services = new LinkedHashMap<String, Object>();

}
