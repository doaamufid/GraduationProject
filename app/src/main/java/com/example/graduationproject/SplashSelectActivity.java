package com.example.graduationproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.databinding.ActivitySplashSelectBinding;

public class SplashSelectActivity extends AppCompatActivity {

    private ActivitySplashSelectBinding binding;
    private String currentLanguage = "ar"; // Arabic is the app's default setting

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply locale + layout direction before super.onCreate to ensure layout inflation uses it
        String lang = getAppLanguage();
        applyLocale(lang);
        
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySplashSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Explicitly enforce the direction on the root view
        int direction = "ar".equals(lang) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR;
        binding.getRoot().setLayoutDirection(direction);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startEntranceAnimations();

        binding.btnBack.setOnClickListener(v -> finish());

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
            userPrefs.edit().putString("user_type", "adult").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            // Open the adult signup screen after selection
            Intent intent = new Intent(SplashSelectActivity.this, AdultOnboardingMainActivity.class);
            startActivity(intent);
            finish();
        });

        // Effect for Long Press
        binding.btnAdultsCard.setOnLongClickListener(v -> {
            v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
            return false; // allow click after
        });

        // 2. اختيار الأطفال
        binding.btnKidsCard.setOnClickListener(v -> {
            userPrefs.edit().putString("user_type", "kid").apply();
            appPrefs.edit().putBoolean("isFirstRun", false).apply();

            navigateToQuotes();
        });

        binding.btnKidsCard.setOnLongClickListener(v -> {
            v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
            return false;
        });
    }

    /**
     * Returns the active app language.
     * Arabic ("ar") is ALWAYS the default regardless of the device locale,
     * unless the user explicitly chose English ("en") in AppPrefs.
     */
    private String getAppLanguage() {
        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lang = appPrefs.getString("language", "ar"); // default: Arabic
        if (!"ar".equals(lang) && !"en".equals(lang)) {
            lang = "ar"; // Arabic is the default setting for the app
        }
        return lang;
    }

    private boolean isRtl() {
        return "ar".equals(currentLanguage);
    }

    /** Applies the language and its layout direction (RTL for Arabic, LTR for English). */
    private void applyLocale(String lang) {
        currentLanguage = lang;
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void startEntranceAnimations() {
        // 1. Initial state
        binding.icDrop.setScaleX(0f);
        binding.icDrop.setScaleY(0f);
        binding.circleSmall.setScaleX(0f);
        binding.circleSmall.setScaleY(0f);
        binding.circleMid.setScaleX(0f);
        binding.circleMid.setScaleY(0f);
        binding.circleLarge.setScaleX(0f);
        binding.circleLarge.setScaleY(0f);

        binding.cloudLeft.setAlpha(0f);
        binding.cloudLeft.setTranslationX(-50f);
        binding.cloudRight.setAlpha(0f);
        binding.cloudRight.setTranslationX(50f);

        binding.textSalam.setAlpha(0f);
        binding.textSalam.setTranslationY(30f);
        binding.textQuestion.setAlpha(0f);
        binding.textQuestion.setTranslationY(30f);
        binding.textSubQuestion.setAlpha(0f);
        binding.textSubQuestion.setTranslationY(30f);

        binding.btnAdultsCard.setAlpha(0f);
        binding.btnAdultsCard.setTranslationY(100f);
        binding.btnKidsCard.setAlpha(0f);
        binding.btnKidsCard.setTranslationY(100f);

        // 2. Icon Pop (Exact match to splash)
        ObjectAnimator dropScaleX = ObjectAnimator.ofFloat(binding.icDrop, "scaleX", 0f, 1.3f, 1f);
        ObjectAnimator dropScaleY = ObjectAnimator.ofFloat(binding.icDrop, "scaleY", 0f, 1.3f, 1f);
        ObjectAnimator dropRotate = ObjectAnimator.ofFloat(binding.icDrop, "rotation", 0f, 15f, -15f, 0f);
        AnimatorSet dropPop = new AnimatorSet();
        dropPop.playTogether(dropScaleX, dropScaleY, dropRotate);
        dropPop.setDuration(1200);
        dropPop.setInterpolator(new OvershootInterpolator());

        // 3. Staggered Clouds Entrance (Appear one by one)
        AnimatorSet cloudsSet = new AnimatorSet();
        ObjectAnimator cloud1Alpha = ObjectAnimator.ofFloat(binding.cloudLeft, "alpha", 0f, 0.9f);
        ObjectAnimator cloud1Move = ObjectAnimator.ofFloat(binding.cloudLeft, "translationX", -50f, 0f);
        ObjectAnimator cloud2Alpha = ObjectAnimator.ofFloat(binding.cloudRight, "alpha", 0f, 0.9f);
        ObjectAnimator cloud2Move = ObjectAnimator.ofFloat(binding.cloudRight, "translationX", 50f, 0f);

        cloudsSet.playTogether(cloud1Alpha, cloud1Move);
        AnimatorSet cloud2Set = new AnimatorSet();
        cloud2Set.playTogether(cloud2Alpha, cloud2Move);
        cloud2Set.setStartDelay(400);

        // 4. Staggered Circle Entrance (Exact match to splash)
        AnimatorSet circlesEntrance = new AnimatorSet();
        circlesEntrance.playTogether(
                createPulseEntrance(binding.circleSmall, 200),
                createPulseEntrance(binding.circleMid, 400),
                createPulseEntrance(binding.circleLarge, 600)
        );

        // 5. Staggered Text & Cards
        AnimatorSet contentSet = new AnimatorSet();
        contentSet.playTogether(
                ObjectAnimator.ofFloat(binding.textSalam, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.textSalam, "translationY", 30f, 0f),
                ObjectAnimator.ofFloat(binding.textQuestion, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.textQuestion, "translationY", 30f, 0f),
                ObjectAnimator.ofFloat(binding.textSubQuestion, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.textSubQuestion, "translationY", 30f, 0f)
        );
        contentSet.setDuration(800);
        contentSet.setStartDelay(500);

        AnimatorSet footerSet = new AnimatorSet();
        footerSet.playTogether(
                ObjectAnimator.ofFloat(binding.btnAdultsCard, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.btnAdultsCard, "translationY", 100f, 0f),
                ObjectAnimator.ofFloat(binding.btnKidsCard, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.btnKidsCard, "translationY", 100f, 0f)
        );
        footerSet.setDuration(1000);
        footerSet.setStartDelay(900);
        footerSet.setInterpolator(new OvershootInterpolator());

        // Start all
        dropPop.start();
        cloudsSet.start();
        cloud2Set.start();
        circlesEntrance.start();
        contentSet.start();
        footerSet.start();

        // Continuous floating animation
        float density = getResources().getDisplayMetrics().density;
        animateCloud(binding.cloudLeft, 6000, 30 * density);
        animateCloud(binding.cloudRight, 8000, -25 * density);
    }

    private AnimatorSet createPulseEntrance(View view, long delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 0.25f); // Match layout alpha

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

    private void animateCloud(View view, long duration, float moveX) {
        ObjectAnimator cloudAnim = ObjectAnimator.ofFloat(view, "translationX", 0f, moveX);
        cloudAnim.setDuration(duration);
        cloudAnim.setRepeatCount(ValueAnimator.INFINITE);
        cloudAnim.setRepeatMode(ValueAnimator.REVERSE);
        cloudAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        cloudAnim.start();
    }

    private void navigateToQuotes() {
        Intent intent = new Intent(SplashSelectActivity.this, ReflectionActivity.class);
        startActivity(intent);
        finish();
    }
}