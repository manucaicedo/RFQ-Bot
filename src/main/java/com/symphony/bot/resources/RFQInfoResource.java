package com.symphony.bot.resources;

import com.symphony.bot.POJO.RFQ;
import com.symphony.bot.SymphonyConfiguration;
import com.symphony.bot.mongo.MongoDBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/IOIInfo")
public class RFQInfoResource {

    private SymphonyConfiguration config;
    private SymphonyClient symClient;
    private final Logger LOG = LoggerFactory.getLogger(RFQInfoResource.class);
    private MongoDBClient mongoDBClient;

    public RFQInfoResource(SymphonyConfiguration config) {
        this.config = config;
        this.mongoDBClient = new MongoDBClient();
    }

    @GET
    @Path("/ioi/{ioiId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startPricing(@PathParam("ioiId") String ioiId) {
        RFQ RFQ = mongoDBClient.getRFQ(ioiId);

        return Response.status(Response.Status.OK).entity(RFQ).build();
    }

    @GET
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startPricing() {
        List<RFQ> rfqs = mongoDBClient.getPending();

        return Response.status(Response.Status.OK).entity(rfqs).build();
    }
}
