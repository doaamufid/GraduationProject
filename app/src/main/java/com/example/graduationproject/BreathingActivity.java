package com.example.graduationproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.graduationproject.databinding.ActivityBreathingBinding;
import com.example.graduationproject.databinding.DialogBreathingSettingsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BreathingActivity extends AppCompatActivity {

    private ActivityBreathingBinding binding;
    private ValueAnimator breathingAnimator;
    private ValueAnimator counterAnimator;

    private boolean isSessionRunning = false;
    private boolean isSquareBreathing = false;
    private boolean isVibrationEnabled = true;

    private int currentCycle = 1;
    private final int totalCycles = 5;
    private int currentCycleStep = 0;
    private final long stepDuration = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBreathingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }

    private void updateSubtitleText() {
        if (isSessionRunning) {
            binding.tvSubTitle.setText("دورة " + currentCycle + " من " + totalCycles);
        } else {
            binding.tvSubTitle.setText(isSquareBreathing ? "النمط: التنفس المربع 4-4-4-4" : "النمط: كلاسيكي (5 دورات)");
        }
    }

    private void startBreathingSession() {
        isSessionRunning = true;
        currentCycle = 1;
        currentCycleStep = 0;

        binding.btnStartBreathing.setText("إيقاف الجلسة");
        updateSubtitleText();

        binding.layoutAnchor.setVisibility(View.GONE);
        binding.layoutAnchor2.setVisibility(View.GONE);
        binding.tvDesc.setVisibility(View.GONE);

        // تعديل الهوامش والـ Layout ديناميكياً لتقريب الزر ومنع الفراغ الكبير أثناء الجلسة
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.btnStartBreathing.getLayoutParams();
        params.topToBottom = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = 80; // رفع الزر قليلاً عن الحافة ليكون بارزاً
        binding.btnStartBreathing.setLayoutParams(params);

        runBreathingEngine();
    }

    private void runBreathingEngine() {
        if (!isSessionRunning) return;

        triggerVibration();

        float startScale = 1.0f;
        float endScale = 1.0f;

        if (currentCycleStep == 0) {
            binding.tvState.setText("شهيق");
            startScale = 1.0f;
            endScale = 1.4f;
        } else if (currentCycleStep == 1) {
            binding.tvState.setText("اثبت");
            startScale = 1.4f;
            endScale = 1.4f;
        } else if (currentCycleStep == 2) {
            binding.tvState.setText("زفير");
            startScale = 1.4f;
            endScale = 1.0f;
        } else if (currentCycleStep == 3) {
            binding.tvState.setText("راحة");
            startScale = 1.0f;
            endScale = 1.0f;
        }

        // العداد التنازلي الصحيح (4، 3، 2، 1)
        if (counterAnimator != null) counterAnimator.cancel();
        counterAnimator = ValueAnimator.ofInt(4, 1);
        counterAnimator.setDuration(stepDuration);
        counterAnimator.setInterpolator(new LinearInterpolator());
        counterAnimator.addUpdateListener(animation ->
                binding.tvTimer.setText(String.valueOf(animation.getAnimatedValue()))
        );
        counterAnimator.start();

        // الأنميشن الخاص بحجم الدائرة وتحديث الـ ProgressBar
        if (breathingAnimator != null) breathingAnimator.cancel();

        float finalStartScale = startScale;
        float finalEndScale = endScale;

        breathingAnimator = ValueAnimator.ofFloat(0f, 1f);
        breathingAnimator.setDuration(stepDuration);
        breathingAnimator.setInterpolator(new LinearInterpolator());
        breathingAnimator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();

            float currentScale = finalStartScale + (finalEndScale - finalStartScale) * fraction;
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
                calculateNextStep();
            }
        });

        breathingAnimator.start();
    }

    private void calculateNextStep() {
        if (!isSessionRunning) return;

        if (isSquareBreathing) {
            if (currentCycleStep == 3) {
                currentCycleStep = 0;
                moveToNextCycle();
            } else {
                currentCycleStep++;
                runBreathingEngine();
            }
        } else {
            if (currentCycleStep == 0) {
                currentCycleStep = 2;
                runBreathingEngine();
            } else {
                currentCycleStep = 0;
                moveToNextCycle();
            }
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

        if (breathingAnimator != null) breathingAnimator.cancel();
        if (counterAnimator != null) counterAnimator.cancel();

        binding.btnStartBreathing.setText("ابدأ");
        binding.tvTimer.setText("4");
        binding.tvState.setText("شهيق");
        binding.breathingProgress.setProgress(40);

        binding.frameProgress.setScaleX(1.0f);
        binding.frameProgress.setScaleY(1.0f);

        binding.layoutAnchor.setVisibility(View.VISIBLE);
        binding.layoutAnchor2.setVisibility(View.VISIBLE);
        binding.tvDesc.setVisibility(View.VISIBLE);

        // إرجاع القيود الافتراضية للزر ليعود مكانه الطبيعي أسفل الكروت
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.btnStartBreathing.getLayoutParams();
        params.topToBottom = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = 64;
        binding.btnStartBreathing.setLayoutParams(params);

        updateSubtitleText();

        if (isCompleted) {
//            Toast.makeText(this, "أحسنتِ جداً! جلسة رائعة، كيف تشعرين الآن؟", Toast.LENGTH_LONG).show();
            showSessionFeedbackDialog();
        }
    }

    private void triggerVibration() {
        if (isVibrationEnabled) {
            android.util.Log.d("TEST_VIBRATION", "🎯 الاهتزاز يعمل الآن بنجاح مع النَّفَس!");

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
        // 1. إنشاء الـ BottomSheetDialog
        BottomSheetDialog feedbackDialog = new BottomSheetDialog(this);

        // 2. عمل ViewBinding للملف الجديد الذي صممناه
        com.example.graduationproject.databinding.DialogSessionFeedbackBinding dialogBinding =
                com.example.graduationproject.databinding.DialogSessionFeedbackBinding.inflate(getLayoutInflater());
        feedbackDialog.setContentView(dialogBinding.getRoot());

        // 3. برمجة الأحداث عند الضغط على الإيموجيز (مبدئياً سنظهر توست أو نغير تفاعل الزر)
        dialogBinding.btnMoodHappy.setOnClickListener(v -> {
            // يمكنكِ تمييز الزر بصرياً هنا
            Toast.makeText(this, "رائع! دامت راحتكِ 🌸", Toast.LENGTH_SHORT).show();
        });

        dialogBinding.btnMoodNeutral.setOnClickListener(v -> {
            Toast.makeText(this, "الحمد لله، خطوة جيدة ☀️", Toast.LENGTH_SHORT).show();
        });

        dialogBinding.btnMoodSad.setOnClickListener(v -> {
            Toast.makeText(this, "لا بأس، غداً سيكون أفضل 💪", Toast.LENGTH_SHORT).show();
        });

        // 4. برمجة زر الحفظ والإغلاق
        dialogBinding.btnSaveMood.setOnClickListener(v -> {
            // هنا مستقبلاً ستضعين كود حفظ البيانات في الـ Database
            Toast.makeText(this, "تم حفظ مزاجك بنجاح! 💾", Toast.LENGTH_SHORT).show();
            feedbackDialog.dismiss(); // إغلاق الشاشة المنبثقة
        });

        // 5. عرض الشاشة اللطيفة للمستخدم
        feedbackDialog.show();
    }
    private void showSettingsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DialogBreathingSettingsBinding dialogBinding = DialogBreathingSettingsBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(dialogBinding.getRoot());

        if (isSquareBreathing) {
            dialogBinding.imgCheckClassic.setVisibility(View.GONE);
            dialogBinding.imgCheckSquare.setVisibility(View.VISIBLE);
        } else {
            dialogBinding.imgCheckClassic.setVisibility(View.VISIBLE);
            dialogBinding.imgCheckSquare.setVisibility(View.GONE);
        }

        dialogBinding.switchVibration.setChecked(isVibrationEnabled);

        dialogBinding.optionClassic.setOnClickListener(v -> {
            isSquareBreathing = false;
            binding.tvDesc.setText("الكلاسيكي . بدون هدف إلزامي . استمر أينما ترتاح");
            updateSubtitleText();
            bottomSheetDialog.dismiss();
        });

        dialogBinding.optionSquare.setOnClickListener(v -> {
            isSquareBreathing = true;
            binding.tvDesc.setText("المربع . تركيز عالي . شهيق توقف زفير توقف");
            updateSubtitleText();
            bottomSheetDialog.dismiss();
        });

        dialogBinding.switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> isVibrationEnabled = isChecked);

        bottomSheetDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (breathingAnimator != null) breathingAnimator.cancel();
        if (counterAnimator != null) counterAnimator.cancel();
        binding = null;
    }
}