package com.symphony.bot.resources;

import com.symphony.bot.SymphonyConfiguration;
import com.symphony.bot.utils.AppAuthClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AppAuthResource {

    private final Logger LOG = LoggerFactory.getLogger(RFQInfoResource.class);
    private SymphonyConfiguration config;

    public AppAuthResource(SymphonyConfiguration config) {
        this.config = config;
    }

    @POST
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initAuth(JSONObject object) {
        String podId = object.getString("podId");

        AppAuthClient appAuthClient = new AppAuthClient();
        JSONObject response = appAuthClient.authenticate(podId);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @POST
    @Path("/verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyJWT(JSONObject object) {
        String podId = object.getString("podId");
        String JWT = object.getString("JWT");

        AppAuthClient appAuthClient = new AppAuthClient();
        JSONObject response = appAuthClient.verify(podId, JWT);
        return Response.status(Response.Status.OK).entity(response).build();
    }
}
