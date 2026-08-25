package com.example.graduationproject.ui;

import android.graphics.Color;

import com.example.graduationproject.AdultMoodStatsActivity;
import com.example.graduationproject.R;

/**
 * Port of the scoreToMood() helper from MoodStatsScreen.jsx.
 * Converts a 1..5 mood score into a label + colour + face type,
 * used by the chart's scrub tooltip.
 */
public class AdultMoodResult {
    public final String label;
    public final int color;
    public final String face;

    private AdultMoodResult(String label, int color, String face) {
        this.label = label;
        this.color = color;
        this.face = face;
    }

    public static AdultMoodResult from(float score, android.content.Context ctx) {
        if (score < 1.6f) return new AdultMoodResult(ctx.getString(R.string.adult_stats_mood_awful), Color.parseColor("#D9695F"), "awful");
        if (score < 2.3f) return new AdultMoodResult(ctx.getString(R.string.adult_stats_mood_sad), Color.parseColor("#DC9142"), "sad");
        if (score < 3.0f) return new AdultMoodResult(ctx.getString(R.string.adult_stats_mood_low), Color.parseColor("#A47F4C"), "low");
        if (score < 3.5f) return new AdultMoodResult(ctx.getString(R.string.adult_stats_mood_neutral), Color.parseColor("#5C7A93"), "neutral");
        if (score < 4.1f) return new AdultMoodResult(ctx.getString(R.string.adult_stats_mood_calm), Color.parseColor("#2E9884"), "calm");
        if (score < 4.6f) return new AdultMoodResult(ctx.getString(R.string.adult_stats_mood_happy), Color.parseColor("#CE9C15"), "happy");
        return new AdultMoodResult(ctx.getString(R.string.adult_stats_mood_overjoyed), Color.parseColor("#3C9E47"), "overjoyed");
    }
}
