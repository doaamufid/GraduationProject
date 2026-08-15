package com.example.graduationproject.models;

/** Equivalent of OLD_CONVERSATION.primaryTrait. */
public class PrimaryTraitReveal {
    public final String id;
    public final String label;
    public final int colorInt;
    public final boolean isNewTrait;
    public final String paraphrase;
    public final String exercise;

    public PrimaryTraitReveal(String id, String label, int colorInt, boolean isNewTrait,
                               String paraphrase, String exercise) {
        this.id = id;
        this.label = label;
        this.colorInt = colorInt;
        this.isNewTrait = isNewTrait;
        this.paraphrase = paraphrase;
        this.exercise = exercise;
    }
}
