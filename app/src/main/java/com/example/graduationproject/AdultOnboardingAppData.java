package com.example.graduationproject;

import android.graphics.Color;

import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.models.AdultOnboarding.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static content tables translated 1:1 from the React file's constants:
 * COLORS / STAGES / AGE_BRACKETS / GENDER_OPTIONS / MOOD_OPTIONS / EMOTIONS /
 * SAFETY_OPTIONS / FEAR_OPTIONS / TIME_PERIODS / FOLLOWUPS / HELPFUL / GOALS / DEMO_FACES.
 */
public final class AdultOnboardingAppData {
    private AdultOnboardingAppData() {}

    // ---- Colors (kept here too for quick access outside XML) ----
    public static final int NIGHT = Color.parseColor("#12142B");
    public static final int NIGHT_DEEP = Color.parseColor("#0B0D1F");
    public static final int TWILIGHT = Color.parseColor("#332C55");
    public static final int TWILIGHT_2 = Color.parseColor("#4B3E72");
    public static final int DAWN = Color.parseColor("#7C6A9C");
    public static final int DAWN_2 = Color.parseColor("#C99E82");
    public static final int LIGHT = Color.parseColor("#F4E3C8");
    public static final int LIGHT_2 = Color.parseColor("#FBF0DE");
    public static final int GLOW = Color.parseColor("#FFE3B0");
    public static final int GLOW_SOFT = Color.parseColor("#FFD79A");
    public static final int TEAL = Color.parseColor("#6FA79A");
    public static final int TEAL_SOFT = Color.parseColor("#8FBFB2");
    public static final int INK = Color.parseColor("#211B33");
    public static final int CREAM = Color.parseColor("#F8F2E6");
    public static final int ROSE = Color.parseColor("#C98A8A");

    public static final int TOTAL_SCREENS = 13;

    // ---- Sky stages ----
    public static final Stage[] STAGES = new Stage[]{
            new Stage(NIGHT_DEEP, NIGHT, CREAM, 1f),
            new Stage(NIGHT, TWILIGHT, CREAM, 0.7f),
            new Stage(TWILIGHT, TWILIGHT_2, CREAM, 0.4f),
            new Stage(TWILIGHT_2, DAWN, CREAM, 0.15f),
            new Stage(DAWN, DAWN_2, INK, 0f),
            new Stage(DAWN_2, LIGHT, INK, 0f),
    };

    public static int stageForScreen(int i) {
        if (i <= 1) return 0;
        if (i <= 3) return 1;
        if (i <= 6) return 2;
        if (i <= 8) return 3;
        if (i <= 10) return 4;
        return 5;
    }

    // ---- Identity screen ----
    public static final int[] AGE_BRACKETS = new int[]{
            R.string.adaptive_adult_onboarding_age_under_18,
            R.string.adaptive_adult_onboarding_age_18_24,
            R.string.adaptive_adult_onboarding_age_25_34,
            R.string.adaptive_adult_onboarding_age_35_44,
            R.string.adaptive_adult_onboarding_age_45_54,
            R.string.adaptive_adult_onboarding_age_55_plus
    };

    public static final Option[] GENDER_OPTIONS = new Option[]{
            new Option("female", "\uD83D\uDC69", R.string.adaptive_adult_onboarding_gender_female),
            new Option("male", "\uD83D\uDC68", R.string.adaptive_adult_onboarding_gender_male),
    };

    // ---- Overall mood ----
    public static final Option[] MOOD_OPTIONS = new Option[]{
            new Option("calm", "\uD83C\uDF24", R.string.adaptive_adult_onboarding_mood_calm, Color.parseColor("#E2F5FF")),
            new Option("pressured", "\uD83C\uDF27", R.string.adaptive_adult_onboarding_mood_pressured, Color.parseColor("#FFF4D6")),
            new Option("hard", "\uD83C\uDF2A", R.string.adaptive_adult_onboarding_mood_hard, Color.parseColor("#FFD9CB")),
    };

    // ---- Frequent emotions ----
    public static final Option[] EMOTIONS = new Option[]{
            new Option("tension", "\uD83D\uDE1F", R.string.adaptive_adult_onboarding_emotion_tension, Color.parseColor("#FFF4D6")),
            new Option("fear", "\uD83D\uDE28", R.string.adaptive_adult_onboarding_emotion_fear, Color.parseColor("#FFD9CB")),
            new Option("sadness", "\uD83D\uDE14", R.string.adaptive_adult_onboarding_emotion_sadness, Color.parseColor("#F6E4E4")),
            new Option("irritation", "\uD83D\uDE16", R.string.adaptive_adult_onboarding_emotion_irritation, Color.parseColor("#FFE5E5")),
            new Option("anxiety", "\uD83D\uDE30", R.string.adaptive_adult_onboarding_emotion_anxiety, Color.parseColor("#E2F5FF")),
            new Option("terror", "\uD83D\uDE31", R.string.adaptive_adult_onboarding_emotion_terror, Color.parseColor("#F5E5FF")),
            new Option("loneliness", "\uD83D\uDE36", R.string.adaptive_adult_onboarding_emotion_loneliness, Color.parseColor("#EEE6F5")),
            new Option("exhaustion", "\uD83D\uDE35", R.string.adaptive_adult_onboarding_emotion_exhaustion, Color.parseColor("#E5F9E5")),
            new Option("unsure", "\uD83D\uDE36", R.string.adaptive_adult_onboarding_emotion_unsure, Color.parseColor("#F9F9F9")),
            new Option("okay", "\uD83C\uDF24", R.string.adaptive_adult_onboarding_emotion_okay, Color.parseColor("#E1D7FF")),
    };

    // ---- Safety ----
    public static final Option[] SAFETY_OPTIONS = new Option[]{
            new Option("rarely", null, R.string.adaptive_adult_onboarding_safety_rarely, Color.parseColor("#FFE5E5")),
            new Option("sometimes", null, R.string.adaptive_adult_onboarding_safety_sometimes, Color.parseColor("#FFF4D6")),
            new Option("often", null, R.string.adaptive_adult_onboarding_safety_often, Color.parseColor("#E2F5FF")),
            new Option("most", null, R.string.adaptive_adult_onboarding_safety_most, Color.parseColor("#E5F9E5")),
            new Option("skip", null, R.string.adaptive_adult_onboarding_safety_skip, Color.parseColor("#F9F9F9")),
    };

    // ---- Intense fear ----
    public static final Option[] FEAR_OPTIONS = new Option[]{
            new Option("yes_sometimes", null, R.string.adaptive_adult_onboarding_fear_yes_sometimes, Color.parseColor("#FFD9CB")),
            new Option("yes_often", null, R.string.adaptive_adult_onboarding_fear_yes_often, Color.parseColor("#FFE5E5")),
            new Option("rarely", null, R.string.adaptive_adult_onboarding_fear_rarely, Color.parseColor("#E5F9E5")),
            new Option("unsure", null, R.string.adaptive_adult_onboarding_fear_unsure, Color.parseColor("#FFF4D6")),
            new Option("skip", null, R.string.adaptive_adult_onboarding_fear_skip, Color.parseColor("#F9F9F9")),
    };

    // ---- Timeline periods ----
    public static final String[] TIME_PERIOD_IDS = new String[]{"morning", "day", "evening", "night"};
    public static final Map<String, Integer> TIME_PERIOD_LABELS = new LinkedHashMap<>();
    static {
        TIME_PERIOD_LABELS.put("morning", R.string.adaptive_adult_onboarding_time_morning);
        TIME_PERIOD_LABELS.put("day", R.string.adaptive_adult_onboarding_time_day);
        TIME_PERIOD_LABELS.put("evening", R.string.adaptive_adult_onboarding_time_evening);
        TIME_PERIOD_LABELS.put("night", R.string.adaptive_adult_onboarding_time_night);
    }

    public static class Followup {
        public final int promptRes;
        public final String field; // matches OnboardingData field name
        public final int[] optionsRes;
        public Followup(int promptRes, String field, int... optionsRes) {
            this.promptRes = promptRes;
            this.field = field;
            this.optionsRes = optionsRes;
        }
    }

    public static final Map<String, Followup> FOLLOWUPS = new LinkedHashMap<>();
    static {
        FOLLOWUPS.put("night", new Followup(R.string.adaptive_adult_onboarding_followup_night_prompt, "nightFeelings",
                R.string.adaptive_adult_onboarding_option_loneliness,
                R.string.adaptive_adult_onboarding_option_fear,
                R.string.adaptive_adult_onboarding_option_overthinking,
                R.string.adaptive_adult_onboarding_option_anxiety,
                R.string.adaptive_adult_onboarding_option_sadness,
                R.string.adaptive_adult_onboarding_option_insomnia));
        FOLLOWUPS.put("day", new Followup(R.string.adaptive_adult_onboarding_followup_day_prompt, "dayFeelings",
                R.string.adaptive_adult_onboarding_option_tension,
                R.string.adaptive_adult_onboarding_option_exhaustion,
                R.string.adaptive_adult_onboarding_option_fear,
                R.string.adaptive_adult_onboarding_option_pressure,
                R.string.adaptive_adult_onboarding_option_focus));
        FOLLOWUPS.put("morning", new Followup(R.string.adaptive_adult_onboarding_followup_morning_prompt, "morningFeelings",
                R.string.adaptive_adult_onboarding_option_anxiety,
                R.string.adaptive_adult_onboarding_option_tiredness,
                R.string.adaptive_adult_onboarding_option_dread,
                R.string.adaptive_adult_onboarding_option_starting));
        FOLLOWUPS.put("evening", new Followup(R.string.adaptive_adult_onboarding_followup_evening_prompt, "eveningFeelings",
                R.string.adaptive_adult_onboarding_option_tension,
                R.string.adaptive_adult_onboarding_option_sadness,
                R.string.adaptive_adult_onboarding_option_anxiety,
                R.string.adaptive_adult_onboarding_option_rest));
    }

    // ---- Helpful activities ----
    public static final Option[] HELPFUL = new Option[]{
            new Option("audio", "\uD83C\uDFA7", R.string.adaptive_adult_onboarding_helpful_audio, Color.parseColor("#E2F5FF")),
            new Option("breathing", "\uD83E\uDEC1", R.string.adaptive_adult_onboarding_helpful_breathing, Color.parseColor("#FFD9CB")),
            new Option("spiritual", "\u2728", R.string.adaptive_adult_onboarding_helpful_spiritual, Color.parseColor("#FFF4D6")),
            new Option("writing", "\u270D\uFE0F", R.string.adaptive_adult_onboarding_helpful_writing, Color.parseColor("#F9F9F9")),
            new Option("talking", "\uD83D\uDCAC", R.string.adaptive_adult_onboarding_helpful_talking, Color.parseColor("#F5E5FF")),
            new Option("movement", "\uD83D\uDEB6", R.string.adaptive_adult_onboarding_helpful_movement, Color.parseColor("#E5F9E5")),
            new Option("activity", "\uD83C\uDFAE", R.string.adaptive_adult_onboarding_helpful_activity, Color.parseColor("#E1D7FF")),
            new Option("unsure", "\uD83D\uDE36", R.string.adaptive_adult_onboarding_helpful_unsure, Color.parseColor("#FFFFFF")),
    };

    // ---- Goals ----
    public static final Option[] GOALS = new Option[]{
            new Option("calm", "\uD83C\uDF24", R.string.adaptive_adult_onboarding_goal_calm, Color.parseColor("#E1D7FF")),
            new Option("stress", "\uD83E\uDEC1", R.string.adaptive_adult_onboarding_goal_stress, Color.parseColor("#FFD9CB")),
            new Option("understand", "\uD83E\uDDE0", R.string.adaptive_adult_onboarding_goal_understand, Color.parseColor("#FFF4D6")),
            new Option("routine", "\uD83D\uDDD3\uFE0F", R.string.adaptive_adult_onboarding_goal_routine, Color.parseColor("#FFE5E5")),
            new Option("sleep", "\uD83C\uDF19", R.string.adaptive_adult_onboarding_goal_sleep, Color.parseColor("#E2F5FF")),
            new Option("loneliness", "\uD83E\uDD0D", R.string.adaptive_adult_onboarding_goal_loneliness, Color.parseColor("#F5E5FF")),
            new Option("habits", "\uD83C\uDF31", R.string.adaptive_adult_onboarding_goal_habits, Color.parseColor("#E5F9E5")),
            new Option("express", "\u270D\uFE0F", R.string.adaptive_adult_onboarding_goal_express, Color.parseColor("#F9F9F9")),
            new Option("explore", "\uD83E\uDDEA", R.string.adaptive_adult_onboarding_goal_explore, Color.parseColor("#FFFFFF")),
    };
    public static final int GOAL_EXPLORE_RES = R.string.adaptive_adult_onboarding_goal_explore;

    // ---- Mood check-in demo ----
    public static final Option[] DEMO_FACES = new Option[]{
            new Option("sad", "\uD83D\uDE14", R.string.adaptive_adult_onboarding_face_sad),
            new Option("low", "\uD83D\uDE15", R.string.adaptive_adult_onboarding_face_low),
            new Option("neutral", "\uD83D\uDE10", R.string.adaptive_adult_onboarding_face_neutral),
            new Option("good", "\uD83D\uDE42", R.string.adaptive_adult_onboarding_face_good),
            new Option("great", "\uD83D\uDE04", R.string.adaptive_adult_onboarding_face_great),
    };
}
