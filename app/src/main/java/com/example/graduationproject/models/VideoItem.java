package com.example.graduationproject.models;

public class VideoItem {
    private long id;
    private String title;
    private String subtitle;
    private String category;
    private String thumbnailName;
    private String bgColorHex;
    private String videoFile;
    private String duration;

    public VideoItem(long id, String title, String subtitle, String category,
                     String thumbnailName, String bgColorHex, String videoFile, String duration) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.thumbnailName = thumbnailName;
        this.bgColorHex = bgColorHex;
        this.videoFile = videoFile;
        this.duration = duration;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getCategory() { return category; }
    public String getThumbnailName() { return thumbnailName; }
    public String getBgColorHex() { return bgColorHex; }
    public String getVideoFile() { return videoFile; }
    public String getDuration() { return duration; }
}