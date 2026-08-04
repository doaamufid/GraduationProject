package com.example.graduationproject.models;

public class NotificationItem {
    private String title;
    private String body;
    private String time;
    private int iconResId;

    public NotificationItem(String title, String body, String time, int iconResId) {
        this.title = title;
        this.body = body;
        this.time = time;
        this.iconResId = iconResId;
    }

    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getTime() { return time; }
    public int getIconResId() { return iconResId; }
}