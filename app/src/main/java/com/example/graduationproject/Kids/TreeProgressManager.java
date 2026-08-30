package com.example.graduationproject.Kids;

import android.content.Context;
import android.content.SharedPreferences;

public class TreeProgressManager {

    private static final String PREF_NAME = "KidsTreePrefs";
    private static final String KEY_POINTS_PREFIX = "child_points_";
    public static final int POINTS_PER_STAGE = 50;

    private final SharedPreferences prefs;
    private final long childId;

    public TreeProgressManager(Context context, long childId) {
        this.childId = childId;
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getChildKey() {
        return KEY_POINTS_PREFIX + childId; // 🌟 حفظ وقراءة النقاط بالـ ID الفريد فقط
    }

    public void addPoints(int pointsToAdd) {
        int currentPoints = getTotalPoints();
        prefs.edit().putInt(getChildKey(), currentPoints + pointsToAdd).apply();
    }

    public int getTotalPoints() {
        return prefs.getInt(getChildKey(), 0);
    }

    public int getStagePoints() {
        if (getStageNumber() >= 4) {
            return POINTS_PER_STAGE;
        }
        return getTotalPoints() % POINTS_PER_STAGE;
    }

    public void resetPoints() {
        prefs.edit().putInt(getChildKey(), 0).apply();
    }

    public int getStageNumber() {
        int total = getTotalPoints();
        if (total < 50) return 1;
        if (total < 100) return 2;
        if (total < 150) return 3;
        return 4;
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
        if (getStageNumber() >= 4) return 100;
        return (getStagePoints() * 100) / POINTS_PER_STAGE;
    }
}