package com.example.graduationproject.Kids;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.graduationproject.R;

public class TreeProgressManager {

    private static final String PREF_NAME = "KidsTreePrefs";
    private static final String KEY_POINTS_PREFIX = "child_points_";

    private final SharedPreferences prefs;
    private final Context context;
    private final String childName; // حفظ اسم الطفل الحالي

    // البناء المعدل لاستقبال اسم الطفل
    public TreeProgressManager(Context context, String childName) {
        this.context = context;
        this.childName = (childName != null && !childName.trim().isEmpty()) ? childName.trim() : "default_child";
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // بناء افتراضي لمنع كسر الكود القديم إن وجد
    public TreeProgressManager(Context context) {
        this(context, "default_child");
    }

    // مفتاح خزن ديناميكي خاص بكل طفل
    private String getChildKey() {
        return KEY_POINTS_PREFIX + childName;
    }

    public void addPoints(int pointsToAdd) {
        int currentPoints = getPoints();
        prefs.edit().putInt(getChildKey(), currentPoints + pointsToAdd).apply();
    }

    public int getPoints() {
        return prefs.getInt(getChildKey(), 0); // القيمة الافتراضية 0 لكل طفل جديد
    }

    public void resetPoints() {
        prefs.edit().putInt(getChildKey(), 0).apply();
    }

    public int getStageNumber() {
        int points = getPoints();
        if (points < 50) return 1;
        if (points < 150) return 2;
        if (points < 300) return 3;
        return 4;
    }

    // جلب اسم المرحلة مترجم تلقائياً حسب لغة الجهاز
    public String getStageName() {
        switch (getStageNumber()) {
            case 1: return context.getString(R.string.stage_sprout);
            case 2: return context.getString(R.string.stage_bud);
            case 3: return context.getString(R.string.stage_tree);
            default: return context.getString(R.string.stage_fruit_tree);
        }
    }

    public int getStageImageRes() {
        switch (getStageNumber()) {
            case 1: return R.drawable.ic_plant_sprout;
            case 2: return R.drawable.ic_plant_bud;
            case 3: return R.drawable.ic_tree_growing;
            default: return R.drawable.ic_tree_fruit;
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