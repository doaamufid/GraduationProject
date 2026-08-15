package com.example.graduationproject.models;

/** Equivalent of one entry in a trait's `evidence` array: { when, ctx }. */
public class TraitEvidence {
    public final String when;
    public final String ctx;

    public TraitEvidence(String when, String ctx) {
        this.when = when;
        this.ctx = ctx;
    }
}
