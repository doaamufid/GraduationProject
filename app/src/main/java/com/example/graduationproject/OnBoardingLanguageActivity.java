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

        binding.cardArabic.setOnClickListener(v -> setSelectedLanguage(AppLanguageManager.LANGUAGE_ARABIC));
        binding.cardEnglish.setOnClickListener(v -> setSelectedLanguage(AppLanguageManager.LANGUAGE_ENGLISH));
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
        binding.cardArabic.setAlpha(0f);
        binding.cardArabic.setTranslationY(80f);
        binding.cardEnglish.setAlpha(0f);
        binding.cardEnglish.setTranslationY(80f);
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

        // Language Cards Animation
        ObjectAnimator btnArAlpha = ObjectAnimator.ofFloat(binding.cardArabic, "alpha", 0f, 1f);
        ObjectAnimator btnArMove = ObjectAnimator.ofFloat(binding.cardArabic, "translationY", 80f, 0f);
        ObjectAnimator btnEnAlpha = ObjectAnimator.ofFloat(binding.cardEnglish, "alpha", 0f, 1f);
        ObjectAnimator btnEnMove = ObjectAnimator.ofFloat(binding.cardEnglish, "translationY", 80f, 0f);
        
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
    }

    private void updateSelection() {
        boolean isArabic = AppLanguageManager.isArabic(selectedLanguage);
        int blueColor = getColor(R.color.primary);

        // Update Arabic Card state
        binding.cardArabic.setAlpha(isArabic ? 1.0f : 0.4f);
        binding.tvLanguageNameAr.setTextColor(isArabic ? blueColor : android.graphics.Color.WHITE);

        // Update English Card state
        binding.cardEnglish.setAlpha(!isArabic ? 1.0f : 0.4f);
        binding.tvLanguageNameEn.setTextColor(!isArabic ? blueColor : android.graphics.Color.WHITE);

        binding.btnContinue.setText(R.string.continue_label);
    }
}
