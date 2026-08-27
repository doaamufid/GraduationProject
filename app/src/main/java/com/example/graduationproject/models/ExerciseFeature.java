package com.example.graduationproject.models;

public class ExerciseFeature {
    private final int iconResId;
    private final String title;
    private final String description;
    private final String duration;
    private final int cardBgColor;
    private final int circleColor;

    public ExerciseFeature(int iconResId, String title, String description, String duration, int cardBgColor, int circleColor) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.cardBgColor = cardBgColor;
        this.circleColor = circleColor;
    }

    public int getIconResId() { return iconResId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDuration() { return duration; }
    public int getCardBgColor() { return cardBgColor; }
    public int getCircleColor() { return circleColor; }
}