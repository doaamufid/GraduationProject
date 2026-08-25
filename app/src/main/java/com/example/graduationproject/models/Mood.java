package com.example.graduationproject.models;

/**
 * Plain data holder for one mood option.
 * Mirrors the ADULT_MOODS array from the original React component:
 * each mood carries its own soft full-screen background colour,
 * a slightly deeper "circle" tone for the hero avatar, and a muted
 * accent colour used for the progress bar / selection highlight.
 */
public class Mood {
    public final String id;
    public final String label;
    public final String faceType;
    public final int bgColor;
    public final int circleColor;
    public final int accentColor;

    public Mood(String id, String label, String faceType, int bgColor, int circleColor, int accentColor) {
        this.id = id;
        this.label = label;
        this.faceType = faceType;
        this.bgColor = bgColor;
        this.circleColor = circleColor;
        this.accentColor = accentColor;
    }
}
