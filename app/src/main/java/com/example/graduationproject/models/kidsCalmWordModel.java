package com.example.graduationproject.models;

/** Mirrors a single item of INITIAL_WORDS / the "words" state array. */
public class kidsCalmWordModel {
    public long id;
    public String text;
    public String emoji;
    public boolean favorite;
    public String durKey;

    public kidsCalmWordModel(long id, String text, String emoji, boolean favorite, String durKey) {
        this.id = id;
        this.text = text;
        this.emoji = emoji;
        this.favorite = favorite;
        this.durKey = durKey;
    }
}
