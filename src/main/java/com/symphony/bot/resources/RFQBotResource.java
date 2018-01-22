package com.symphony.bot.resources;

import com.symphony.bot.POJO.RFQ;
import com.symphony.bot.SymphonyConfiguration;
import com.symphony.bot.bots.RFQBot;
import com.symphony.bot.utils.SymphonyAuth;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/IOIBot")
public class RFQBotResource {

    private SymphonyConfiguration config;
    private SymphonyClient symClient;
    private final Logger LOG = LoggerFactory.getLogger(RFQBotResource.class);
    private RFQBot RFQBot;

    public RFQBotResource(SymphonyConfiguration config) {
        this.config = config;
        try {
            SymphonyClient symClient = new SymphonyAuth().init(config);
            RFQBot = RFQBot.getInstance(symClient, config);

        } catch (Exception e) {
            LOG.error("error", e);
        }
    }


    @POST
    @Path("/sendIOI")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendIOI(RFQ RFQReceived) {
        RFQ RFQ = RFQBot.sendIOI(RFQReceived);

        return Response.status(Response.Status.OK).entity(RFQ).build();
    }

    @POST
    @Path("/startPricing")
    public Response startPricing(RFQ rfq) {
        RFQBot.startPricing(rfq.getId().toString(), rfq.getTraderEmail());

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/price")
    public Response sendPrice(RFQ rfq) {
        RFQ result = RFQBot.sendPrice(rfq.getId().toString(), rfq.getPrice());

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @POST
    @Path("/confirm/")
    public Response confirmIOI(ObjectId id) {
        RFQ result = RFQBot.confirmRFQ(id.toString());

        return Response.status(Response.Status.OK).entity(result).build();
    }
}
