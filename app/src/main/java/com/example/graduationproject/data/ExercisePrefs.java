package com.example.graduationproject.data;

import android.content.Context;
import android.content.SharedPreferences;

public class ExercisePrefs {

    private static final String PREF_NAME = "exercise_try_counts";
    private final SharedPreferences prefs;

    public ExercisePrefs(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getTryCount(String areaKey) {
        return prefs.getInt(areaKey, 0);
    }

    public int incrementTryCount(String areaKey) {
        int newCount = getTryCount(areaKey) + 1;
        prefs.edit().putInt(areaKey, newCount).apply();
        return newCount;
    }
}