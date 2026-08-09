package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ملاحظة: احذف هذين السطرين قبل إطلاق التطبيق نهائياً
        // للتجريب
//        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();
//        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

            boolean isFirstRun = appPrefs.getBoolean("isFirstRun", true);
            String userType = userPrefs.getString("user_type", null);

            Intent intent;
            if (isFirstRun) {
                intent = new Intent(SplashActivity.this, OnBoardingActivity1.class);
            } else if (userType == null) {
                intent = new Intent(SplashActivity.this, SplashSelectActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();

        }, 3000);
    }
}