package com.example.graduationproject.models;

public class LoveItem {
    public static final String SOURCE_SELF = "self";
    public static final String SOURCE_OTHER = "other";

    public final long id;
    public final String text;
    public final String source; // SOURCE_SELF | SOURCE_OTHER

    public LoveItem(long id, String text, String source) {
        this.id = id;
        this.text = text;
        this.source = source;
    }
}
