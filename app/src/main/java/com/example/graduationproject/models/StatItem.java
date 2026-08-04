package com.example.graduationproject.models;

/** Equivalent of one [value, label] pair in the quick-stats array. */
public class StatItem {
    public final String value;
    public final String label;

    public StatItem(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
