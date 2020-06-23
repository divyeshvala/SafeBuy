package com.example.app.model;

public class MessageObject
{
    String messageId, userType, text, image, time;

    public MessageObject() {
    }

    public MessageObject(String messageId, String userType, String text, String image, String time)
    {
        this.messageId = messageId;
        this.userType = userType;
        this.image = image;
        this.text = text;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String senderId) {
        this.userType = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}