package com.example.graduationproject.models;

public class ExerciseFeature {
    private final int iconResId;
    private final String title;
    private final String subtitle;

    public ExerciseFeature(int iconResId, String title, String subtitle) {
        this.iconResId = iconResId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getIconResId() { return iconResId; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
}