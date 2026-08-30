package com.example.graduationproject.models;

public class FavoriteStory {
    private long id;
    private String category;
    private String title;
    private String storyText;
    private long createdAt;

    public FavoriteStory(long id, String category, String title, String storyText, long createdAt) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.storyText = storyText;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getStoryText() { return storyText; }
    public long getCreatedAt() { return createdAt; }
}