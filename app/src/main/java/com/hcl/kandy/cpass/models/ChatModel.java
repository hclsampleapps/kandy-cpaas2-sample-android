package com.hcl.kandy.cpass.models;

public class ChatModel {
    private String messageTxt;
    private String destination;//detstination email address
    private boolean isMessageIn;// true if message coming from other person
    private String messageId;

    public ChatModel(String messageTxt, String destination, boolean isMessageIn, String messageId) {
        this.messageTxt = messageTxt;
        this.destination = destination;
        this.isMessageIn = isMessageIn;
        this.messageId = messageId;
    }

    @SuppressWarnings("unused")
    public String getMessageId() {
        return messageId;
    }

    public String getMessageTxt() {
        return messageTxt;
    }

    public String getDestination() {
        return destination;
    }

    public boolean isMessageIn() {
        return isMessageIn;
    }

}
