package com.wanderlust.configuration;

public class DefaultUserServiceConfiguration extends MongoServiceConfiguration {


	public boolean maintain_subscriber_collection = true;
	

	public String subscriber_collection_name = "subscribers";
	

	public boolean maintain_subscribing_collection = true;
	

	public String subscribing_collection_name = "subscribing";


	public boolean maintain_reverse_index = false;
	

	public boolean store_follow_counts_with_user = false;
	
	
	public String user_collection_name = "users";
	

	public String user_validation_class = "com.wanderlust.users.";
	
}
