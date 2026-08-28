package com.example.graduationproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.BreathingModeAdapter;
import com.example.graduationproject.databinding.ActivityBreathingBinding;
import com.example.graduationproject.databinding.DialogBreathingSettingsBinding;
import com.example.graduationproject.models.BreathingMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class BreathingActivity extends AppCompatActivity {

    private ActivityBreathingBinding binding;
    private ValueAnimator breathingAnimator;
    private ValueAnimator counterAnimator;

    private boolean isSessionRunning = false;
    private boolean isVibrationEnabled = true;

    private List<BreathingMode> breathingModes;
    private BreathingMode selectedMode;

    private int currentCycle = 1;
    private int totalCycles = 5;
    private int currentCycleStep = 0; // 0: Inhale, 1: Hold1, 2: Exhale, 3: Hold2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Match status bar and task bar with screen color
        EdgeToEdge.enable(this,
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        binding = ActivityBreathingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to the root to handle navigation bar
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            // Apply padding to header to handle status bar
            binding.layoutHeader.setPadding(binding.layoutHeader.getPaddingLeft(), 
                    systemBars.top, 
                    binding.layoutHeader.getPaddingRight(), 
                    binding.layoutHeader.getPaddingBottom());
            
            // Adjust height of layoutHeader to include status bar
            binding.layoutHeader.getLayoutParams().height = (int) (65 * getResources().getDisplayMetrics().density) + systemBars.top;
            binding.layoutHeader.requestLayout();
            
            return insets;
        });

        setupModes();
        selectedMode = breathingModes.get(1); // Default to Box Breathing
        updateSubtitleText();

        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.btnSessionSettings.setOnClickListener(v -> showSettingsBottomSheet());

        binding.btnStartBreathing.setOnClickListener(v -> {
            if (!isSessionRunning) {
                startBreathingSession();
            } else {
                stopBreathingSession(false);
            }
        });

        startEntranceAnimations();
    }

    private void setupModes() {
        breathingModes = new ArrayList<>();
        breathingModes.add(new BreathingMode("التنفس المتساوي", "التنفس المتوازن يساعدك على الاسترخاء والتركيز.", new int[]{4, 0, 4, 0}, 3, R.drawable.calm, Color.parseColor("#FFF5E1")));
        breathingModes.add(new BreathingMode("تنفس المربع", "تنفس المربع طريقة قوية لتقليل التوتر.", new int[]{4, 4, 4, 4}, 4, R.drawable.body, Color.parseColor("#FFEBE1")));
        breathingModes.add(new BreathingMode("تنفس ٤٧٨", "تنفس ٤-٧-٨ يساعد على تحسين النوم.", new int[]{4, 7, 8, 0}, 5, R.drawable.smiley, Color.parseColor("#E1FFE1")));
        breathingModes.add(new BreathingMode("تنفس ٧-١١", "تنفس ٧-١١ يساعد في تقليل القلق وتحسين النوم.", new int[]{7, 0, 11, 0}, 7, R.drawable.sad, Color.parseColor("#E1F5FF")));
        breathingModes.add(new BreathingMode("تنفس مخصص", "اضغط لإنشاء نمط التنفس الخاص بك", new int[]{4, 2, 4, 2}, 2, R.drawable.avatar, Color.parseColor("#F5E1FF")));
    }

    private void startEntranceAnimations() {
        // Initial state
        binding.tvMainTitle.setAlpha(0f);
        binding.tvMainTitle.setTranslationY(-30f);
        binding.tvSubTitle.setAlpha(0f);
        binding.tvSubTitle.setTranslationY(-20f);
        binding.tvDesc.setAlpha(0f);
        binding.tvDesc.setTranslationY(-10f);
        
        binding.frameProgress.setScaleX(0.5f);
        binding.frameProgress.setScaleY(0.5f);
        binding.frameProgress.setAlpha(0f);

        binding.layoutAnchor.setAlpha(0f);
        binding.layoutAnchor.setTranslationX(50f);
        binding.layoutAnchor2.setAlpha(0f);
        binding.layoutAnchor2.setTranslationX(-50f);

        binding.btnStartBreathing.setAlpha(0f);
        binding.btnStartBreathing.setTranslationY(100f);

        // Header and Titles
        AnimatorSet headerSet = new AnimatorSet();
        headerSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvMainTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvMainTitle, "translationY", -30f, 0f),
                ObjectAnimator.ofFloat(binding.tvSubTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvSubTitle, "translationY", -20f, 0f),
                ObjectAnimator.ofFloat(binding.tvDesc, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvDesc, "translationY", -10f, 0f)
        );
        headerSet.setDuration(800);
        headerSet.setInterpolator(new DecelerateInterpolator());

        // Center progress circle
        ObjectAnimator progressAlpha = ObjectAnimator.ofFloat(binding.frameProgress, "alpha", 0f, 1f);
        ObjectAnimator progressScaleX = ObjectAnimator.ofFloat(binding.frameProgress, "scaleX", 0.5f, 1f);
        ObjectAnimator progressScaleY = ObjectAnimator.ofFloat(binding.frameProgress, "scaleY", 0.5f, 1f);
        AnimatorSet progressSet = new AnimatorSet();
        progressSet.playTogether(progressAlpha, progressScaleX, progressScaleY);
        progressSet.setDuration(1000);
        progressSet.setStartDelay(300);
        progressSet.setInterpolator(new OvershootInterpolator());

        // Anchors
        ObjectAnimator anchor1Alpha = ObjectAnimator.ofFloat(binding.layoutAnchor, "alpha", 0f, 1f);
        ObjectAnimator anchor1Move = ObjectAnimator.ofFloat(binding.layoutAnchor, "translationX", 50f, 0f);
        ObjectAnimator anchor2Alpha = ObjectAnimator.ofFloat(binding.layoutAnchor2, "alpha", 0f, 1f);
        ObjectAnimator anchor2Move = ObjectAnimator.ofFloat(binding.layoutAnchor2, "translationX", -50f, 0f);
        AnimatorSet anchorSet = new AnimatorSet();
        anchorSet.playTogether(anchor1Alpha, anchor1Move, anchor2Alpha, anchor2Move);
        anchorSet.setDuration(800);
        anchorSet.setStartDelay(600);
        anchorSet.setInterpolator(new DecelerateInterpolator());

        // Button
        ObjectAnimator buttonAlpha = ObjectAnimator.ofFloat(binding.btnStartBreathing, "alpha", 0f, 1f);
        ObjectAnimator buttonMove = ObjectAnimator.ofFloat(binding.btnStartBreathing, "translationY", 100f, 0f);
        AnimatorSet buttonSet = new AnimatorSet();
        buttonSet.playTogether(buttonAlpha, buttonMove);
        buttonSet.setDuration(800);
        buttonSet.setStartDelay(900);
        buttonSet.setInterpolator(new OvershootInterpolator());

        headerSet.start();
        progressSet.start();
        anchorSet.start();
        buttonSet.start();
    }

    private void updateSubtitleText() {
        if (binding == null || selectedMode == null) return;

        if (isSessionRunning) {
            binding.tvSubTitle.setText("دورة " + currentCycle + " من " + totalCycles);
        } else {
            String patternStr = selectedMode.pattern[0] + "-" + selectedMode.pattern[1] + "-" + selectedMode.pattern[2] + "-" + selectedMode.pattern[3];
            binding.tvSubTitle.setText("النمط: " + selectedMode.name + " (" + patternStr + ")");
            binding.tvDesc.setText(selectedMode.description);
        }
    }

    private void startBreathingSession() {
        if (binding == null || selectedMode == null) return;

        isSessionRunning = true;
        currentCycle = 1;
        currentCycleStep = 0;

        // Calculate total cycles
        int secondsPerCycle = 0;
        for (int s : selectedMode.pattern) secondsPerCycle += s;
        if (secondsPerCycle == 0) secondsPerCycle = 8; // fallback
        totalCycles = (selectedMode.durationMinutes * 60) / secondsPerCycle;
        if (totalCycles < 1) totalCycles = 1;

        binding.btnStartBreathing.setText("إيقاف الجلسة");
        updateSubtitleText();

        // Animate elements out
        binding.layoutAnchor.animate().alpha(0f).translationX(50f).setDuration(300).start();
        binding.layoutAnchor2.animate().alpha(0f).translationX(-50f).setDuration(300).setStartDelay(100).start();
        binding.tvDesc.animate().alpha(0f).setDuration(300).start();

        runBreathingEngine();
    }

    private void runBreathingEngine() {
        if (!isSessionRunning || binding == null || selectedMode == null) return;

        int inhale = selectedMode.pattern[0];
        int hold1 = selectedMode.pattern[1];
        int exhale = selectedMode.pattern[2];
        int hold2 = selectedMode.pattern[3];

        long stepDurationMs;
        String stateText;
        float startScale, endScale;

        if (currentCycleStep == 0) {
            stateText = "شهيق";
            stepDurationMs = inhale * 1000L;
            startScale = 1.0f;
            endScale = 1.4f;
        } else if (currentCycleStep == 1) {
            stateText = "اثبت";
            stepDurationMs = hold1 * 1000L;
            startScale = 1.4f;
            endScale = 1.4f;
        } else if (currentCycleStep == 2) {
            stateText = "زفير";
            stepDurationMs = exhale * 1000L;
            startScale = 1.4f;
            endScale = 1.0f;
        } else {
            stateText = "راحة";
            stepDurationMs = hold2 * 1000L;
            startScale = 1.0f;
            endScale = 1.0f;
        }

        if (stepDurationMs <= 0) {
            moveToNextStep();
            return;
        }

        triggerVibration();
        binding.tvState.setText(stateText);

        // Timer animation
        if (counterAnimator != null) counterAnimator.cancel();
        counterAnimator = ValueAnimator.ofInt((int)(stepDurationMs/1000), 1);
        counterAnimator.setDuration(stepDurationMs);
        counterAnimator.setInterpolator(new LinearInterpolator());
        counterAnimator.addUpdateListener(animation -> {
            if (binding == null || isFinishing() || isDestroyed()) return;
            binding.tvTimer.setText(String.valueOf(animation.getAnimatedValue()));
        });
        counterAnimator.start();

        // Scale and progress animation
        if (breathingAnimator != null) breathingAnimator.cancel();

        breathingAnimator = ValueAnimator.ofFloat(0f, 1f);
        breathingAnimator.setDuration(stepDurationMs);
        breathingAnimator.setInterpolator(new LinearInterpolator());
        breathingAnimator.addUpdateListener(animation -> {
            if (binding == null || isFinishing() || isDestroyed()) return;

            float fraction = animation.getAnimatedFraction();
            float currentScale = startScale + (endScale - startScale) * fraction;
            binding.frameProgress.setScaleX(currentScale);
            binding.frameProgress.setScaleY(currentScale);

            int progressPercent = (int) (fraction * 100);
            if (currentCycleStep == 0) {
                binding.breathingProgress.setProgress(progressPercent);
            } else if (currentCycleStep == 2) {
                binding.breathingProgress.setProgress(100 - progressPercent);
            } else if (currentCycleStep == 1) {
                binding.breathingProgress.setProgress(100);
            } else {
                binding.breathingProgress.setProgress(0);
            }
        });

        breathingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isSessionRunning && binding != null && !isFinishing() && !isDestroyed()) {
                    moveToNextStep();
                }
            }
        });

        breathingAnimator.start();
    }

    private void moveToNextStep() {
        if (!isSessionRunning || binding == null) return;

        if (currentCycleStep >= 3) {
            currentCycleStep = 0;
            moveToNextCycle();
        } else {
            currentCycleStep++;
            runBreathingEngine();
        }
    }

    private void moveToNextCycle() {
        if (currentCycle >= totalCycles) {
            stopBreathingSession(true);
        } else {
            currentCycle++;
            updateSubtitleText();
            runBreathingEngine();
        }
    }

    private void stopBreathingSession(boolean isCompleted) {
        isSessionRunning = false;
        cancelAnimators();

        if (binding == null) return;

        binding.btnStartBreathing.setText("ابدأ الجلسة");
        binding.tvTimer.setText("4");
        binding.tvState.setText("شهيق");
        binding.breathingProgress.setProgress(40);

        binding.frameProgress.setScaleX(1.0f);
        binding.frameProgress.setScaleY(1.0f);

        // Animate elements back in
        binding.layoutAnchor.animate().alpha(1f).translationX(0f).setDuration(400).start();
        binding.layoutAnchor2.animate().alpha(1f).translationX(0f).setDuration(400).setStartDelay(100).start();
        binding.tvDesc.animate().alpha(1f).setDuration(400).start();

        updateSubtitleText();

        if (isCompleted) {
            showSessionFeedbackDialog();
        }
    }

    private void triggerVibration() {
        if (isVibrationEnabled) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(150);
                }
            }
        }
    }

    private void showSessionFeedbackDialog() {
        if (isFinishing() || isDestroyed()) return;

        BottomSheetDialog feedbackDialog = new BottomSheetDialog(this);
        com.example.graduationproject.databinding.DialogSessionFeedbackBinding dialogBinding =
                com.example.graduationproject.databinding.DialogSessionFeedbackBinding.inflate(getLayoutInflater());
        feedbackDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnMoodHappy.setOnClickListener(v ->
                Toast.makeText(this, "رائع! دامت راحتكِ 🌸", Toast.LENGTH_SHORT).show()
        );

        dialogBinding.btnMoodNeutral.setOnClickListener(v ->
                Toast.makeText(this, "الحمد لله، خطوة جيدة ☀️", Toast.LENGTH_SHORT).show()
        );

        dialogBinding.btnMoodSad.setOnClickListener(v ->
                Toast.makeText(this, "لا بأس، غداً سيكون أفضل 💪", Toast.LENGTH_SHORT).show()
        );

        dialogBinding.btnSaveMood.setOnClickListener(v -> {
            Toast.makeText(this, "تم حفظ مزاجك بنجاح! 💾", Toast.LENGTH_SHORT).show();
            feedbackDialog.dismiss();
        });

        feedbackDialog.show();
    }

    private void showSettingsBottomSheet() {
        if (isFinishing() || isDestroyed()) return;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DialogBreathingSettingsBinding dialogBinding = DialogBreathingSettingsBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.rvBreathingModes.setLayoutManager(new LinearLayoutManager(this));
        BreathingModeAdapter adapter = new BreathingModeAdapter(breathingModes, mode -> {
            selectedMode = mode;
            updateSubtitleText();
            bottomSheetDialog.dismiss();
        });
        dialogBinding.rvBreathingModes.setAdapter(adapter);

        dialogBinding.switchVibration.setChecked(isVibrationEnabled);
        dialogBinding.switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> isVibrationEnabled = isChecked);

        bottomSheetDialog.show();
    }

    private void cancelAnimators() {
        if (breathingAnimator != null) {
            breathingAnimator.removeAllUpdateListeners();
            breathingAnimator.removeAllListeners();
            breathingAnimator.cancel();
            breathingAnimator = null;
        }
        if (counterAnimator != null) {
            counterAnimator.removeAllUpdateListeners();
            counterAnimator.removeAllListeners();
            counterAnimator.cancel();
            counterAnimator = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSessionRunning) {
            stopBreathingSession(false);
        }
    }

    @Override
    protected void onDestroy() {
        isSessionRunning = false;
        cancelAnimators();
        binding = null;
        super.onDestroy();
    }
}
