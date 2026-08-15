package com.example.graduationproject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Locale;

/**
 * Enhanced Java/XML port of AdultOnboardingDetailsScreen.
 * Features slowed entrance animations and continuous looping animations for background elements.
 */
public class AdultSignupActivity extends AppCompatActivity {

    private String[] ageRanges = null;
    private static final String GENDER_FEMALE = "female";
    private static final String GENDER_MALE = "male";

    // ---- state ----
    private String selectedAge = null;
    private String selectedGender = null;

    // ---- views ----
    private LinearLayout nameFieldContainer;
    private EditText editName;
    private FlexboxLayout ageChipContainer;
    private LinearLayout cardFemale, cardMale;
    private TextView txtFemaleLabel, txtMaleLabel;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force Arabic as the default language and RTL
        setLocale("ar");
        
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        
        setContentView(R.layout.activity_main);

        ageRanges = getResources().getStringArray(R.array.age_ranges);

        applyWindowInsets();
        bindViews();
        renderAgeChips();
        setupNameField();
        setupGenderCards();
        setupContinueButton();

        // Start animations with a slightly longer initial delay
        new Handler().postDelayed(this::startEntranceAnimations, 500);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        android.content.res.Resources res = getResources();
        android.content.res.Configuration config = new android.content.res.Configuration(res.getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void applyWindowInsets() {
        View rootView = findViewById(R.id.root_view);
        View buttonContainer = findViewById(R.id.btn_continue);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            View logo = findViewById(R.id.logo_container);
            if (logo.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) logo.getLayoutParams();
                lp.topMargin = (int) (80 * getResources().getDisplayMetrics().density) + statusBarHeight;
                logo.setLayoutParams(lp);
            }

            if (buttonContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams btnLp = (ViewGroup.MarginLayoutParams) buttonContainer.getLayoutParams();
                btnLp.bottomMargin = (int) (32 * getResources().getDisplayMetrics().density) + navBarHeight;
                buttonContainer.setLayoutParams(btnLp);
            }

            return insets;
        });
    }

    private void bindViews() {
        nameFieldContainer = findViewById(R.id.name_field_container);
        editName = findViewById(R.id.edit_name);
        ageChipContainer = findViewById(R.id.age_chip_container);
        cardFemale = findViewById(R.id.card_female);
        cardMale = findViewById(R.id.card_male);
        txtFemaleLabel = findViewById(R.id.txt_female_label);
        txtMaleLabel = findViewById(R.id.txt_male_label);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void startEntranceAnimations() {
        View logo = findViewById(R.id.logo_container);
        View ring1 = findViewById(R.id.ring_1);
        View ring2 = findViewById(R.id.ring_2);
        View ring3 = findViewById(R.id.ring_3);
        View title = findViewById(R.id.txt_title);
        View subtitle = findViewById(R.id.txt_subtitle);
        View scroll = findViewById(R.id.scroll_view);
        View cloud1 = findViewById(R.id.cloud_1);
        View cloud2 = findViewById(R.id.cloud_2);

        // Slowed Logo Animation (1000ms instead of 600ms)
        logo.animate().alpha(1).scaleX(1).scaleY(1)
                .setDuration(1000)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .start();

        // Slowed Rings Entrance (1200ms duration, increased staggered delays)
        animateRingEntrance(ring1, 400);
        animateRingEntrance(ring2, 700);
        animateRingEntrance(ring3, 1000);

        // Clouds Entry
        cloud1.animate().alpha(0.7f).setDuration(1500).start();
        cloud2.animate().alpha(0.6f).setDuration(1500).start();
        
        // Start continuous cloud floating
        startFloatingAnimation(cloud1, 3000, 20f);
        startFloatingAnimation(cloud2, 4000, -15f);

        // Content Slide Up (Slowed)
        animateSlideUp(title, 800);
        animateSlideUp(subtitle, 1100);
        animateSlideUp(scroll, 1400);
        
        // Button Entrance
        float buttonTargetAlpha = (selectedAge != null && selectedGender != null) ? 1f : 0.4f;
        btnContinue.animate()
                .alpha(buttonTargetAlpha)
                .translationY(0)
                .setDuration(1000)
                .setStartDelay(1600)
                .start();
    }

    private void animateRingEntrance(View ring, long delay) {
        ring.animate()
                .alpha(1)
                .scaleX(1)
                .scaleY(1)
                .setStartDelay(delay)
                .setDuration(1200)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> startLoopingPulse(ring)) // Start looping after entrance
                .start();
    }

    /**
     * Continuous breathing/pulsing animation for the rings.
     */
    private void startLoopingPulse(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.15f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.15f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0.5f);

        scaleX.setDuration(2500);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleY.setDuration(2500);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        alpha.setDuration(2500);
        alpha.setRepeatCount(ValueAnimator.INFINITE);
        alpha.setRepeatMode(ValueAnimator.REVERSE);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleX.start();
        scaleY.start();
        alpha.start();
    }

    /**
     * Continuous floating animation for clouds.
     */
    private void startFloatingAnimation(View view, int duration, float deltaY) {
        ObjectAnimator floatY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, deltaY);
        floatY.setDuration(duration);
        floatY.setRepeatCount(ValueAnimator.INFINITE);
        floatY.setRepeatMode(ValueAnimator.REVERSE);
        floatY.setInterpolator(new AccelerateDecelerateInterpolator());
        floatY.start();
    }

    private void animateSlideUp(View view, long delay) {
        view.animate()
                .alpha(1)
                .translationY(0)
                .setStartDelay(delay)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void setupNameField() {
        editName.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s.toString().trim().length() > 0;
                nameFieldContainer.setBackgroundResource(
                        hasText ? R.drawable.bg_input_field_active : R.drawable.bg_input_field);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void renderAgeChips() {
        ageChipContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String range : ageRanges) {
            TextView chip = (TextView) inflater.inflate(R.layout.item_age_chip, ageChipContainer, false);
            chip.setText(range);
            styleAgeChip(chip, range.equals(selectedAge));

            chip.setOnClickListener(v -> {
                selectedAge = range;
                renderAgeChips();
                updateContinueState();
            });

            ageChipContainer.addView(chip);
        }
    }

    private void styleAgeChip(TextView chip, boolean selected) {
        if (selected) {
            chip.setBackgroundResource(R.drawable.bg_age_chip_selected);
            chip.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            chip.setBackgroundResource(R.drawable.bg_age_chip);
            chip.setTextColor(ContextCompat.getColor(this, R.color.text_main));
        }
    }

    private void setupGenderCards() {
        cardFemale.setOnClickListener(v -> {
            selectedGender = GENDER_FEMALE;
            renderGenderCards();
            updateContinueState();
        });
        cardMale.setOnClickListener(v -> {
            selectedGender = GENDER_MALE;
            renderGenderCards();
            updateContinueState();
        });
        renderGenderCards();
    }

    private void renderGenderCards() {
        boolean femaleSelected = GENDER_FEMALE.equals(selectedGender);
        boolean maleSelected = GENDER_MALE.equals(selectedGender);

        cardFemale.setBackgroundResource(femaleSelected ? R.drawable.bg_gender_card_selected : R.drawable.bg_gender_card);
        txtFemaleLabel.setTextColor(ContextCompat.getColor(this, femaleSelected ? R.color.primary : R.color.text_main));

        cardMale.setBackgroundResource(maleSelected ? R.drawable.bg_gender_card_selected : R.drawable.bg_gender_card);
        txtMaleLabel.setTextColor(ContextCompat.getColor(this, maleSelected ? R.color.primary : R.color.text_main));
    }

    private void setupContinueButton() {
        btnContinue.setOnClickListener(v -> {
            if (!btnContinue.isEnabled()) return;
        });
    }

    private void updateContinueState() {
        boolean canContinue = selectedAge != null && selectedGender != null;
        btnContinue.setEnabled(canContinue);

        float target = canContinue ? 1f : 0.4f;
        
        if (btnContinue.getAlpha() > 0 && btnContinue.getAlpha() != target) {
            ObjectAnimator.ofFloat(btnContinue, View.ALPHA, btnContinue.getAlpha(), target)
                    .setDuration(300)
                    .start();
        }
    }
}
