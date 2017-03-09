package com.wanderlust.configuration;

public class DefaultEventServiceConfiguration extends MongoServiceConfiguration {

	public boolean maintain_subscriber_collection = true;

	public String subscriber_collection_name = "Esubscribers";
	
	public boolean maintain_subscribing_collection = true;
	
	public String subscribing_collection_name = "Esubscribing";

	public boolean maintain_reverse_index = false;
	
	public boolean store_subscribe_counts_with_event = false;
	
	public String event_collection_name = "events";

	public String event_validation_class = "com.wanderlust.events.";
	
}
