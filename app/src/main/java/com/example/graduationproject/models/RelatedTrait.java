package com.example.graduationproject.models;

/** Equivalent of one entry in OLD_CONVERSATION.relatedTraits. */
public class RelatedTrait {
    public final String id;
    public final String label;
    public final int colorInt;
    public final String reason;
    public final String suggestion;

    public RelatedTrait(String id, String label, int colorInt, String reason, String suggestion) {
        this.id = id;
        this.label = label;
        this.colorInt = colorInt;
        this.reason = reason;
        this.suggestion = suggestion;
    }
}
