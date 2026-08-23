package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.databinding.ActivityOnBoardingLanguageBinding;

public class OnBoardingLanguageActivity extends AppCompatActivity {
    private ActivityOnBoardingLanguageBinding binding;
    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLanguageManager.applySavedLanguage(this);
        selectedLanguage = AppLanguageManager.getSavedLanguage(this);

        EdgeToEdge.enable(this);
        binding = ActivityOnBoardingLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getRoot().setLayoutDirection(AppLanguageManager.getLayoutDirection(this));
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnArabic.setOnClickListener(v -> setSelectedLanguage(AppLanguageManager.LANGUAGE_ARABIC));
        binding.btnEnglish.setOnClickListener(v -> setSelectedLanguage(AppLanguageManager.LANGUAGE_ENGLISH));
        binding.btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoardingLanguageActivity.this, OnBoardingActivity1.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        binding.tvSkip.setVisibility(View.GONE);
        updateSelection();
    }

    private void setSelectedLanguage(String language) {
        selectedLanguage = AppLanguageManager.normalize(language);
        AppLanguageManager.saveLanguage(this, selectedLanguage);
        updateSelection();
    }

    private void updateSelection() {
        boolean isArabic = AppLanguageManager.isArabic(selectedLanguage);
        binding.btnArabic.setSelected(isArabic);
        binding.btnEnglish.setSelected(!isArabic);

        binding.btnArabic.setBackgroundTintList(getColorStateList(isArabic ? R.color.primary : R.color.white));
        binding.btnArabic.setTextColor(getColor(isArabic ? android.R.color.white : R.color.text_main));

        binding.btnEnglish.setBackgroundTintList(getColorStateList(!isArabic ? R.color.primary : R.color.white));
        binding.btnEnglish.setTextColor(getColor(!isArabic ? android.R.color.white : R.color.text_main));
    }
}
