package com.example.graduationproject.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** Persists the daily "streak" counter — a straight port of the artifact-storage logic. */
public class AdultChatStreakPrefs {
    private static final String PREFS = "companion_streak_prefs";
    private static final String KEY_COUNT = "count";
    private static final String KEY_LAST_DATE = "last_date";

    public static int loadAndBump(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String today = fmt.format(new Calendar.Builder().setInstant(System.currentTimeMillis()).build().getTime());

        int count = prefs.getInt(KEY_COUNT, 0);
        String lastDate = prefs.getString(KEY_LAST_DATE, null);

        if (!today.equals(lastDate)) {
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            String yesterdayStr = fmt.format(yesterday.getTime());

            count = yesterdayStr.equals(lastDate) ? count + 1 : 1;
            prefs.edit().putInt(KEY_COUNT, count).putString(KEY_LAST_DATE, today).apply();
        }
        return count;
    }
}
