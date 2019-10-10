package com.hcl.kandy.cpass.models;

import com.rbbn.cpaas.mobile.messaging.api.Part;

import java.util.List;

public class MultimediaChatModelChatModel {
    private String messageTxt;
    private String destination;//detstination email address
    private boolean isMessageIn;// true if message coming from other person
    private String messageId;
    private List<Part> parts;

    public MultimediaChatModelChatModel(String messageTxt, String destination,
                                        boolean isMessageIn, String messageId, List<Part> parts) {
        this.messageTxt = messageTxt;
        this.destination = destination;
        this.isMessageIn = isMessageIn;
        this.messageId = messageId;
        this.parts = parts;
    }


    public MultimediaChatModelChatModel(String messageTxt, String destination, boolean isMessageIn, String messageId) {
        this.messageTxt = messageTxt;
        this.destination = destination;
        this.isMessageIn = isMessageIn;
        this.messageId = messageId;
    }

    public List<Part> getParts() {
        return parts;
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
