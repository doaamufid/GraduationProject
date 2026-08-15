package com.example.graduationproject.models;

/** Equivalent of OLD_CONVERSATION.secondaryUpdate. */
public class SecondaryUpdate {
    public final String traitId;
    public final String label;
    public final int delta;
    public final String note;

    public SecondaryUpdate(String traitId, String label, int delta, String note) {
        this.traitId = traitId;
        this.label = label;
        this.delta = delta;
        this.note = note;
    }
}
