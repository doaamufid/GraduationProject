package com.example.graduationproject;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

import app.rive.runtime.kotlin.core.Rive;
import app.rive.runtime.kotlin.core.RendererType;

public class GraduationProjectApplication extends Application {
    private static final String TAG = "GraduationApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Apply saved language preference
        try {
            AppLanguageManager.applySavedLanguage(this);
        } catch (Exception e) {
            Log.w(TAG, "Failed to apply saved locale", e);
        }

        // Initialize Rive with Canvas renderer for better compatibility
        Rive.INSTANCE.init(this, RendererType.Canvas);

        // Initialize SQLCipher libraries
        try {
            net.sqlcipher.database.SQLiteDatabase.loadLibs(this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load SQLCipher libraries", e);
        }
    }

    // Removed forceArabicLocale() as language is now managed by AppLanguageManager
}
