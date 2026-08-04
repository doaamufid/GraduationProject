package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.databinding.ActivitySplashSelectBinding;

public class SplashSelectActivity extends AppCompatActivity {

    private ActivitySplashSelectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySplashSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });

        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // 1. اختيار البالغين
        binding.btnAdultsCard.setOnClickListener(v -> {
            String inputName = binding.etName.getText().toString().trim();
            userPrefs.edit().putString("user_name", inputName).apply();
            userPrefs.edit().putString("user_type", "adult").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            navigateToQuotes();
        });

        // 2. اختيار الأطفال
        binding.btnKidsCard.setOnClickListener(v -> {
            String inputName = binding.etName.getText().toString().trim();
            userPrefs.edit().putString("user_name", inputName).apply();
            userPrefs.edit().putString("user_type", "kid").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            navigateToQuotes();
        });
    }

    private void navigateToQuotes() {
        Intent intent = new Intent(SplashSelectActivity.this, MindfulnessQuotesActivity.class);
        startActivity(intent);
        finish();
    }
}