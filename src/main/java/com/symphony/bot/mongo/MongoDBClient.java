package com.symphony.bot.mongo;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.symphony.bot.POJO.RFQ;
import com.symphony.bot.POJO.RFQRoom;
import com.symphony.bot.SymphonyConfiguration;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBClient {
    private MongoCollection<RFQRoom> rfqRoomCollection;
    private MongoCollection<RFQ> rfqCollection;

    public MongoDBClient(SymphonyConfiguration config) {
        MongoClientURI connectionString = new MongoClientURI(config.getMongoURL());
        MongoClient mongoClient = new MongoClient(connectionString);
        String dbs = mongoClient.listDatabaseNames().first();
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = mongoClient.getDatabase("IOIBot");

        database = database.withCodecRegistry(pojoCodecRegistry);
        rfqRoomCollection = database.getCollection("IOIRoom", RFQRoom.class);
        rfqCollection = database.getCollection("IOI", RFQ.class);
    }

    public String getRoomForTargetCompany(String company){
        RFQRoom room = rfqRoomCollection.find(eq("targetCompany",company)).first();
        return room.getStreamId();
    }

    public void setRoomForTargetCompany(String company, String streamId){
        RFQRoom RFQRoom = new RFQRoom(company, streamId);
        rfqRoomCollection.insertOne(RFQRoom);
    }

    public RFQ newRFQ (RFQ rfq){
        rfqCollection.insertOne(rfq);
        rfq = rfqCollection.find(eq("dateCreated", rfq.getDateCreated())).first();
        return rfq;
    }

    public RFQ getRFQ (String ioiId){
        return rfqCollection.find(eq("_id", new ObjectId(ioiId))).first();
    }

    public void updateRFQ (RFQ rfq){
        if(rfq.getStatus().equals("pending")){
            rfqCollection.updateOne(eq("_id", rfq.getId()),combine(set("action", rfq.getAction()),set("numShares", rfq.getNumShares()),set("symbol", rfq.getSymbol()),set("status", rfq.getStatus())));
        }
        else if(rfq.getStatus().equals("pricing")){
            rfqCollection.updateOne(eq("_id", rfq.getId()),combine(set("pricingStreamId", rfq.getPricingStreamId()),set("status", rfq.getStatus()),set("traderEmail", rfq.getTraderEmail())));
        }
        else if(rfq.getStatus().equals("priced")){
            rfqCollection.updateOne(eq("_id", rfq.getId()),combine(set("price", rfq.getPrice()),set("status", rfq.getStatus())));
        }
        else{
            rfqCollection.updateOne(eq("_id", rfq.getId()),set("status", rfq.getStatus()));
        }


    }

    public List<RFQ> getPending() {
        List<RFQ> rfqs= new ArrayList<>();
        rfqCollection.find(eq("status", "pending")).forEach(new Block<RFQ>() {
            @Override
            public void apply(final RFQ RFQ) {
                rfqs.add(RFQ);
            }
        });
        return rfqs;
    }
}
