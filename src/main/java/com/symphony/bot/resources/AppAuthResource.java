package com.symphony.bot.resources;

import com.symphony.bot.POJO.AppAuthResponse;
import com.symphony.bot.POJO.User;
import com.symphony.bot.POJO.VerifyRequest;
import com.symphony.bot.SymphonyConfiguration;
import com.symphony.bot.utils.AppAuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/auth")
public class AppAuthResource {

    private final Logger LOG = LoggerFactory.getLogger(RFQInfoResource.class);
    private SymphonyConfiguration config;
    private AppAuthClient appAuthClient;

    public AppAuthResource(SymphonyConfiguration config) {
        this.config = config;
        appAuthClient = new AppAuthClient(config);
    }

    @POST
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initAuth(String podId) {
        if(podId!=null) {
            appAuthClient = new AppAuthClient(config);
            AppAuthResponse response = null;
            try {
                response = appAuthClient.authenticate(podId);
            } catch (Exception e) {
                return Response.status(500).entity("Something went wrong").build();
            }
            return Response.status(Response.Status.OK).entity(response).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Pod id must be specified").build();
        }
    }

    @POST
    @Path("/verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyJWT(VerifyRequest object) {

        appAuthClient = new AppAuthClient(config);
        Object response = null;
        try {
            response = appAuthClient.verify(object);
        } catch (Exception e) {
            return Response.status(500).build();
        }
        return Response.status(Response.Status.OK).entity(response).build();
    }
}
