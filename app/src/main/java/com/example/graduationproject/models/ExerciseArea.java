package com.example.graduationproject.models;

import java.io.Serializable;

public class ExerciseArea implements Serializable {
    public String key;            // head / chest / shoulders / stomach
    public String title;          // الرأس والرقبة
    public String subtitle;       // صداع • توتر
    public String badgeTextColor; // #B7950B
    public String badgeBgColor;   // #FFF9E7

    public String exerciseTitle;  // تحرير الرقبة - التنفس والتأمل
    public String exerciseDesc;
    public int durationMinutes;
    public int repsCount;
    public boolean isOffline;

    public ExerciseArea(String key, String title, String subtitle,
                        String badgeTextColor, String badgeBgColor,
                        String exerciseTitle, String exerciseDesc,
                        int durationMinutes, int repsCount, boolean isOffline) {
        this.key = key;
        this.title = title;
        this.subtitle = subtitle;
        this.badgeTextColor = badgeTextColor;
        this.badgeBgColor = badgeBgColor;
        this.exerciseTitle = exerciseTitle;
        this.exerciseDesc = exerciseDesc;
        this.durationMinutes = durationMinutes;
        this.repsCount = repsCount;
        this.isOffline = isOffline;
    }
}