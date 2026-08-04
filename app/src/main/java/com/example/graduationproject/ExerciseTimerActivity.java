package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.databinding.ActivityExerciseTimerBinding;

public class ExerciseTimerActivity extends AppCompatActivity {

    private ActivityExerciseTimerBinding binding;
    private String areaKey;
    private CountDownTimer countDownTimer;
    private boolean isEligibleForSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        areaKey = getIntent().getStringExtra("area_key");
        String exerciseTitle = getIntent().getStringExtra("exercise_title");
        String exerciseDesc = getIntent().getStringExtra("exercise_desc");
        int durationMinutes = getIntent().getIntExtra("duration_minutes", 1);

        binding.tvExerciseTitle.setText(exerciseTitle);
        binding.tvExerciseDesc.setText(exerciseDesc);
        binding.btnCompleteExercise.setVisibility(View.GONE);

        long totalMillis = durationMinutes * 60 * 1000L;
        startTimer(totalMillis);

        binding.btnCompleteExercise.setOnClickListener(v -> completeExercise());
        binding.btnBack.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            Toast.makeText(this, "تم إلغاء التمرين، استمر لفترة أطول في المرة القادمة", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void startTimer(long totalMillis) {
        countDownTimer = new CountDownTimer(totalMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                binding.tvTimer.setText(String.format("%02d:%02d", minutes, seconds));

                long elapsedSeconds = (totalMillis - millisUntilFinished) / 1000;
                long cycleTime = elapsedSeconds % 8; // دورة مريحة مدتها 8 ثوانٍ كاملة

                if (cycleTime < 4) {
                    // 4 ثوانٍ الأولى: شهيق متصاعد وتكبير تدريجي للدائرة
                    binding.tvBreathingState.setText("شهيق...");
                    animateCircle(1.25f);
                } else {
                    // 4 ثوانٍ المتبقية: زفير هادئ وانكماش سلس للدائرة لحجمها الأصلي
                    binding.tvBreathingState.setText("زفير...");
                    animateCircle(1.0f);
                }

                long elapsedMillis = totalMillis - millisUntilFinished;
                if (!isEligibleForSuccess && elapsedMillis >= (totalMillis * 0.75)) {
                    isEligibleForSuccess = true;
                    binding.btnCompleteExercise.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("00:00");
                binding.tvBreathingState.setText("أحسنت!");
                isEligibleForSuccess = true;
                binding.btnCompleteExercise.setVisibility(View.VISIBLE);
                completeExercise();
            }
        }.start();
    }

    // دالة الأنيميشن السلسة للنبض الدائري المتجاوب
    private void animateCircle(float scale) {
        binding.breathingCircleContainer.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(3800) // المدة تغطي زمن الشهيق/الزفير بشكل انسيابي بطيء ومريح للأعصاب
                .start();
    }

    private void completeExercise() {
        if (countDownTimer != null) countDownTimer.cancel();
        Intent result = new Intent();
        result.putExtra("area_key", areaKey);
        if (isEligibleForSuccess) {
            setResult(RESULT_OK, result);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}