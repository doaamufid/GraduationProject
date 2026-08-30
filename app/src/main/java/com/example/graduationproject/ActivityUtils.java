package com.example.graduationproject;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {

    public static void startActivityWithAnimation(Activity activity, Intent intent) {
        activity.startActivity(intent);
        applyTransition(activity);
    }

    public static void startActivityAndFinishWithAnimation(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.finish();
        applyTransition(activity);
    }

    public static void applyTransition(Activity activity) {
        if (AppLanguageManager.isArabic(AppLanguageManager.getSavedLanguage(activity))) {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public static void applyBackTransition(Activity activity) {
        if (AppLanguageManager.isArabic(AppLanguageManager.getSavedLanguage(activity))) {
            overridePendingTransitionArabic(activity);
        } else {
            overridePendingTransitionEnglish(activity);
        }
    }

    private static void overridePendingTransitionArabic(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private static void overridePendingTransitionEnglish(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

