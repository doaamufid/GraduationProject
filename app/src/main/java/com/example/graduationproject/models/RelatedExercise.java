package com.example.graduationproject.models;

/** Equivalent of `article.relatedExercise = { label, icon }`. */
public class RelatedExercise {
    public final String label;
    public final int iconRes;

    public RelatedExercise(String label, int iconRes) {
        this.label = label;
        this.iconRes = iconRes;
    }
}
