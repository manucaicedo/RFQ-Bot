package com.symphony.bot.bots;

import com.symphony.bot.POJO.RFQ;
import com.symphony.bot.SymphonyConfiguration;
import com.symphony.bot.mongo.MongoDBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.events.*;
import org.symphonyoss.client.exceptions.*;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.model.Room;
import org.symphonyoss.client.services.*;
import org.symphonyoss.symphony.clients.model.*;
import org.symphonyoss.symphony.pod.api.StreamsApi;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.V3RoomAttributes;
import org.symphonyoss.symphony.pod.model.V3RoomDetail;


public class RFQBot implements ChatListener, ChatServiceListener, RoomServiceEventListener, RoomEventListener {

    private static RFQBot instance;
    private final Logger logger = LoggerFactory.getLogger(RFQBot.class);
    private SymphonyClient symClient;
    private RoomService roomService;
    SymphonyConfiguration config;
    private MongoDBClient mongoDBClient;

    protected RFQBot(SymphonyClient symClient, SymphonyConfiguration config) {
        this.symClient=symClient;
        this.config = config;
        init();


    }

    public static RFQBot getInstance(SymphonyClient symClient, SymphonyConfiguration config){
        if(instance==null){
            instance = new RFQBot(symClient,config);
        }
        return instance;
    }

    private void init() {

        logger.info("Connections example starting...");

        symClient.getChatService().addListener(this);
        roomService = symClient.getRoomService();
        roomService.addRoomServiceEventListener(this);
        mongoDBClient = new MongoDBClient();
        //Init connection service.
        ConnectionsService connectionsService = new ConnectionsService(symClient);

        //Optional to auto accept connections.
        connectionsService.setAutoAccept(true);

    }


    //Chat sessions callback method.
    @Override
    public void onChatMessage(SymMessage message) {
        if (message == null)
            return;
        logger.debug("TS: {}\nFrom ID: {}\nSymMessage: {}\nSymMessage Type: {}",
                message.getTimestamp(),
                message.getFromUserId(),
                message.getMessage(),
                message.getMessageType());
        SymMessage message2;

        if (message.getMessageText().toLowerCase().contains("rfq")) {
            RFQ rfq = new RFQ();
            rfq.setOriginStreamId(message.getStreamId());
            rfq.setTargetCompany(config.getCompanyName());
            rfq.setSenderEmail(message.getSymUser().getEmailAddress());
            String company=null;
            try {
                company = symClient.getUsersClient().getUserFromId(message.getSymUser().getId()).getCompany();
            } catch (UsersClientException e) {
                e.printStackTrace();
            }
            rfq.setSenderCompany(company);
            rfq.setStatus("new");
            rfq = mongoDBClient.newRFQ(rfq);
            message2 = new SymMessage();

            message2.setEntityData("{\"summary\": { \"type\": \"com.symphsol.mifid\", \"version\":  \"1.0\", \"streamId\":  \""+message.getStreamId()+"\", \"targetCompany\":  \""+config.getCompanyName()+"\", \"id\":  \""+ rfq.getId()+"\", \"client\":  \"true\", \"status\":  \""+ rfq.getStatus()+"\" }}");
            message2.setMessage("<messageML><div class='entity' data-entity-id='summary'><b><i>Please install the MIFID renderer application to render this entity.</i></b></div></messageML>");
            try {
                symClient.getMessagesClient().sendMessage(message.getStream(), message2);
            } catch (MessagesException e) {
                logger.error("Failed to send message", e);
            }

        }



    }



    @Override
    public void onNewChat(Chat chat) {

        chat.addListener(this);

        logger.debug("New chat session detected on stream {} with {}", chat.getStream().getStreamId(), chat.getRemoteUsers());
    }

    @Override
    public void onRemovedChat(Chat chat) {

    }


    @Override
    public void onMessage(SymMessage symMessage) {
        logger.info("Message detected from stream: {} from: {} message: {}",
                symMessage.getStreamId(),
                symMessage.getFromUserId(),
                symMessage.getMessage());

    }

    @Override
    public void onNewRoom(Room room) {
        room.addEventListener(this);
    }

    @Override
    public void onRoomMessage(SymMessage symMessage) {
        String traderRoom = mongoDBClient.getRoomForTargetCompany(config.getCompanyName());
        boolean isExternal=false;
        try {
             isExternal= symClient.getStreamsClient().getStreamAttributes(symMessage.getStreamId()).getCrossPod();
        } catch (StreamsException e) {
            e.printStackTrace();
        }
        if(symMessage.getMessageText().toLowerCase().contains("/setroom") && !isExternal){
            try {
                String company = symClient.getUsersClient().getUserFromId(symMessage.getSymUser().getId()).getCompany();
                mongoDBClient.setRoomForTargetCompany(company,symMessage.getStreamId());
            } catch (UsersClientException e) {
                e.printStackTrace();
            }

        }
        else if (symMessage.getMessageText().equals("init") && symMessage.getStreamId().equals(traderRoom)){
            SymMessage message2 = new SymMessage();

            message2.setEntityData("{\"summary\": { \"type\": \"com.symphsol.mifid\", \"version\":  \"1.0\", \"version\":  \"1.0\", \"targetCompany\":  \""+config.getCompanyName()+"\", \"email\":  \""+ symMessage.getSymUser().getEmailAddress()+"\", \"client\":  \"init\"}}");
            message2.setMessage("<messageML><div class='entity' data-entity-id='summary'><b><i>Please install the MIFID renderer application to render this entity.</i></b></div></messageML>");
            try {
                symClient.getMessagesClient().sendMessage(symMessage.getStream(), message2);
            } catch (MessagesException e) {
                logger.error("Failed to send message", e);
            }
        }
    }

    @Override
    public void onSymRoomDeactivated(SymRoomDeactivated symRoomDeactivated) {

    }

    @Override
    public void onSymRoomMemberDemotedFromOwner(SymRoomMemberDemotedFromOwner symRoomMemberDemotedFromOwner) {

    }

    @Override
    public void onSymRoomMemberPromotedToOwner(SymRoomMemberPromotedToOwner symRoomMemberPromotedToOwner) {

    }

    @Override
    public void onSymRoomReactivated(SymRoomReactivated symRoomReactivated) {

    }

    @Override
    public void onSymRoomUpdated(SymRoomUpdated symRoomUpdated) {

    }

    @Override
    public void onSymUserJoinedRoom(SymUserJoinedRoom symUserJoinedRoom) {
    }

    @Override
    public void onSymUserLeftRoom(SymUserLeftRoom symUserLeftRoom) {

    }

    @Override
    public void onSymRoomCreated(SymRoomCreated symRoomCreated) {

    }


    public RFQ sendIOI(RFQ rfq){
        try {
            rfq.setStatus("pending");
            mongoDBClient.updateRFQ(rfq);

            Stream stream = new Stream();
            stream.setId(rfq.getOriginStreamId());

            SymMessage message2 = new SymMessage();
            message2.setMessage("<messageML><card accent='tempo-text-color--green'>RFQ Sent to "+ rfq.getTargetCompany()+"<br/><hr /><b>"+ rfq.getAction()+" "+ rfq.getNumShares()+" <cash tag='"+ rfq.getSymbol()+"'/> </b></card></messageML>");

            symClient.getMessagesClient().sendMessage(stream, message2);

            message2.setEntityData("{\"summary\": { \"type\": \"com.symphsol.mifid\", \"version\":  \"1.0\", \"client\":  \"false\", \"status\":  \""+ rfq.getStatus()+"\", \"id\":  \""+ rfq.getId()+"\", \"action\":  \""+ rfq.getAction()+"\" , \"symbol\":  \""+ rfq.getSymbol()+"\" " +
                    ", \"numShares\":  \""+ rfq.getNumShares()+"\", \"senderCompany\":  \""+ rfq.getSenderCompany()+"\", \"sender\":  \""+ rfq.getSenderEmail()+"\"}}");
            message2.setMessage("<messageML><div class='entity' data-entity-id='summary'><card accent='tempo-text-color--green'>RFQ Received<br/><hr /><b>"+ rfq.getAction()+" "+ rfq.getNumShares()+" <cash tag='"+ rfq.getSymbol()+"'/> </b></card></div></messageML>");
            String targetStreamId = mongoDBClient.getRoomForTargetCompany(config.getCompanyName());
            Stream IOIRoomStream = new Stream();
            IOIRoomStream.setId(targetStreamId);
            symClient.getMessagesClient().sendMessage(IOIRoomStream, message2);

        } catch (MessagesException e) {
            System.out.println(e);
            logger.error("Failed to send message", e);
        }
        return rfq;
    }

    public void startPricing(String rfqId, String traderEmail){
        RFQ rfq = mongoDBClient.getRFQ(rfqId);
        try {
            StreamsApi streamsApi = new StreamsApi();
            V3RoomAttributes roomAttributes = new V3RoomAttributes();
            roomAttributes.setCopyProtected(false);
            roomAttributes.setCrossPod(true);
            roomAttributes.setDescription("RFQ: "+ rfqId);
            roomAttributes.setDiscoverable(false);
            roomAttributes.setMembersCanInvite(false);
            roomAttributes.setName("RFQ "+ rfq.getSenderCompany()+"-"+ rfq.getTargetCompany()+" : "+ rfq.getAction()+" "+ rfq.getSymbol()+" "+ rfq.getNumShares());
            roomAttributes.setMembersCanInvite(false);
            roomAttributes.setPublic(false);
            roomAttributes.setViewHistory(false);
            V3RoomDetail v3RoomDetail = streamsApi.v3RoomCreatePost(roomAttributes, symClient.getSymAuth().getSessionToken().getToken());

            Room room = new Room();
            room.setId(v3RoomDetail.getRoomSystemInfo().getId());
            room.setStreamId(v3RoomDetail.getRoomSystemInfo().getId());
            Stream stream = new Stream();
            stream.setId(v3RoomDetail.getRoomSystemInfo().getId());
            room.setStream(stream);
            roomService.joinRoom(room);
            long trader = symClient.getUsersClient().getUserFromEmail(traderEmail).getId();
            long sender = symClient.getUsersClient().getUserFromEmail(rfq.getSenderEmail()).getId();
            symClient.getRoomMembershipClient().addMemberToRoom(room.getStreamId(),trader);
            symClient.getRoomMembershipClient().addMemberToRoom(room.getStreamId(),sender);

            rfq.setPricingStreamId(room.getStreamId());
            rfq.setStatus("pricing");
            rfq.setTraderEmail(traderEmail);
            mongoDBClient.updateRFQ(rfq);

            SymMessage message2 = new SymMessage();
            message2.setEntityData("{\"summary\": { \"type\": \"com.symphsol.mifid\", \"version\":  \"1.0\", \"status\":  \""+ rfq.getStatus()+"\", \"id\":  \""+rfqId+"\", \"action\":  \""+ rfq.getAction()+"\" , \"symbol\":  \""+ rfq.getSymbol()+"\" " +
                    ", \"numShares\":  \""+ rfq.getNumShares()+"\", \"senderCompany\":  \""+ rfq.getSenderCompany()+"\", \"traderEmail\":  \""+ rfq.getTraderEmail()+"\"}}");
            message2.setMessage("<messageML><div class='entity' data-entity-id='summary'><card accent='tempo-text-color--green'>RFQ Pricing<br/><hr /><b>"+ rfq.getAction()+" "+ rfq.getNumShares()+" <cash tag='"+ rfq.getSymbol()+"'/> </b></card></div></messageML>");

            symClient.getMessagesClient().sendMessage(room.getStream(), message2);

            SymMessage traderRoomMessage = new SymMessage();

            traderRoomMessage.setMessage("<messageML><div><card accent='tempo-text-color--green'>RFQ from "+ rfq.getSenderCompany()+" pricing in progress by "+symClient.getUsersClient().getUserFromEmail(rfq.getTraderEmail()).getDisplayName()+"<br/><hr /><b>"+ rfq.getAction()+" "+ rfq.getNumShares()+" <cash tag='"+ rfq.getSymbol()+"'/> </b></card></div></messageML>");
            String targetStreamId = mongoDBClient.getRoomForTargetCompany(config.getCompanyName());
            Stream RFQRoomStream = new Stream();
            RFQRoomStream.setId(targetStreamId);
            symClient.getMessagesClient().sendMessage(RFQRoomStream, traderRoomMessage);


        }  catch (RoomException e) {
            e.printStackTrace();
        } catch (UsersClientException e) {
            e.printStackTrace();
        } catch (SymException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public RFQ sendPrice(String rfqId, String price){
        RFQ rfq = mongoDBClient.getRFQ(rfqId);
        rfq.setPrice(price);
        rfq.setStatus("priced");
        mongoDBClient.updateRFQ(rfq);

        Stream stream = new Stream();
        stream.setId(rfq.getPricingStreamId());

        SymMessage message2 = new SymMessage();
        message2.setEntityData("{\"summary\": { \"type\": \"com.symphsol.mifid\", \"version\":  \"1.0\", \"status\":  \""+ rfq.getStatus()+"\", \"id\":  \""+rfqId+"\", \"action\":  \""+ rfq.getAction()+"\" , \"symbol\":  \""+ rfq.getSymbol()+"\" " +
                ", \"numShares\":  \""+ rfq.getNumShares()+"\", \"senderCompany\":  \""+ rfq.getSenderCompany()+"\", \"sender\":  \""+ rfq.getSenderEmail()+"\", \"traderEmail\":  \""+ rfq.getTraderEmail()+"\", \"price\":  \""+ rfq.getPrice()+"\"}}");
        message2.setMessage("<messageML><div class='entity' data-entity-id='summary'><card accent='tempo-text-color--green'>RFQ Priced<br/><hr/><b>"+ rfq.getAction()+" "+ rfq.getNumShares()+" <cash tag='"+ rfq.getSymbol()+"'/> @"+ rfq.getPrice()+"</b></card></div></messageML>");

        try {
            symClient.getMessagesClient().sendMessage(stream, message2);
        } catch (MessagesException e) {
            e.printStackTrace();
        }
        return rfq;
    }

    public RFQ confirmIOI(String rfqId){
        RFQ rfq = mongoDBClient.getRFQ(rfqId);
        rfq.setStatus("confirmed");
        mongoDBClient.updateRFQ(rfq);
        Stream pricingStream = new Stream();
        pricingStream.setId(rfq.getPricingStreamId());

        SymMessage message2 = new SymMessage();
        message2.setEntityData("{\"summary\": { \"type\": \"com.symphsol.mifid\", \"version\":  \"1.0\", \"status\":  \""+ rfq.getStatus()+"\", \"id\":  \""+rfqId+"\", \"action\":  \""+ rfq.getAction()+"\" , \"symbol\":  \""+ rfq.getSymbol()+"\" " +
                ", \"numShares\":  \""+ rfq.getNumShares()+"\", \"senderCompany\":  \""+ rfq.getSenderCompany()+"\", \"sender\":  \""+ rfq.getSenderEmail()+"\", \"traderEmail\":  \""+ rfq.getTraderEmail()+"\"}}");
        message2.setMessage("<messageML><card accent='tempo-text-color--green'>RFQ Confirmed<br/><hr/><b>"+ rfq.getAction()+" "+ rfq.getNumShares()+" <cash tag='"+ rfq.getSymbol()+"'/> @"+ rfq.getPrice()+"</b></card></messageML>");

        Stream originStream = new Stream();
        originStream.setId(rfq.getOriginStreamId());

        String traderStreamId = mongoDBClient.getRoomForTargetCompany(config.getCompanyName());
        Stream traderStream = new Stream();
        traderStream.setId(traderStreamId);

        try {
            symClient.getMessagesClient().sendMessage(pricingStream, message2);
            symClient.getMessagesClient().sendMessage(originStream, message2);
            symClient.getMessagesClient().sendMessage(traderStream, message2);

            symClient.getStreamsClient().deactivateRoom(rfq.getPricingStreamId());
        } catch (MessagesException e) {
            e.printStackTrace();
        } catch (StreamsException e) {
            e.printStackTrace();
        }
        return rfq;
    }

}