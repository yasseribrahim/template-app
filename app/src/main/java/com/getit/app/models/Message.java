package com.getit.app.models;
import com.google.gson.annotations.Expose;

public class Message {
    @Expose
    private String senderId;
    @Expose
    private String senderName;
    @Expose
    private String receiveName;
    @Expose
    private String message;
    @Expose
    private long timestamp;

    public Message() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", receiveName=" + receiveName +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}