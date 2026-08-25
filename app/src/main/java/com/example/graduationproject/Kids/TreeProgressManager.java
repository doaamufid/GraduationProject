package com.example.graduationproject.Kids;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.graduationproject.R;

public class TreeProgressManager {

    private static final String PREF_NAME = "KidsTreePrefs";
    private static final String KEY_POINTS_PREFIX = "child_points_";

    private final SharedPreferences prefs;
    private final Context context;
    private final String childName;

    public TreeProgressManager(Context context, String childName) {
        this.context = context;
        this.childName = (childName != null && !childName.trim().isEmpty()) ? childName.trim() : "default_child";
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public TreeProgressManager(Context context) {
        this(context, "default_child");
    }

    private String getChildKey() {
        return KEY_POINTS_PREFIX + childName;
    }

    public void addPoints(int pointsToAdd) {
        int currentPoints = getPoints();
        prefs.edit().putInt(getChildKey(), currentPoints + pointsToAdd).apply();
    }

    public int getPoints() {
        return prefs.getInt(getChildKey(), 0);
    }

    public void resetPoints() {
        prefs.edit().putInt(getChildKey(), 0).apply();
    }

    public int getStageNumber() {
        int points = getPoints();
        if (points < 50) return 1;  // البذرة (0 - 49 نقطة)
        if (points < 150) return 2; // البرعم (50 - 149 نقطة)
        if (points < 300) return 3; // الشجرة (150 - 299 نقطة)
        return 4;                   // الشجرة المثمرة (300+ نقطة)
    }

    public String getStageName() {
        switch (getStageNumber()) {
            case 1: return "مرحلة البذرة";
            case 2: return "مرحلة البرعم";
            case 3: return "مرحلة الشجرة";
            default: return "مرحلة الشجرة المثمرة";
        }
    }

    public int getProgressPercentage() {
        int points = getPoints();
        if (points < 50) return (points * 100) / 50;
        if (points < 150) return ((points - 50) * 100) / 100;
        if (points < 300) return ((points - 150) * 100) / 150;
        return 100;
    }
}