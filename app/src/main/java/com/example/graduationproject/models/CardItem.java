package com.example.graduationproject.models;

/**
 * Mirrors a JS card object: { id, phrase, photo, active }
 */
public class CardItem {
    public long id;
    public String phrase;
    public CardPhoto photo; // nullable -> text-only card
    public boolean active;

    public CardItem(long id, String phrase, CardPhoto photo, boolean active) {
        this.id = id;
        this.phrase = phrase;
        this.photo = photo;
        this.active = active;
    }
}
