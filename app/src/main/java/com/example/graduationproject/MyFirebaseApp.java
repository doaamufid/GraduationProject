package com.example.graduationproject;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class MyFirebaseApp extends Application {

    private static final String TAG = "MyFirebaseApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // تهيئة Firebase أول شي
        FirebaseApp.initializeApp(this);

        // تفعيل App Check بوضع "Debug" - مخصص للتطوير المحلي فقط
        // (بدون هاد، الطلبات لـ Gemini ممكن تترفض بسبب حماية App Check)
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
        );

        Log.d(TAG, "Firebase App Check (Debug Provider) تم تفعيله بنجاح");
    }
}