package com.example.graduationproject.models;

public class ExerciseFeature {
    private final int iconResId;
    private final int iconBackgroundResId;
    private final String title;
    private final String subtitle;

    public ExerciseFeature(int iconResId, int iconBackgroundResId, String title, String subtitle) {
        this.iconResId = iconResId;
        this.iconBackgroundResId = iconBackgroundResId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getIconResId() { return iconResId; }
    public int getIconBackgroundResId() { return iconBackgroundResId; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
}