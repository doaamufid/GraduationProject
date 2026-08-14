package com.example.graduationproject.models.profile;

import java.util.List;

public class Trait {
    public final String id;
    public String label;
    public final int color;
    public int count;
    public String quote;
    public final boolean selfAdded;
    public final List<TraitEvidence> evidence;
    public final String exercise;

    public Trait(String id, String label, int color, int count, String quote, boolean selfAdded,
                 List<TraitEvidence> evidence, String exercise) {
        this.id = id;
        this.label = label;
        this.color = color;
        this.count = count;
        this.quote = quote;
        this.selfAdded = selfAdded;
        this.evidence = evidence;
        this.exercise = exercise;
    }
}
