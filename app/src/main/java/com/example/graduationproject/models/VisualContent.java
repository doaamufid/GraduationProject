package com.example.graduationproject.models;

public class VisualContent {
    private int id;
    private String title;
    private String duration;
    private int thumbnailResId;
    private String categoryType; // لمعرفة الفئة التابع لها (مثل: RESPIRATION, SLEEP, RECOMMENDED)
    private String videoUrl; // في حال رغبتِ بتشغيل فيديو حقيقي لاحقاً

    public VisualContent(int id, String title, String duration, int thumbnailResId, String categoryType, String videoUrl) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.thumbnailResId = thumbnailResId;
        this.categoryType = categoryType;
        this.videoUrl = videoUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDuration() { return duration; }
    public int getThumbnailResId() { return thumbnailResId; }
    public String getCategoryType() { return categoryType; }
    public String getVideoUrl() { return videoUrl; }
}