package com.symphony.bot.POJO;

import org.bson.types.ObjectId;

public class RFQRoom {

    ObjectId id;
    String targetCompany;
    String streamId;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTargetCompany() {
        return targetCompany;
    }

    public void setTargetCompany(String targetCompany) {
        this.targetCompany = targetCompany;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public RFQRoom(String targetCompany, String streamId) {
        this.targetCompany = targetCompany;
        this.streamId = streamId;
    }

    public RFQRoom() {
    }
}
