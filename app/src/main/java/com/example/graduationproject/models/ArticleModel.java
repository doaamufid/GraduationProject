package com.example.graduationproject.models;

import java.util.List;

public class ArticleModel {
    private String tag;
    private String title;
    private List<String> badges; // قائمة تحمل النصوص للأزرار (سواء زر واحد أو اثنين)

    public ArticleModel(String tag, String title, List<String> badges) {
        this.tag = tag;
        this.title = title;
        this.badges = badges;
    }

    public String getTag() { return tag; }
    public String getTitle() { return title; }
    public List<String> getBadges() { return badges; }
}