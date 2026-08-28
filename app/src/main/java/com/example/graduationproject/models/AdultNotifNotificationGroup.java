package com.example.graduationproject.models;

import java.util.List;

/**
 * يقابل كائن مجموعة واحدة (label + items) في INITIAL_GROUPS الأصلية.
 */
public class AdultNotifNotificationGroup {

    public final String label;
    public final List<AdultNotifNotificationItem> items;

    public AdultNotifNotificationGroup(String label, List<AdultNotifNotificationItem> items) {
        this.label = label;
        this.items = items;
    }
}
