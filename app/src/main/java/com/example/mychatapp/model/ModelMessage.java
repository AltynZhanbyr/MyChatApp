package com.example.mychatapp.model;

public class ModelMessage {
    private String text;
    private String name;
    private String imageURL;
    private String sender;
    private String recipient;
    private boolean isMine;

    public ModelMessage(){}
    public ModelMessage(String text,String name, String imageURL){
        this.imageURL=imageURL;
        this.name=name;
        this.text=text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}
