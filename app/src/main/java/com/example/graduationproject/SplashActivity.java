package com.example.graduationproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

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

        startSplashAnimations();

        // Delay navigation until animations are done
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::navigateToNext, 4000);
    }

    private void startSplashAnimations() {
        // 1. Initial State
        binding.icDrop.setScaleX(0f);
        binding.icDrop.setScaleY(0f);
        binding.circleSmall.setScaleX(0f);
        binding.circleSmall.setScaleY(0f);
        binding.circleMid.setScaleX(0f);
        binding.circleMid.setScaleY(0f);
        binding.circleLarge.setScaleX(0f);
        binding.circleLarge.setScaleY(0f);
        binding.textSalam.setAlpha(0f);
        binding.textSalam.setTranslationY(100f);
        binding.textMental.setAlpha(0f);
        binding.textMental.setTranslationY(70f);

        // 2. Animate Clouds (Floating)
        float density = getResources().getDisplayMetrics().density;
        animateCloud(binding.cloudLeft, 6000, 30 * density);
        animateCloud(binding.cloudRight, 8000, -25 * density);
        animateCloud(binding.cloudCenter, 10000, 20 * density);

        // 3. Background "Breathe" Effect
        ObjectAnimator bgAnim = ObjectAnimator.ofFloat(binding.main, "alpha", 0.8f, 1f);
        bgAnim.setDuration(4000);
        bgAnim.setRepeatCount(ValueAnimator.INFINITE);
        bgAnim.setRepeatMode(ValueAnimator.REVERSE);
        bgAnim.start();

        // 4. Entrance Sequence
        // Drop Pop with Rotation
        ObjectAnimator dropScaleX = ObjectAnimator.ofFloat(binding.icDrop, "scaleX", 0f, 1.3f, 1f);
        ObjectAnimator dropScaleY = ObjectAnimator.ofFloat(binding.icDrop, "scaleY", 0f, 1.3f, 1f);
        ObjectAnimator dropRotate = ObjectAnimator.ofFloat(binding.icDrop, "rotation", 0f, 15f, -15f, 0f);
        AnimatorSet dropPop = new AnimatorSet();
        dropPop.playTogether(dropScaleX, dropScaleY, dropRotate);
        dropPop.setDuration(1200);
        dropPop.setInterpolator(new OvershootInterpolator());

        // Pulsing Circles Entrance
        AnimatorSet circlesEntrance = new AnimatorSet();
        circlesEntrance.playTogether(
                createPulseEntrance(binding.circleSmall, 200),
                createPulseEntrance(binding.circleMid, 400),
                createPulseEntrance(binding.circleLarge, 600)
        );

        // Text Appearance
        ObjectAnimator textSalamFade = ObjectAnimator.ofFloat(binding.textSalam, "alpha", 0f, 1f);
        ObjectAnimator textSalamMove = ObjectAnimator.ofFloat(binding.textSalam, "translationY", 100f, 0f);
        ObjectAnimator textMentalFade = ObjectAnimator.ofFloat(binding.textMental, "alpha", 0f, 1f);
        ObjectAnimator textMentalMove = ObjectAnimator.ofFloat(binding.textMental, "translationY", 70f, 0f);

        AnimatorSet textSet = new AnimatorSet();
        textSet.playTogether(textSalamFade, textSalamMove, textMentalFade, textMentalMove);
        textSet.setDuration(1500);
        textSet.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet fullSequence = new AnimatorSet();
        fullSequence.playSequentially(dropPop, textSet);
        fullSequence.start();
        circlesEntrance.start();
    }

    private void animateCloud(View view, long duration, float moveX) {
        ObjectAnimator cloudAnim = ObjectAnimator.ofFloat(view, "translationX", 0f, moveX);
        cloudAnim.setDuration(duration);
        cloudAnim.setRepeatCount(ValueAnimator.INFINITE);
        cloudAnim.setRepeatMode(ValueAnimator.REVERSE);
        cloudAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        cloudAnim.start();
    }

    private AnimatorSet createPulseEntrance(View view, long delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, view.getAlpha());

        AnimatorSet pulse = new AnimatorSet();
        pulse.playTogether(scaleX, scaleY, alpha);
        pulse.setDuration(1500);
        pulse.setStartDelay(delay);
        pulse.setInterpolator(new AnticipateOvershootInterpolator());

        pulse.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Subtle continuous pulse
                ObjectAnimator loopX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f);
                ObjectAnimator loopY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f);
                loopX.setRepeatCount(ValueAnimator.INFINITE);
                loopX.setRepeatMode(ValueAnimator.REVERSE);
                loopY.setRepeatCount(ValueAnimator.INFINITE);
                loopY.setRepeatMode(ValueAnimator.REVERSE);
                loopX.setDuration(2500 + delay);
                loopY.setDuration(2500 + delay);
                loopX.start();
                loopY.start();
            }
        });

        return pulse;
    }

    private void navigateToNext() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean firstRun = prefs.getBoolean("isFirstRun", true);

        Intent intent;
        if (firstRun) {
            intent = new Intent(SplashActivity.this, OnBoardingLanguageActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}