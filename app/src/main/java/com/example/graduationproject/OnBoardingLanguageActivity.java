package com.example.graduationproject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

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
        setupAnimations();
    }

    private void setupAnimations() {
        // Initial state
        binding.ivIllustration.setAlpha(0f);
        binding.ivIllustration.setScaleX(0.7f);
        binding.ivIllustration.setScaleY(0.7f);
        binding.tvTitle.setAlpha(0f);
        binding.tvTitle.setTranslationY(50f);
        binding.tvDescription.setAlpha(0f);
        binding.tvDescription.setTranslationY(50f);
        binding.btnArabic.setAlpha(0f);
        binding.btnArabic.setTranslationY(80f);
        binding.btnEnglish.setAlpha(0f);
        binding.btnEnglish.setTranslationY(80f);
        binding.btnContinue.setAlpha(0f);
        binding.btnContinue.setTranslationY(100f);

        // Illustration Animation
        ObjectAnimator imageAlpha = ObjectAnimator.ofFloat(binding.ivIllustration, "alpha", 0f, 1f);
        ObjectAnimator imageScaleX = ObjectAnimator.ofFloat(binding.ivIllustration, "scaleX", 0.7f, 1f);
        ObjectAnimator imageScaleY = ObjectAnimator.ofFloat(binding.ivIllustration, "scaleY", 0.7f, 1f);
        AnimatorSet imageSet = new AnimatorSet();
        imageSet.playTogether(imageAlpha, imageScaleX, imageScaleY);
        imageSet.setDuration(1000);
        imageSet.setInterpolator(new OvershootInterpolator());

        // Floating effect for illustration
        ObjectAnimator floating = ObjectAnimator.ofFloat(binding.ivIllustration, "translationY", -20f, 20f);
        floating.setDuration(3000);
        floating.setRepeatCount(ValueAnimator.INFINITE);
        floating.setRepeatMode(ValueAnimator.REVERSE);
        floating.setInterpolator(new AccelerateDecelerateInterpolator());
        floating.start();

        // Title Animation
        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(binding.tvTitle, "alpha", 0f, 1f);
        ObjectAnimator titleMove = ObjectAnimator.ofFloat(binding.tvTitle, "translationY", 50f, 0f);
        titleAlpha.setDuration(800);
        titleMove.setDuration(800);
        titleAlpha.setStartDelay(400);
        titleMove.setStartDelay(400);

        // Description Animation
        ObjectAnimator descAlpha = ObjectAnimator.ofFloat(binding.tvDescription, "alpha", 0f, 1f);
        ObjectAnimator descMove = ObjectAnimator.ofFloat(binding.tvDescription, "translationY", 50f, 0f);
        descAlpha.setDuration(800);
        descMove.setDuration(800);
        descAlpha.setStartDelay(600);
        descMove.setStartDelay(600);

        // Language Buttons Animation
        ObjectAnimator btnArAlpha = ObjectAnimator.ofFloat(binding.btnArabic, "alpha", 0f, 1f);
        ObjectAnimator btnArMove = ObjectAnimator.ofFloat(binding.btnArabic, "translationY", 80f, 0f);
        ObjectAnimator btnEnAlpha = ObjectAnimator.ofFloat(binding.btnEnglish, "alpha", 0f, 1f);
        ObjectAnimator btnEnMove = ObjectAnimator.ofFloat(binding.btnEnglish, "translationY", 80f, 0f);
        
        btnArAlpha.setDuration(800);
        btnArMove.setDuration(800);
        btnArAlpha.setStartDelay(700);
        btnArMove.setStartDelay(700);
        
        btnEnAlpha.setDuration(800);
        btnEnMove.setDuration(800);
        btnEnAlpha.setStartDelay(800);
        btnEnMove.setStartDelay(800);

        // Continue Button Animation
        ObjectAnimator buttonAlpha = ObjectAnimator.ofFloat(binding.btnContinue, "alpha", 0f, 1f);
        ObjectAnimator buttonMove = ObjectAnimator.ofFloat(binding.btnContinue, "translationY", 100f, 0f);
        buttonAlpha.setDuration(800);
        buttonMove.setDuration(800);
        buttonAlpha.setStartDelay(1000);
        buttonMove.setStartDelay(1000);
        buttonMove.setInterpolator(new DecelerateInterpolator());

        AnimatorSet mainSet = new AnimatorSet();
        mainSet.playTogether(imageSet, titleAlpha, titleMove, descAlpha, descMove, btnArAlpha, btnArMove, btnEnAlpha, btnEnMove, buttonAlpha, buttonMove);
        mainSet.start();
    }

    private void setSelectedLanguage(String language) {
        selectedLanguage = AppLanguageManager.normalize(language);
        AppLanguageManager.saveLanguage(this, selectedLanguage);
        updateSelection();
        
        // Refresh layout direction immediately
        binding.getRoot().setLayoutDirection(AppLanguageManager.getLayoutDirection(this));
        
        // Restart activity to apply language changes fully if needed, 
        // but for onboarding, just updating UI text might be enough if using strings.xml
        // However, AppLanguageManager.saveLanguage already calls applyLanguage.
    }

    private void updateSelection() {
        boolean isArabic = AppLanguageManager.isArabic(selectedLanguage);
        binding.btnArabic.setSelected(isArabic);
        binding.btnEnglish.setSelected(!isArabic);

        binding.btnArabic.setBackgroundTintList(getColorStateList(isArabic ? R.color.primary : R.color.white));
        binding.btnArabic.setTextColor(getColor(isArabic ? android.R.color.white : R.color.text_main));

        binding.btnEnglish.setBackgroundTintList(getColorStateList(!isArabic ? R.color.primary : R.color.white));
        binding.btnEnglish.setTextColor(getColor(!isArabic ? android.R.color.white : R.color.text_main));
        
        // Update strings if they are not automatically updated
        binding.tvTitle.setText(R.string.language_selection_title);
        binding.tvDescription.setText(R.string.language_selection_subtitle);
        binding.btnArabic.setText(R.string.language_arabic);
        binding.btnEnglish.setText(R.string.language_english);
        binding.btnContinue.setText(R.string.continue_label);
    }
}
