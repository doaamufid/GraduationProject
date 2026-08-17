package com.example.graduationproject.models;

/**
 * Mirrors the JS message object shape:
 * { id, text, cat, hearts, img, emoji, isMine }
 */
public class Message {
    public long id;
    public String text;
    public String cat;
    public int hearts;
    public String img;     // nullable image url
    public String emoji;   // nullable emoji
    public boolean isMine;
    public int colorIndex = -1; // -1 means auto-cycle based on index

    public Message(long id, String text, String cat, int hearts, String img, String emoji, boolean isMine) {
        this.id = id;
        this.text = text;
        this.cat = cat;
        this.hearts = hearts;
        this.img = img;
        this.emoji = emoji;
        this.isMine = isMine;
    }

    public Message(long id, String text, String cat, int hearts, String img, String emoji, boolean isMine, int colorIndex) {
        this(id, text, cat, hearts, img, emoji, isMine);
        this.colorIndex = colorIndex;
    }
}
