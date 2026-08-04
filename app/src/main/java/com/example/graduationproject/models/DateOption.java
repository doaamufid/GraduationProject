package com.example.graduationproject.models;

/** Equivalent of one entry in the DATE_OPTIONS constant. */
public class DateOption {
    public final String key;
    public final String label;
    public final int days;

    public DateOption(String key, String label, int days) {
        this.key = key;
        this.label = label;
        this.days = days;
    }
}
