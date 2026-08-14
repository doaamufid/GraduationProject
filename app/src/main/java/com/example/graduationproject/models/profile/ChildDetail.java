package com.example.graduationproject.models.profile;

import java.util.List;

public class ChildDetail {
    public final long id;
    public final String name;
    public final int age;
    public final String avatarEmoji;
    public final int color;
    public final String lastActive;
    public final ChildStats stats;
    public final int[] mood;
    public final String[] days;
    public final List<ChildFeature> topFeatures;
    public final ChildAlert alert; // nullable
    public final List<String> recommendations;
    public final List<ChildHistoryEntry> history;

    public ChildDetail(long id, String name, int age, String avatarEmoji, int color, String lastActive,
                        ChildStats stats, int[] mood, String[] days, List<ChildFeature> topFeatures,
                        ChildAlert alert, List<String> recommendations, List<ChildHistoryEntry> history) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.avatarEmoji = avatarEmoji;
        this.color = color;
        this.lastActive = lastActive;
        this.stats = stats;
        this.mood = mood;
        this.days = days;
        this.topFeatures = topFeatures;
        this.alert = alert;
        this.recommendations = recommendations;
        this.history = history;
    }
}
