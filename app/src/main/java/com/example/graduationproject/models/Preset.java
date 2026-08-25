package com.example.graduationproject.models;

/**
 * Mirrors the JS PRESETS array — a named gradient background option
 * a user can pick for a calm card instead of uploading a photo.
 */
public class Preset {
    public final String id;
    public final String emoji;
    public final String label;
    public final int gradientDrawableRes; // drawable resource id for the gradient

    public Preset(String id, String emoji, String label, int gradientDrawableRes) {
        this.id = id;
        this.emoji = emoji;
        this.label = label;
        this.gradientDrawableRes = gradientDrawableRes;
    }
}
