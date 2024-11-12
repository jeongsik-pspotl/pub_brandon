package com.inswave.whive.branch.domain;


public class BuildMessage {

    private String senderSessionId;
    private String message;
    private String messageType;
    private MessageType messageStatus;

    public MessageType getMessageType() {
        return messageStatus;
    }

    public void setMessageType(MessageType messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getSenderSessionId() {
        return senderSessionId;
    }

    public void setSenderSessionId(String senderSessionId) {
        this.senderSessionId = senderSessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "buildMessage{" + "senderSessionId='" + senderSessionId + '\'' + ", message='" + message + '\'' + ", messageType=" + messageType + '}';
    }
}