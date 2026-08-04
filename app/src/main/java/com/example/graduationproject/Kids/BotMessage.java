package com.example.graduationproject.Kids;

public class BotMessage {
    private final long id;
    private final String text;
    private final String mood;
    private final long createdAt;

    public BotMessage(long id, String text, String mood, long createdAt) {
        this.id = id;
        this.text = text;
        this.mood = mood;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getMood() {
        return mood;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}