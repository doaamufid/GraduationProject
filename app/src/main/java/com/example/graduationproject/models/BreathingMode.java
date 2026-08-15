package com.example.graduationproject.models;

public class BreathingMode {
    public final String name;
    public final String description;
    public final int[] pattern; // [inhale, hold, exhale, hold]
    public final int durationMinutes;
    public final int illustrationResId;
    public final int backgroundColor;

    public BreathingMode(String name, String description, int[] pattern, int durationMinutes, int illustrationResId, int backgroundColor) {
        this.name = name;
        this.description = description;
        this.pattern = pattern;
        this.durationMinutes = durationMinutes;
        this.illustrationResId = illustrationResId;
        this.backgroundColor = backgroundColor;
    }
}
