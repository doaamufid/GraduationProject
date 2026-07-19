package com.example.graduationproject.models;

/** One step of the 5-4-3-2-1 grounding exercise. */
public class Sense {
    public final String key;
    public final int count;
    public final String tag;
    public final String title;
    public final String question;
    public final String emoji;

    public Sense(String key, int count, String tag, String title, String question, String emoji) {
        this.key = key;
        this.count = count;
        this.tag = tag;
        this.title = title;
        this.question = question;
        this.emoji = emoji;
    }
}
