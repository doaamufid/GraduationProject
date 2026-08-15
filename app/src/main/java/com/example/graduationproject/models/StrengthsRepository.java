package com.example.graduationproject.models;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.graduationproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * In-memory singleton holding the `traits` state and the constant
 * OLD_CONVERSATION payload, equivalent to `useState(INITIAL_TRAITS)`
 * and the OLD_CONVERSATION constant in the original root component.
 */
public final class StrengthsRepository {

    private static StrengthsRepository instance;

    public final List<Trait> traits = new ArrayList<>();
    public final OldConversation oldConversation;

    private StrengthsRepository(Context appContext) {
        int cPatience = ContextCompat.getColor(appContext, R.color.trait_patience);
        int cCourage = ContextCompat.getColor(appContext, R.color.trait_courage);
        int cEmpathy = ContextCompat.getColor(appContext, R.color.trait_empathy);
        int cPersistence = ContextCompat.getColor(appContext, R.color.trait_persistence);
        int cDiscipline = ContextCompat.getColor(appContext, R.color.trait_discipline);
        int cConfidence = ContextCompat.getColor(appContext, R.color.trait_confidence);
        int cResilience = ContextCompat.getColor(appContext, R.color.trait_resilience);

        Trait patience = new Trait("patience", appContext.getString(R.string.trait_patience), cPatience, 8, appContext.getString(R.string.patience_quote),
                false, false, appContext.getString(R.string.patience_exercise));
        patience.evidence.add(new TraitEvidence(appContext.getString(R.string.last_week), appContext.getString(R.string.patience_evidence_1)));
        patience.evidence.add(new TraitEvidence(appContext.getString(R.string.one_week_ago), appContext.getString(R.string.patience_evidence_2)));
        patience.evidence.add(new TraitEvidence(appContext.getString(R.string.two_weeks_ago), appContext.getString(R.string.patience_evidence_3)));
        traits.add(patience);

        Trait courage = new Trait("courage", appContext.getString(R.string.trait_courage), cCourage, 6, appContext.getString(R.string.courage_quote),
                false, false, appContext.getString(R.string.courage_exercise));
        courage.evidence.add(new TraitEvidence(appContext.getString(R.string.yesterday), appContext.getString(R.string.courage_evidence_1)));
        courage.evidence.add(new TraitEvidence(appContext.getString(R.string.four_days_ago), appContext.getString(R.string.courage_evidence_2)));
        traits.add(courage);

        Trait empathy = new Trait("empathy", appContext.getString(R.string.trait_empathy), cEmpathy, 5, appContext.getString(R.string.empathy_quote),
                false, false, appContext.getString(R.string.empathy_exercise));
        empathy.evidence.add(new TraitEvidence(appContext.getString(R.string.today), appContext.getString(R.string.empathy_evidence_1)));
        empathy.evidence.add(new TraitEvidence(appContext.getString(R.string.three_days_ago), appContext.getString(R.string.empathy_evidence_2)));
        traits.add(empathy);

        oldConversation = new OldConversation(
                appContext.getString(R.string.sunday_date), appContext.getString(R.string.pm_time), appContext.getString(R.string.three_days_ago),
                new PrimaryTraitReveal("persistence", appContext.getString(R.string.trait_persistence), cPersistence, true,
                        appContext.getString(R.string.persistence_paraphrase),
                        appContext.getString(R.string.persistence_exercise)),
                new SecondaryUpdate("empathy", appContext.getString(R.string.trait_empathy), 1, appContext.getString(R.string.empathy_note)),
                Arrays.asList(
                        new RelatedTrait("discipline", appContext.getString(R.string.trait_discipline), cDiscipline,
                                appContext.getString(R.string.discipline_reason),
                                appContext.getString(R.string.discipline_suggestion)),
                        new RelatedTrait("confidence", appContext.getString(R.string.trait_confidence), cConfidence,
                                appContext.getString(R.string.confidence_reason),
                                appContext.getString(R.string.confidence_suggestion)),
                        new RelatedTrait("resilience", appContext.getString(R.string.trait_resilience), cResilience,
                                appContext.getString(R.string.resilience_reason),
                                appContext.getString(R.string.resilience_suggestion))
                )
        );
    }

    public static synchronized StrengthsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StrengthsRepository(context.getApplicationContext());
        }
        return instance;
    }

    public Trait findById(String id) {
        for (Trait t : traits) if (t.id.equals(id)) return t;
        return null;
    }

    /**
     * Equivalent of the `handleAnalyze` timeout branch that runs the FIRST
     * time: bumps the secondary trait's count/evidence and appends the new
     * primary trait discovered from OLD_CONVERSATION.
     */
    public void applyAnalysisResult() {
        Trait secondary = findById(oldConversation.secondaryUpdate.traitId);
        if (secondary != null) {
            secondary.count += oldConversation.secondaryUpdate.delta;
            secondary.evidence.add(0, new TraitEvidence(
                    oldConversation.relativeLabel, oldConversation.secondaryUpdate.note));
        }

        PrimaryTraitReveal p = oldConversation.primaryTrait;
        Trait newTrait = new Trait(p.id, p.label, p.colorInt, 1, p.paraphrase, false, true, p.exercise);
        newTrait.evidence.add(new TraitEvidence(oldConversation.relativeLabel, p.paraphrase));
        traits.add(newTrait);
    }

    /** Equivalent of `handleAddTrait`. */
    public Trait addSelfTrait(Context context, String name, String note) {
        String defaultNote = context.getString(R.string.default_self_note);
        String defaultCtx = context.getString(R.string.default_self_ctx);
        String whenLabel = context.getString(R.string.default_self_when);
        String exercise = context.getString(R.string.default_self_exercise);

        Trait trait = new Trait("self-" + System.currentTimeMillis(), name,
                ContextCompat.getColor(context, R.color.trait_self_added), 1,
                note != null && !note.isEmpty() ? note : defaultNote,
                true, false, exercise);
        trait.evidence.add(new TraitEvidence(whenLabel, note != null && !note.isEmpty() ? note : defaultCtx));
        traits.add(trait);
        return trait;
    }
}
