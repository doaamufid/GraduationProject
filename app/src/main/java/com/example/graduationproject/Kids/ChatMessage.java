package com.example.graduationproject.Kids;

public class ChatMessage {
    private String message;
    private boolean isUser; // true إذا كانت الرسالة من الطفل، false إذا كانت من AI

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}