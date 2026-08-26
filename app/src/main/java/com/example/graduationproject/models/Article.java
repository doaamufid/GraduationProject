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
    public final boolean featured;
    public final String reason;
    public final String relatedExerciseLabel; // nullable
    public final RelatedExercise relatedExercise; // nullable
    public final String[] body;

    // Legacy constructor used by com.example.graduationproject.data.ArticleRepository
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
        this.featured = false;
        this.reason = reason;
        this.relatedExerciseLabel = relatedExerciseLabel;
        this.relatedExercise = null;
        this.body = body;
    }

    // New constructor used by com.example.graduationproject.models.ArticleRepository
    public Article(int id, String title, String category, String time, String price,
                    String author, boolean featured, String reason,
                    RelatedExercise relatedExercise, String[] body) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.time = time;
        this.price = price;
        this.rating = 4.8;
        this.author = author;
        this.hasExercise = (relatedExercise != null);
        this.featured = featured;
        this.reason = reason;
        this.relatedExerciseLabel = (relatedExercise != null ? relatedExercise.label : null);
        this.relatedExercise = relatedExercise;
        this.body = body;
    }
}
