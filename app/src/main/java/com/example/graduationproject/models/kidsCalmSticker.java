package com.example.graduationproject.models;

/** Mirrors the STICKERS array from the React component. */
public class kidsCalmSticker {
    public final String id;
    public final String emoji;
    public final String label;
    public final int colorStart;
    public final int colorEnd;

    public kidsCalmSticker(String id, String emoji, String label, int colorStart, int colorEnd) {
        this.id = id;
        this.emoji = emoji;
        this.label = label;
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
    }
}
