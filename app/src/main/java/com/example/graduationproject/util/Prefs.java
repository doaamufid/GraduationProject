package com.example.graduationproject.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Port of the JS safeGet/safeSet(localStorage) + todayStr/yesterdayStr helpers.
 */
public class Prefs {

    private static final String FILE = "habit_breathe_prefs";
    private final SharedPreferences sp;

    public Prefs(Context ctx) {
        sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static String todayStr() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
    }

    public static String yesterdayStr() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.getTime());
    }

    public int getStreak() {
        return sp.getInt("breathe_streak", 0);
    }

    public void setStreak(int v) {
        sp.edit().putInt("breathe_streak", v).apply();
    }

    public String getLastSessionDate() {
        return sp.getString("breathe_last_date", null);
    }

    public void setLastSessionDate(String d) {
        sp.edit().putString("breathe_last_date", d).apply();
    }

    public boolean isDark() {
        return sp.getBoolean("breathe_dark", false);
    }

    public void setDark(boolean v) {
        sp.edit().putBoolean("breathe_dark", v).apply();
    }

    public boolean isReminderOn() {
        return sp.getBoolean("breathe_reminder_on", false);
    }

    public void setReminderOn(boolean v) {
        sp.edit().putBoolean("breathe_reminder_on", v).apply();
    }

    public String getReminderTime() {
        return sp.getString("breathe_reminder_time", "20:00");
    }

    public void setReminderTime(String v) {
        sp.edit().putString("breathe_reminder_time", v).apply();
    }

    public String getLengthType() {
        return sp.getString("breathe_length_type", "minutes");
    }

    public void setLengthType(String v) {
        sp.edit().putString("breathe_length_type", v).apply();
    }

    public int getCyclesTarget() {
        return sp.getInt("breathe_cycles_target", 10);
    }

    public void setCyclesTarget(int v) {
        sp.edit().putInt("breathe_cycles_target", v).apply();
    }

    /** Set of mode keys completed today, e.g. {"equal","box"}. Auto-resets on a new day. */
    public Set<String> getCompletedToday() {
        String storedDay = sp.getString("routine_day", "");
        String today = todayStr();
        if (!today.equals(storedDay)) {
            return new HashSet<>();
        }
        return new HashSet<>(sp.getStringSet("routine_completed", new HashSet<>()));
    }

    public void setCompletedToday(Set<String> completed) {
        sp.edit()
                .putString("routine_day", todayStr())
                .putStringSet("routine_completed", completed)
                .apply();
    }

    /** Registers a new completed step, and returns the updated streak logic result. */
    public int registerFullCompletion() {
        String today = todayStr();
        String last = getLastSessionDate();
        int streak = getStreak();
        int newStreak;
        if (today.equals(last)) {
            newStreak = streak > 0 ? streak : 1;
        } else if (yesterdayStr().equals(last)) {
            newStreak = streak + 1;
        } else {
            newStreak = 1;
        }
        setStreak(newStreak);
        setLastSessionDate(today);
        return newStreak;
    }
}
