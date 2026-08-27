package com.example.graduationproject.models;

public class MoodDay {
    private final String dayName;
    private final String date;
    private final int moodIconRes;
    private final int moodColor;
    private final boolean isCurrentDay;

    public MoodDay(String dayName, String date, int moodIconRes, int moodColor, boolean isCurrentDay) {
        this.dayName = dayName;
        this.date = date;
        this.moodIconRes = moodIconRes;
        this.moodColor = moodColor;
        this.isCurrentDay = isCurrentDay;
    }

    public String getDayName() { return dayName; }
    public String getDate() { return date; }
    public int getMoodIconRes() { return moodIconRes; }
    public int getMoodColor() { return moodColor; }
    public boolean isCurrentDay() { return isCurrentDay; }
}