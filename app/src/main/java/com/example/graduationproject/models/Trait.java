package com.example.graduationproject.models;

import java.util.ArrayList;
import java.util.List;

/** Equivalent of one entry in the traits array (INITIAL_TRAITS + additions). */
public class Trait {
    public final String id;
    public String label;
    public int colorInt;
    public int count;
    public String quote;
    public boolean selfAdded;
    public boolean isNew;
    public final List<TraitEvidence> evidence = new ArrayList<>();
    public String exercise;

    public Trait(String id, String label, int colorInt, int count, String quote,
                 boolean selfAdded, boolean isNew, String exercise) {
        this.id = id;
        this.label = label;
        this.colorInt = colorInt;
        this.count = count;
        this.quote = quote;
        this.selfAdded = selfAdded;
        this.isNew = isNew;
        this.exercise = exercise;
    }
}
