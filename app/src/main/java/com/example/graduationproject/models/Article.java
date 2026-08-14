package com.example.graduationproject.models;

/** Equivalent of one entry in the ARTICLES constant. */
public class Article {
    public final int id;
    public final String title;
    public final String category;
    public final String time;
    public final String tagEn;
    public final String author;
    public final boolean featured;
    public final String reason;
    public final RelatedExercise relatedExercise;
    public final String[] body;

    public Article(int id, String title, String category, String time, String tagEn, String author,
                    boolean featured, String reason, RelatedExercise relatedExercise, String[] body) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.time = time;
        this.tagEn = tagEn;
        this.author = author;
        this.featured = featured;
        this.reason = reason;
        this.relatedExercise = relatedExercise;
        this.body = body;
    }
}
