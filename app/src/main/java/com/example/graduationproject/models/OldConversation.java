package com.example.graduationproject.models;

import java.util.List;

/** Equivalent of the OLD_CONVERSATION constant. */
public class OldConversation {
    public final String dateLabel;
    public final String timeLabel;
    public final String relativeLabel;
    public final PrimaryTraitReveal primaryTrait;
    public final SecondaryUpdate secondaryUpdate;
    public final List<RelatedTrait> relatedTraits;

    public OldConversation(String dateLabel, String timeLabel, String relativeLabel,
                            PrimaryTraitReveal primaryTrait, SecondaryUpdate secondaryUpdate,
                            List<RelatedTrait> relatedTraits) {
        this.dateLabel = dateLabel;
        this.timeLabel = timeLabel;
        this.relativeLabel = relativeLabel;
        this.primaryTrait = primaryTrait;
        this.secondaryUpdate = secondaryUpdate;
        this.relatedTraits = relatedTraits;
    }
}
