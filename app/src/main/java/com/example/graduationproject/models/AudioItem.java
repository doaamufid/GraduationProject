package com.example.graduationproject.models;

public class AudioItem {
    public final long id;
    public final String label;
    public final int durationSeconds;

    public AudioItem(long id, String label, int durationSeconds) {
        this.id = id;
        this.label = label;
        this.durationSeconds = durationSeconds;
    }
}
