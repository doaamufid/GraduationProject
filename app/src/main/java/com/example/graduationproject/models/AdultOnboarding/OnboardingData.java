package com.example.graduationproject.models.AdultOnboarding;

import java.util.ArrayList;
import java.util.List;

/**
 * Plain data holder mirroring the React `initialData` / reducer state.
 * All multi-select fields are backed by ArrayLists so TOGGLE_ARRAY behaviour
 * (add if absent, remove if present) is trivial to reproduce.
 */
public class OnboardingData {

    public String nickname = "";
    public boolean nicknameProvided = false;

    public Integer ageRangeIndex = null; // null = not chosen
    public String gender = null; // "female" | "male" | "unspecified" | null

    public String overallMood = null; // "calm" | "pressured" | "hard" | "unsure"
    public List<String> frequentEmotions = new ArrayList<>();

    public String safetyFeeling = null; // rarely/sometimes/often/most/skip
    public String intenseFearExperience = null; // yes_sometimes/yes_often/rarely/unsure/skip

    public List<String> difficultTimes = new ArrayList<>(); // morning/day/evening/night
    public List<String> morningFeelings = new ArrayList<>();
    public List<String> dayFeelings = new ArrayList<>();
    public List<String> eveningFeelings = new ArrayList<>();
    public List<String> nightFeelings = new ArrayList<>();

    public List<String> helpfulActivities = new ArrayList<>();
    public List<String> goals = new ArrayList<>();

    public String demoMoodSelected = null;
    public boolean moodCheckinEnabled = true;
    public boolean onboardingCompleted = false;

    public void reset() {
        nickname = "";
        nicknameProvided = false;
        ageRangeIndex = null;
        gender = null;
        overallMood = null;
        frequentEmotions = new ArrayList<>();
        safetyFeeling = null;
        intenseFearExperience = null;
        difficultTimes = new ArrayList<>();
        morningFeelings = new ArrayList<>();
        dayFeelings = new ArrayList<>();
        eveningFeelings = new ArrayList<>();
        nightFeelings = new ArrayList<>();
        helpfulActivities = new ArrayList<>();
        goals = new ArrayList<>();
        demoMoodSelected = null;
        moodCheckinEnabled = true;
        onboardingCompleted = false;
    }

    /** Deep-ish copy used to simulate local persistence (like savedRef in the RN app). */
    public OnboardingData copy() {
        OnboardingData d = new OnboardingData();
        d.nickname = nickname;
        d.nicknameProvided = nicknameProvided;
        d.ageRangeIndex = ageRangeIndex;
        d.gender = gender;
        d.overallMood = overallMood;
        d.frequentEmotions = new ArrayList<>(frequentEmotions);
        d.safetyFeeling = safetyFeeling;
        d.intenseFearExperience = intenseFearExperience;
        d.difficultTimes = new ArrayList<>(difficultTimes);
        d.morningFeelings = new ArrayList<>(morningFeelings);
        d.dayFeelings = new ArrayList<>(dayFeelings);
        d.eveningFeelings = new ArrayList<>(eveningFeelings);
        d.nightFeelings = new ArrayList<>(nightFeelings);
        d.helpfulActivities = new ArrayList<>(helpfulActivities);
        d.goals = new ArrayList<>(goals);
        d.demoMoodSelected = demoMoodSelected;
        d.moodCheckinEnabled = moodCheckinEnabled;
        d.onboardingCompleted = onboardingCompleted;
        return d;
    }

    public void restoreFrom(OnboardingData other) {
        nickname = other.nickname;
        nicknameProvided = other.nicknameProvided;
        ageRangeIndex = other.ageRangeIndex;
        gender = other.gender;
        overallMood = other.overallMood;
        frequentEmotions = new ArrayList<>(other.frequentEmotions);
        safetyFeeling = other.safetyFeeling;
        intenseFearExperience = other.intenseFearExperience;
        difficultTimes = new ArrayList<>(other.difficultTimes);
        morningFeelings = new ArrayList<>(other.morningFeelings);
        dayFeelings = new ArrayList<>(other.dayFeelings);
        eveningFeelings = new ArrayList<>(other.eveningFeelings);
        nightFeelings = new ArrayList<>(other.nightFeelings);
        helpfulActivities = new ArrayList<>(other.helpfulActivities);
        goals = new ArrayList<>(other.goals);
        demoMoodSelected = other.demoMoodSelected;
        moodCheckinEnabled = other.moodCheckinEnabled;
        onboardingCompleted = other.onboardingCompleted;
    }

    public static void toggle(List<String> list, String value) {
        if (list.contains(value)) list.remove(value);
        else list.add(value);
    }
}
