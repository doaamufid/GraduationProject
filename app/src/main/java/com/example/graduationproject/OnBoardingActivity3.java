package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityOnBoarding3Binding;

public class OnBoardingActivity3 extends AppCompatActivity {

    private ActivityOnBoarding3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoarding3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // زر التالي -> الشاشة الرابعة
        binding.btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoardingActivity3.this, OnBoardingActivity4.class);
            startActivity(intent);
        });

        // زر تخطي
        binding.tvSkip.setOnClickListener(v -> skipOnBoarding());
    }

    private void skipOnBoarding() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        preferences.edit().putBoolean("isFirstRun", false).apply();

        Intent intent = new Intent(OnBoardingActivity3.this, SplashSelectActivity.class);
        startActivity(intent);
        finish();
    }
}