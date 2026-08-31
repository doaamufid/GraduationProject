package com.example.graduationproject.models;

/**
 * A single quote entry paired with a calming nature background image.
 * Mirrors the ENTRIES array from the original React component.
 */
public class QouteFeatureQuoteEntry {
    public final String id;
    public final String imgUrl;
    public final String ar;
    public final String en;

    public QouteFeatureQuoteEntry(String id, String imgUrl, String ar, String en) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.ar = ar;
        this.en = en;
    }
}
