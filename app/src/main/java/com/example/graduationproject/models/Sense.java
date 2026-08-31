package com.example.graduationproject.models;

public class Sense {
    public final String key;
    public final int count;
    public final int tagRes;
    public final int titleRes;
    public final int questionRes;
    public final String emoji;

    public Sense(String key, int count, int tagRes, int titleRes, int questionRes, String emoji) {
        this.key = key;
        this.count = count;
        this.tagRes = tagRes;
        this.titleRes = titleRes;
        this.questionRes = questionRes;
        this.emoji = emoji;
    }
}
