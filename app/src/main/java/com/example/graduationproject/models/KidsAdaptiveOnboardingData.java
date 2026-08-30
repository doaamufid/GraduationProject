package com.example.graduationproject.models;

import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Mirrors the React `initialData` object + reducer state exactly (same fields,
 * same semantics). TOGGLE_ARRAY actions map to the toggle() helper below.
 */
public class KidsAdaptiveOnboardingData {

    public String nickname = "";
    public boolean nicknameProvided = false;
    public Integer ageRangeIndex = null; // null = not answered
    public String gender = null; // "female" | "male" | "unspecified" | null
    public String overallMood = null; // "calm" | "pressured" | "hard" | "unsure" | null
    public LinkedHashSet<String> frequentEmotions = new LinkedHashSet<>();
    public String safetyFeeling = null;
    public String intenseFearExperience = null;
    public LinkedHashSet<String> difficultTimes = new LinkedHashSet<>();
    public LinkedHashSet<String> morningFeelings = new LinkedHashSet<>();
    public LinkedHashSet<String> dayFeelings = new LinkedHashSet<>();
    public LinkedHashSet<String> eveningFeelings = new LinkedHashSet<>();
    public LinkedHashSet<String> nightFeelings = new LinkedHashSet<>();
    public LinkedHashSet<String> helpfulActivities = new LinkedHashSet<>();
    public LinkedHashSet<String> goals = new LinkedHashSet<>();
    public String demoMoodSelected = null;
    public boolean moodCheckinEnabled = true;
    public boolean onboardingCompleted = false;

    public KidsAdaptiveOnboardingData() {}

    /** Deep copy, used the same way RESTORE replaces the whole reducer state. */
    public KidsAdaptiveOnboardingData copy() {
        KidsAdaptiveOnboardingData d = new KidsAdaptiveOnboardingData();
        d.nickname = nickname;
        d.nicknameProvided = nicknameProvided;
        d.ageRangeIndex = ageRangeIndex;
        d.gender = gender;
        d.overallMood = overallMood;
        d.frequentEmotions = new LinkedHashSet<>(frequentEmotions);
        d.safetyFeeling = safetyFeeling;
        d.intenseFearExperience = intenseFearExperience;
        d.difficultTimes = new LinkedHashSet<>(difficultTimes);
        d.morningFeelings = new LinkedHashSet<>(morningFeelings);
        d.dayFeelings = new LinkedHashSet<>(dayFeelings);
        d.eveningFeelings = new LinkedHashSet<>(eveningFeelings);
        d.nightFeelings = new LinkedHashSet<>(nightFeelings);
        d.helpfulActivities = new LinkedHashSet<>(helpfulActivities);
        d.goals = new LinkedHashSet<>(goals);
        d.demoMoodSelected = demoMoodSelected;
        d.moodCheckinEnabled = moodCheckinEnabled;
        d.onboardingCompleted = onboardingCompleted;
        return d;
    }

    /** 🌟 دالة تحويل الخيار المختار (sad, low, neutral, good, great) إلى مظهر الأفاتار المناسب */
    public String getAvatarMoodFromSelection() {
        if (demoMoodSelected == null || demoMoodSelected.trim().isEmpty()) {
            return KidsAdaptiveTeddyBuddyView.MOOD_WARM;
        }
        switch (demoMoodSelected) {
            case "sad":
            case "low":
                return KidsAdaptiveTeddyBuddyView.MOOD_CALM;
            case "good":
            case "great":
                return KidsAdaptiveTeddyBuddyView.MOOD_WARM;
            case "neutral":
            default:
                return KidsAdaptiveTeddyBuddyView.MOOD_NEUTRAL;
        }
    }

    /** Equivalent of dispatch({type:'TOGGLE_ARRAY', field, value}). */
    public static void toggle(LinkedHashSet<String> set, String value) {
        if (set.contains(value)) set.remove(value);
        else set.add(value);
    }

    public List<String> goalsMinusWatchOnly(String watchOnlyLabel) {
        List<String> out = new ArrayList<>();
        for (String g : goals) if (!g.equals(watchOnlyLabel)) out.add(g);
        return out;
    }
}