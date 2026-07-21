package com.example.graduationproject.models;

public class Reminder {
    public boolean enabled;
    public String time; // display string, e.g. "9:00 AM"

    public Reminder(boolean enabled, String time) {
        this.enabled = enabled;
        this.time = time;
    }
}
