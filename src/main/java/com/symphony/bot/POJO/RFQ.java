package com.symphony.bot.POJO;

import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class RFQ {

    private ObjectId id;
    private String senderCompany;
    private String senderEmail;
    private String targetCompany;
    private String originStreamId;
    private String pricingStreamId;
    private String action;
    private int numShares;
    private String symbol;
    private String price;
    private String status;
    private String traderEmail;
    private Date dateCreated;

    public RFQ() {
        this.dateCreated = new Date();
    }

    public RFQ(String senderCompany, String senderEmail, String targetCompany, String originStreamId, String action, int numShares, String symbol) {
        this.senderCompany = senderCompany;
        this.senderEmail = senderEmail;
        this.targetCompany = targetCompany;
        this.originStreamId = originStreamId;
        this.action = action;
        this.numShares = numShares;
        this.symbol = symbol;
        this.status = "pending";
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSenderCompany() {
        return senderCompany;
    }

    public void setSenderCompany(String senderCompany) {
        this.senderCompany = senderCompany;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getTargetCompany() {
        return targetCompany;
    }

    public void setTargetCompany(String targetCompany) {
        this.targetCompany = targetCompany;
    }

    public String getOriginStreamId() {
        return originStreamId;
    }

    public void setOriginStreamId(String originStreamId) {
        this.originStreamId = originStreamId;
    }

    public String getPricingStreamId() {
        return pricingStreamId;
    }

    public void setPricingStreamId(String pricingStreamId) {
        this.pricingStreamId = pricingStreamId;
    }

    public int getNumShares() {
        return numShares;
    }

    public void setNumShares(int numShares) {
        this.numShares = numShares;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTraderEmail() {
        return traderEmail;
    }

    public void setTraderEmail(String traderEmail) {
        this.traderEmail = traderEmail;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
