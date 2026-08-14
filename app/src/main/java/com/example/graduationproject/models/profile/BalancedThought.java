package com.example.graduationproject.models.profile;

public class BalancedThought {
    public final long id;
    public final String pattern;
    public final String savedDate;
    public final String original;
    public final String reframed;
    public final String exercise;

    public BalancedThought(long id, String pattern, String savedDate, String original, String reframed, String exercise) {
        this.id = id;
        this.pattern = pattern;
        this.savedDate = savedDate;
        this.original = original;
        this.reframed = reframed;
        this.exercise = exercise;
    }
}
