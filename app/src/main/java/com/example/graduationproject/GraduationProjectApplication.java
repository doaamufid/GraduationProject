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

        // Force Arabic locale + RTL layout direction across the app
        try {
            forceArabicLocale();
        } catch (Exception e) {
            Log.w(TAG, "Failed to set default locale", e);
        }

        // Initialize Rive with Canvas renderer for better compatibility
        Rive.INSTANCE.init(this, RendererType.Canvas);
    }

    private void forceArabicLocale() {
        Locale ar = new Locale("ar");
        Locale.setDefault(ar);

        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(ar);
            config.setLayoutDirection(ar);
            // apply to base context
            createConfigurationContext(config);
        } else {
            config.locale = ar;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(ar);
            }
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }
}
