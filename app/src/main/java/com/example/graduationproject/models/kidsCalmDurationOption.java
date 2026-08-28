package com.example.graduationproject.models;

/** Mirrors DUR_OPTIONS: short/medium/long durations for a calming word. */
public class kidsCalmDurationOption {
    public final String key;
    public final String emoji;
    public final String labelResName; // resolved via string resources by the UI
    public final int minutes;

    public kidsCalmDurationOption(String key, String emoji, String labelResName, int minutes) {
        this.key = key;
        this.emoji = emoji;
        this.labelResName = labelResName;
        this.minutes = minutes;
    }
}
