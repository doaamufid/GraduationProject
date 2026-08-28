package com.example.graduationproject.models;

/**
 * يقابل عنصر واحد داخل مصفوفة "items" في الكود الأصلي.
 */
public class AdultNotifNotificationItem {

    public final int id;
    public final AdultNotifNotificationType type;
    public final String title;
    public final String desc;
    public final String time;
    public boolean unread;

    public AdultNotifNotificationItem(int id, AdultNotifNotificationType type, String title, String desc,
                             String time, boolean unread) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.time = time;
        this.unread = unread;
    }
}
