package com.example.graduationproject.models;

/**
 * Mirrors a JS dhikr object: { id, text, category, favorite, minutes }
 */
public class CalmDhikrItem {
    public long id;
    public String text;
    public String category; // key into CategoryMeta
    public boolean favorite;
    public int minutes;

    public CalmDhikrItem(long id, String text, String category, boolean favorite, int minutes) {
        this.id = id;
        this.text = text;
        this.category = category;
        this.favorite = favorite;
        this.minutes = minutes;
    }
}
