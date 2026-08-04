package com.example.graduationproject.models;

public class Suggestion {
    public final String id;
    public final String name;
    public final String icon;
    public final String reason;
    public boolean done;

    public Suggestion(String id, String name, String icon, String reason, boolean done) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.reason = reason;
        this.done = done;
    }
}
