package com.example.graduationproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import androidx.core.view.ViewCompat;

import java.util.Locale;

public final class AppLanguageManager {
    public static final String PREFS_NAME = "AppPrefs";
    public static final String KEY_LANGUAGE = "language";
    public static final String LANGUAGE_ARABIC = "ar";
    public static final String LANGUAGE_ENGLISH = "en";

    private AppLanguageManager() {
    }

    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH);
        return normalize(language);
    }

    public static void saveLanguage(Context context, String language) {
        String normalized = normalize(language);
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, normalized)
                .apply();
        applyLanguage(context, normalized);
    }

    public static void applySavedLanguage(Context context) {
        applyLanguage(context, getSavedLanguage(context));
    }

    public static Context wrapContext(Context context) {
        String language = getSavedLanguage(context);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(locale);
            }
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }

    public static void applyLanguage(Context context, String language) {
        String normalized = normalize(language);
        Locale locale = new Locale(normalized);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(locale);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            View decorView = activity.getWindow().getDecorView();
            decorView.setLayoutDirection(isRtl(normalized) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
            ViewCompat.setLayoutDirection(decorView, isRtl(normalized) ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);
        }
    }

    public static boolean isRtl(String language) {
        return LANGUAGE_ARABIC.equals(normalize(language));
    }

    public static boolean isArabic(String language) {
        return LANGUAGE_ARABIC.equals(normalize(language));
    }

    public static String normalize(String language) {
        if (LANGUAGE_ENGLISH.equalsIgnoreCase(language)) {
            return LANGUAGE_ENGLISH;
        }
        return LANGUAGE_ARABIC;
    }

    public static int getLayoutDirection(Context context) {
        return isRtl(getSavedLanguage(context)) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR;
    }

    public static void restartApp(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
