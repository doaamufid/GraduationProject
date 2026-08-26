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
            new Option("female", "♀", R.string.adaptive_adult_onboarding_gender_female, R.drawable.user_circle_bold),
            new Option("male", "♂", R.string.adaptive_adult_onboarding_gender_male, R.drawable.user_circle_bold),
    };

    // ---- Overall mood ----
    public static final Option[] MOOD_OPTIONS = new Option[]{
            new Option("calm", "🌤", R.string.adaptive_adult_onboarding_mood_calm, R.drawable.ic_sun),
            new Option("pressured", "🌧", R.string.adaptive_adult_onboarding_mood_pressured, R.drawable.ic_rain),
            new Option("hard", "🌪", R.string.adaptive_adult_onboarding_mood_hard, R.drawable.ic_wind),
    };

    // ---- Frequent emotions ----
    public static final Option[] EMOTIONS = new Option[]{
            new Option("tension", "😟", R.string.adaptive_adult_onboarding_emotion_tension, R.drawable.ic_wind),
            new Option("fear", "😨", R.string.adaptive_adult_onboarding_emotion_fear, R.drawable.ic_alert_triangle),
            new Option("sadness", "😔", R.string.adaptive_adult_onboarding_emotion_sadness, R.drawable.ic_heart),
            new Option("irritation", "😣", R.string.adaptive_adult_onboarding_emotion_irritation, R.drawable.ic_zap),
            new Option("anxiety", "😰", R.string.adaptive_adult_onboarding_emotion_anxiety, R.drawable.ic_waves),
            new Option("terror", "😱", R.string.adaptive_adult_onboarding_emotion_terror, R.drawable.ic_alert_triangle),
            new Option("loneliness", "😶", R.string.adaptive_adult_onboarding_emotion_loneliness, R.drawable.ic_user),
            new Option("exhaustion", "😵", R.string.adaptive_adult_onboarding_emotion_exhaustion, R.drawable.ic_clock),
            new Option("unsure", "🫥", R.string.adaptive_adult_onboarding_emotion_unsure, R.drawable.ic_help_circle),
            new Option("okay", "🌤", R.string.adaptive_adult_onboarding_emotion_okay, R.drawable.ic_sun),
    };

    // ---- Safety ----
    public static final Option[] SAFETY_OPTIONS = new Option[]{
            new Option("rarely", null, R.string.adaptive_adult_onboarding_safety_rarely),
            new Option("sometimes", null, R.string.adaptive_adult_onboarding_safety_sometimes),
            new Option("often", null, R.string.adaptive_adult_onboarding_safety_often),
            new Option("most", null, R.string.adaptive_adult_onboarding_safety_most),
            new Option("skip", null, R.string.adaptive_adult_onboarding_safety_skip),
    };

    // ---- Intense fear ----
    public static final Option[] FEAR_OPTIONS = new Option[]{
            new Option("yes_sometimes", null, R.string.adaptive_adult_onboarding_fear_yes_sometimes),
            new Option("yes_often", null, R.string.adaptive_adult_onboarding_fear_yes_often),
            new Option("rarely", null, R.string.adaptive_adult_onboarding_fear_rarely),
            new Option("unsure", null, R.string.adaptive_adult_onboarding_fear_unsure),
            new Option("skip", null, R.string.adaptive_adult_onboarding_fear_skip),
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
            new Option("audio", "🎧", R.string.adaptive_adult_onboarding_helpful_audio, R.drawable.ic_headphones),
            new Option("breathing", "🫁", R.string.adaptive_adult_onboarding_helpful_breathing, R.drawable.ic_wind),
            new Option("spiritual", "🕌", R.string.adaptive_adult_onboarding_helpful_spiritual, R.drawable.ic_sparkles),
            new Option("writing", "✍️", R.string.adaptive_adult_onboarding_helpful_writing, R.drawable.ic_pencil),
            new Option("talking", "💬", R.string.adaptive_adult_onboarding_helpful_talking, R.drawable.ic_chat),
            new Option("movement", "🚶", R.string.adaptive_adult_onboarding_helpful_movement, R.drawable.ic_activity),
            new Option("activity", "🎮", R.string.adaptive_adult_onboarding_helpful_activity, R.drawable.ic_star),
            new Option("unsure", "😶", R.string.adaptive_adult_onboarding_helpful_unsure, R.drawable.ic_help_circle),
    };

    // ---- Goals ----
    public static final Option[] GOALS = new Option[]{
            new Option("calm", "🌤", R.string.adaptive_adult_onboarding_goal_calm, R.drawable.ic_sun),
            new Option("stress", "🫁", R.string.adaptive_adult_onboarding_goal_stress, R.drawable.ic_wind),
            new Option("understand", "🧠", R.string.adaptive_adult_onboarding_goal_understand, R.drawable.ic_brain),
            new Option("routine", "🗓️", R.string.adaptive_adult_onboarding_goal_routine, R.drawable.ic_calendar),
            new Option("sleep", "🌙", R.string.adaptive_adult_onboarding_goal_sleep, R.drawable.ic_moon),
            new Option("loneliness", "🤍", R.string.adaptive_adult_onboarding_goal_loneliness, R.drawable.ic_heart),
            new Option("habits", "🌱", R.string.adaptive_adult_onboarding_goal_habits, R.drawable.ic_trees),
            new Option("express", "✍️", R.string.adaptive_adult_onboarding_goal_express, R.drawable.ic_pencil),
            new Option("explore", "🧭", R.string.adaptive_adult_onboarding_goal_explore, R.drawable.ic_search),
    };
    public static final int GOAL_EXPLORE_RES = R.string.adaptive_adult_onboarding_goal_explore;

    // ---- Mood check-in demo ----
    public static final Option[] DEMO_FACES = new Option[]{
            new Option("sad", "😔", R.string.adaptive_adult_onboarding_face_sad, R.drawable.bg_mood_sad),
            new Option("low", "😕", R.string.adaptive_adult_onboarding_face_low, R.drawable.bg_mood_meh),
            new Option("neutral", "😐", R.string.adaptive_adult_onboarding_face_neutral, R.drawable.bg_mood_neutral),
            new Option("good", "🙂", R.string.adaptive_adult_onboarding_face_good, R.drawable.bg_mood_happy),
            new Option("great", "😄", R.string.adaptive_adult_onboarding_face_great, R.drawable.bg_mood_very_happy),
    };
}
