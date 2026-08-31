package com.example.graduationproject;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

import app.rive.runtime.kotlin.core.Rive;
import app.rive.runtime.kotlin.core.RendererType;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class GraduationProjectApplication extends Application {
    private static final String TAG = "GraduationApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize App Check
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
            );
            Log.d(TAG, "Firebase App Check (Debug Provider) initialized");
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
            );
            Log.d(TAG, "Firebase App Check (Play Integrity) initialized");
        }

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
