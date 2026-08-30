package com.example.graduationproject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityOnBoarding1Binding;

public class OnBoardingActivity1 extends AppCompatActivity {

    private ActivityOnBoarding1Binding binding;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(AppLanguageManager.wrapContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this, 
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        binding = ActivityOnBoarding1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> insets);

        setupAnimations();

        binding.btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoardingActivity1.this, OnBoardingActivity2.class);
            ActivityUtils.startActivityWithAnimation(this, intent);
        });

        binding.tvSkip.setOnClickListener(v -> skipOnBoarding());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityUtils.applyBackTransition(this);
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
        binding.btnNext.setAlpha(0f);
        binding.btnNext.setTranslationY(100f);

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

        // Button Animation
        ObjectAnimator buttonAlpha = ObjectAnimator.ofFloat(binding.btnNext, "alpha", 0f, 1f);
        ObjectAnimator buttonMove = ObjectAnimator.ofFloat(binding.btnNext, "translationY", 100f, 0f);
        buttonAlpha.setDuration(800);
        buttonMove.setDuration(800);
        buttonAlpha.setStartDelay(800);
        buttonMove.setStartDelay(800);
        buttonMove.setInterpolator(new DecelerateInterpolator());

        AnimatorSet mainSet = new AnimatorSet();
        mainSet.playTogether(imageSet, titleAlpha, titleMove, descAlpha, descMove, buttonAlpha, buttonMove);
        mainSet.start();
    }

    private void skipOnBoarding() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        preferences.edit().putBoolean("isFirstRun", false).apply();

        Intent intent = new Intent(OnBoardingActivity1.this, SplashSelectActivity.class);
        ActivityUtils.startActivityAndFinishWithAnimation(this, intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}