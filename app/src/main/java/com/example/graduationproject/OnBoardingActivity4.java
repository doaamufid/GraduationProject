package com.example.graduationproject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityOnBoarding4Binding;

public class OnBoardingActivity4 extends AppCompatActivity {

    private ActivityOnBoarding4Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.activity.EdgeToEdge.enable(this);
        binding = ActivityOnBoarding4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            // Apply top padding to the skip button area or the first visible element
            if (binding.tvSkip != null) {
                binding.tvSkip.setPadding(0, systemBars.top, 0, 0);
            }
            return insets;
        });

        setupAnimations();

        binding.btnStart.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            preferences.edit().putBoolean("isFirstRun", false).apply();

            Intent intent = new Intent(OnBoardingActivity4.this, SplashSelectActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        if (binding.tvSkip != null) {
            binding.tvSkip.setVisibility(android.view.View.GONE);
        }
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
        binding.btnStart.setAlpha(0f);
        binding.btnStart.setTranslationY(100f);

        // Illustration Animation
        ObjectAnimator imageAlpha = ObjectAnimator.ofFloat(binding.ivIllustration, "alpha", 0f, 1f);
        ObjectAnimator imageScaleX = ObjectAnimator.ofFloat(binding.ivIllustration, "scaleX", 0.7f, 1f);
        ObjectAnimator imageScaleY = ObjectAnimator.ofFloat(binding.ivIllustration, "scaleY", 0.7f, 1f);
        AnimatorSet imageSet = new AnimatorSet();
        imageSet.playTogether(imageAlpha, imageScaleX, imageScaleY);
        imageSet.setDuration(1000);
        imageSet.setInterpolator(new OvershootInterpolator());

        // Floating effect
        ObjectAnimator floating = ObjectAnimator.ofFloat(binding.ivIllustration, "translationY", -20f, 20f);
        floating.setDuration(3000);
        floating.setRepeatCount(ValueAnimator.INFINITE);
        floating.setRepeatMode(ValueAnimator.REVERSE);
        floating.setInterpolator(new AccelerateDecelerateInterpolator());
        floating.start();

        // Text & Button Animation
        AnimatorSet textSet = new AnimatorSet();
        textSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvTitle, "translationY", 50f, 0f),
                ObjectAnimator.ofFloat(binding.tvDescription, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvDescription, "translationY", 50f, 0f),
                ObjectAnimator.ofFloat(binding.btnStart, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.btnStart, "translationY", 100f, 0f)
        );
        textSet.setDuration(800);
        textSet.setStartDelay(400);
        textSet.setInterpolator(new DecelerateInterpolator());

        AnimatorSet mainSet = new AnimatorSet();
        mainSet.playTogether(imageSet, textSet);
        mainSet.start();
    }
}