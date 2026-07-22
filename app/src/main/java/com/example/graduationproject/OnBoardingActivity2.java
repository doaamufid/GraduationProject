package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityOnBoarding2Binding;

public class OnBoardingActivity2 extends AppCompatActivity {

    private ActivityOnBoarding2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoarding2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // زر التالي -> الشاشة الثالثة
        binding.btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoardingActivity2.this, OnBoardingActivity3.class);
            startActivity(intent);
        });

        // زر تخطي
        binding.tvSkip.setOnClickListener(v -> skipOnBoarding());
    }

    private void skipOnBoarding() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        preferences.edit().putBoolean("isFirstRun", false).apply();

        Intent intent = new Intent(OnBoardingActivity2.this, SplashSelectActivity.class);
        startActivity(intent);
        finish();
    }
}