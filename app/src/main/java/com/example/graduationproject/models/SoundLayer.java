package com.example.graduationproject.models;

/** Equivalent of one entry in an environment's `layers` array. */
public class SoundLayer {
    public final String key;
    public final String label;
    public final int iconRes;
    public final int soundRes;
    public final int defaultLevel;

    public SoundLayer(String key, String label, int iconRes, int soundRes, int defaultLevel) {
        this.key = key;
        this.label = label;
        this.iconRes = iconRes;
        this.soundRes = soundRes;
        this.defaultLevel = defaultLevel;
    }
}
