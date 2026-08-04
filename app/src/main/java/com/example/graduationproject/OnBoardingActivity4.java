package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityOnBoarding4Binding;

public class OnBoardingActivity4 extends AppCompatActivity {

    private ActivityOnBoarding4Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoarding4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            preferences.edit().putBoolean("isFirstRun", false).apply();

            Intent intent = new Intent(OnBoardingActivity4.this, SplashSelectActivity.class);
            startActivity(intent);
            finish();
        });

        if (binding.tvSkip != null) {
            binding.tvSkip.setVisibility(android.view.View.GONE);
        }
    }
}