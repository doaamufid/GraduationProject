package com.example.graduationproject.models;

public class SoundItem {
    private long id;
    private String title;
    private String iconName;
    private String audioFileName;

    public SoundItem(long id, String title, String iconName, String audioFileName) {
        this.id = id;
        this.title = title;
        this.iconName = iconName;
        this.audioFileName = audioFileName;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getIconName() { return iconName; }
    public String getAudioFileName() { return audioFileName; }
}