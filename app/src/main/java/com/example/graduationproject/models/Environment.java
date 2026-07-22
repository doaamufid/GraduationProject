package com.example.graduationproject.models;


public class Environment {
    private int id;
    private String title;
    private String subtitle;
    private int imageResId; // معرف الصورة في مجلد drawable
    private int soundResId; // معرف ملف الصوت في مجلد raw (إذا كان لديكِ أصوات)

    public Environment(int id, String title, String subtitle, int imageResId, int soundResId) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
        this.soundResId = soundResId;
    }

    // Getters و Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getImageResId() { return imageResId; }
    public int getSoundResId() { return soundResId; }
}
