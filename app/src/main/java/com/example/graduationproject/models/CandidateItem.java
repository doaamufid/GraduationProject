package com.example.graduationproject.models;

public class CandidateItem {
    public final String type; // "article" or "video"
    public final int id;
    public final String title;
    public final String category;

    public CandidateItem(String type, int id, String title, String category) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.category = category;
    }
}
