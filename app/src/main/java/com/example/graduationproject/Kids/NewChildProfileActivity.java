package com.example.graduationproject.Kids;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityNewChildProfileBinding;

public class NewChildProfileActivity extends AppCompatActivity {
    private static final String[] AVATARS = {"🦊", "🐻", "🐰", "🐼", "🐨"};
    private static final int[] AGES = {4, 5, 6, 7, 8, 9};
    private static final String GENDER_BOY = "ولد";
    private static final String GENDER_GIRL = "بنت";
    private static final int GENDER_DEFAULT_TEXT_COLOR = Color.rgb(93, 64, 55);


    private ActivityNewChildProfileBinding binding;
    private ChildProfileStore childProfileStore;
    private TextView selectedAgeView;
    private TextView selectedGenderView;
    private int selectedAge = -1;
    private String selectedGender = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewChildProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        childProfileStore = new ChildProfileStore(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            binding.btnBack.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        startEntranceAnimations();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnStart.setOnClickListener(v -> saveProfileAndFinish());
        binding.etChildName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStartState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        setupAgeButtons();
        setupGenderButtons();
    }

    private void startEntranceAnimations() {
        // Initial State
        binding.tvMascot.setScaleX(0f);
        binding.tvMascot.setScaleY(0f);
        binding.tvTitle.setAlpha(0f);
        binding.tvTitle.setTranslationY(30f);
        binding.tvSubtitle.setAlpha(0f);
        binding.tvSubtitle.setTranslationY(30f);

        binding.tvNameLabel.setAlpha(0f);
        binding.etChildName.setAlpha(0f);
        binding.etChildName.setTranslationY(50f);
        binding.tvNameNote.setAlpha(0f);

        binding.tvAgeLabel.setAlpha(0f);
        binding.ageScroll.setAlpha(0f);
        binding.ageScroll.setTranslationY(50f);

        binding.tvGenderLabel.setAlpha(0f);
        binding.genderContainer.setAlpha(0f);
        binding.genderContainer.setTranslationY(50f);

        binding.btnStart.setAlpha(0f);
        binding.btnStart.setTranslationY(100f);

        // Animations
        ObjectAnimator mascotPopX = ObjectAnimator.ofFloat(binding.tvMascot, "scaleX", 0f, 1f);
        ObjectAnimator mascotPopY = ObjectAnimator.ofFloat(binding.tvMascot, "scaleY", 0f, 1f);
        AnimatorSet mascotSet = new AnimatorSet();
        mascotSet.playTogether(mascotPopX, mascotPopY);
        mascotSet.setDuration(1000);
        mascotSet.setInterpolator(new OvershootInterpolator());

        AnimatorSet headerSet = new AnimatorSet();
        headerSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvTitle, "translationY", 30f, 0f),
                ObjectAnimator.ofFloat(binding.tvSubtitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvSubtitle, "translationY", 30f, 0f)
        );
        headerSet.setDuration(800);
        headerSet.setStartDelay(300);

        AnimatorSet nameSet = new AnimatorSet();
        nameSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvNameLabel, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.etChildName, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.etChildName, "translationY", 50f, 0f),
                ObjectAnimator.ofFloat(binding.tvNameNote, "alpha", 0f, 1f)
        );
        nameSet.setDuration(800);
        nameSet.setStartDelay(500);

        AnimatorSet ageSet = new AnimatorSet();
        ageSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvAgeLabel, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.ageScroll, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.ageScroll, "translationY", 50f, 0f)
        );
        ageSet.setDuration(800);
        ageSet.setStartDelay(700);

        AnimatorSet genderSet = new AnimatorSet();
        genderSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvGenderLabel, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.genderContainer, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.genderContainer, "translationY", 50f, 0f)
        );
        genderSet.setDuration(800);
        genderSet.setStartDelay(900);

        AnimatorSet buttonSet = new AnimatorSet();
        buttonSet.playTogether(
                ObjectAnimator.ofFloat(binding.btnStart, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.btnStart, "translationY", 100f, 0f)
        );
        buttonSet.setDuration(1000);
        buttonSet.setStartDelay(1100);
        buttonSet.setInterpolator(new OvershootInterpolator());

        mascotSet.start();
        headerSet.start();
        nameSet.start();
        ageSet.start();
        genderSet.start();
        buttonSet.start();

        // Floating Mascot
        ObjectAnimator floating = ObjectAnimator.ofFloat(binding.tvMascot, "translationY", -15f, 15f);
        floating.setDuration(2500);
        floating.setRepeatCount(ValueAnimator.INFINITE);
        floating.setRepeatMode(ValueAnimator.REVERSE);
        floating.setInterpolator(new AccelerateDecelerateInterpolator());
        floating.start();
    }

    private void setupAgeButtons() {
        int size = (int) (44 * getResources().getDisplayMetrics().density);
        int margin = (int) (6 * getResources().getDisplayMetrics().density);

        for (int age : AGES) {
            TextView ageView = new TextView(this);
            ageView.setText(String.valueOf(age));
            ageView.setGravity(Gravity.CENTER);
            ageView.setTextSize(15);
            ageView.setTypeface(ageView.getTypeface(), android.graphics.Typeface.BOLD);
            ageView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            ageView.setBackgroundResource(R.drawable.bg_child_age_default);
            ageView.setOnClickListener(v -> selectAge((TextView) v, age));
            
            // Effect for long press
            ageView.setOnLongClickListener(v -> {
                v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start();
                return false;
            });

            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            binding.ageContainer.addView(ageView, params);
        }
    }

    private void selectAge(TextView ageView, int age) {
        if (selectedAgeView != null) {
            selectedAgeView.setBackgroundResource(R.drawable.bg_child_age_default);
            selectedAgeView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }

        selectedAge = age;
        selectedAgeView = ageView;
        selectedAgeView.setBackgroundResource(R.drawable.bg_child_age_selected);
        selectedAgeView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        
        binding.worldCard.setVisibility(View.VISIBLE);
        binding.worldCard.setAlpha(0f);
        binding.worldCard.setScaleY(0.5f);
        binding.worldCard.animate().alpha(1f).scaleY(1f).setDuration(500).setInterpolator(new OvershootInterpolator()).start();
        
        updateStartState();
    }

    private void setupGenderButtons() {
        binding.btnGenderBoy.setOnClickListener(v -> selectGender(binding.btnGenderBoy, GENDER_BOY));
        binding.btnGenderGirl.setOnClickListener(v -> selectGender(binding.btnGenderGirl, GENDER_GIRL));
        
        binding.btnGenderBoy.setOnLongClickListener(v -> {
            v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
            return false;
        });
        binding.btnGenderGirl.setOnLongClickListener(v -> {
            v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
            return false;
        });
    }

    private void selectGender(TextView genderView, String gender) {
        if (selectedGenderView != null) {
            selectedGenderView.setBackgroundResource(R.drawable.bg_child_age_default);
            selectedGenderView.setTextColor(GENDER_DEFAULT_TEXT_COLOR);
        }

        selectedGender = gender;
        selectedGenderView = genderView;
        selectedGenderView.setBackgroundResource(R.drawable.bg_child_age_selected);
        selectedGenderView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        updateStartState();
    }

    private void updateStartState() {
        boolean canStart = selectedAge > 0
                && !selectedGender.isEmpty()
                && !binding.etChildName.getText().toString().trim().isEmpty();

        binding.btnStart.setEnabled(canStart);
        binding.btnStart.setBackgroundResource(canStart
                ? R.drawable.bg_child_start_enabled
                : R.drawable.bg_child_start_disabled);
        
        if (canStart) {
            binding.btnStart.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).withEndAction(() -> {
                binding.btnStart.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        childProfileStore.close();
        super.onDestroy();
    }

    private void saveProfileAndFinish() {
        String name = binding.etChildName.getText().toString().trim();
        if (name.isEmpty() || selectedAge <= 0 || selectedGender.isEmpty()) {
            Toast.makeText(this, "اكتب الاسم واختر العمر والجنس", Toast.LENGTH_SHORT).show();
            return;
        }
        childProfileStore.addProfile(name, selectedAge, selectedGender, AVATARS[selectedAge % AVATARS.length]);
        setResult(RESULT_OK);
        finish();
    }
}