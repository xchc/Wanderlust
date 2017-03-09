package com.wanderlust.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;
import com.wanderlust.services.ProfileService;

@Path("/content")
@Produces(MediaType.APPLICATION_JSON)
public class ProfileResource {

    private final ProfileService content;

    public ProfileResource(ProfileService content) {
        this.content = content;
    }

    @GET
    @Path("/{content_id}")
    public UserProfile get(@PathParam("content_id") String content_id ) {

        UserProfileID id = new UserProfileID(content_id);
        return this.content.getContentById(id);
    }

}
