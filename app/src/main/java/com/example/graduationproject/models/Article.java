package com.example.graduationproject.models;

/**
 * Mirrors the plain-object articles from ARTICLES[] in the original React source.
 */
public class Article {

    public final int id;
    public final String title;
    public final String category;      // one of Category.KEY_*
    public final String time;
    public final String price;
    public final double rating;
    public final String author;
    public final boolean hasExercise;
    public final String reason;
    public final String relatedExerciseLabel; // nullable
    public final String[] body;

    public Article(int id, String title, String category, String time, String price,
                    double rating, String author, boolean hasExercise, String reason,
                    String relatedExerciseLabel, String[] body) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.time = time;
        this.price = price;
        this.rating = rating;
        this.author = author;
        this.hasExercise = hasExercise;
        this.reason = reason;
        this.relatedExerciseLabel = relatedExerciseLabel;
        this.body = body;
    }
}
