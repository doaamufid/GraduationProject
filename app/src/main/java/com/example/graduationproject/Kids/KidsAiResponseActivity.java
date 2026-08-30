package com.example.graduationproject.Kids;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.ActivityUtils;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.WeeklyMoodAdapter;
import com.example.graduationproject.databinding.ActivityKidsAiResponseBinding;
import com.example.graduationproject.models.MoodDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class KidsAiResponseActivity extends AppCompatActivity {
    private ActivityKidsAiResponseBinding binding;
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;
    private String responseText = "";
    private final List<MoodDay> moodDays = new ArrayList<>();
    private WeeklyMoodAdapter weeklyMoodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge first
        EdgeToEdge.enable(this);

        binding = ActivityKidsAiResponseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Make status bar and navigation bar transparent
        android.view.Window window = getWindow();
        window.setFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        // استقبال نص الرد
        responseText = getIntent().getStringExtra("AI_RESPONSE");
        if (responseText != null && !responseText.isEmpty()) {
            binding.tvAiResponseText.setText(responseText);
        }
binding.btnActionDraw.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(KidsAiResponseActivity.this,DrawInstructionActivity.class);
        startActivity(intent);
        finish();
    }
});
        // تهيئة محرك الصوت لقراءة الرد تلقائياً
        initTextToSpeech();

        // 1. زر "احكي مع صديقك"
        binding.btnActionChat.setOnClickListener(v -> {
            stopSpeech();
            Intent intent = new Intent(KidsAiResponseActivity.this, KidsAiCompanionActivity.class);
            if (!responseText.isEmpty()) {
                intent.putExtra("INITIAL_MESSAGE", responseText);
            }
            ActivityUtils.startActivityWithAnimation(this, intent);
        });

        // 2. زر إعادة الاستماع للصوت
        binding.btnListenVoice.setOnClickListener(v -> speakText(responseText));

        // 3. باقي أزرار الأنشطة
        binding.btnActionBreath.setOnClickListener(v -> {
            stopSpeech();
            ActivityUtils.startActivityWithAnimation(this, new Intent(KidsAiResponseActivity.this, KidsBubbleBreathingActivity.class));
        });

        binding.btnActionTree.setOnClickListener(v -> {
            stopSpeech();
            ActivityUtils.startActivityWithAnimation(this, new Intent(KidsAiResponseActivity.this, KidsTreeActivity.class));
        });

        binding.btnActionDraw.setOnClickListener(v -> {
            stopSpeech();
            ActivityUtils.startActivityWithAnimation(this, new Intent(KidsAiResponseActivity.this, DrawInstructionActivity.class));
        });

        binding.btnActionCalmCorner.setOnClickListener(v -> {
            stopSpeech();
            ActivityUtils.startActivityWithAnimation(this, new Intent(KidsAiResponseActivity.this, com.example.graduationproject.KidsCalmCornerActivity.class));
        });

        binding.btnActionRoutine.setOnClickListener(v -> {
            stopSpeech();
            ActivityUtils.startActivityWithAnimation(this, new Intent(KidsAiResponseActivity.this, com.example.graduationproject.KidsRoutineMainActivity.class));
        });

        setupWeeklyMood();
        setupAnimations();
    }

    private void setupWeeklyMood() {
        moodDays.clear();

        android.content.SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String todayMoodId = appPrefs.getString("today_mood_id", "");
        int todayMoodColor = appPrefs.getInt("today_mood_color", 0xFFEAEEF3); // Default neutral

        Calendar cal = Calendar.getInstance();
        int todayDate = cal.get(Calendar.DAY_OF_YEAR);

        // Start from Saturday of the current week
        Calendar tempCal = (Calendar) cal.clone();
        while (tempCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            tempCal.add(Calendar.DAY_OF_YEAR, -1);
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("ar"));

        for (int i = 0; i < 7; i++) {
            int dateVal = tempCal.get(Calendar.DAY_OF_MONTH);
            int dayOfYear = tempCal.get(Calendar.DAY_OF_YEAR);
            boolean isToday = (dayOfYear == todayDate);

            String dayName = dayFormat.format(tempCal.getTime());
            String dateStr = String.valueOf(dateVal);

            int icon = 0;
            int color = 0;

            if (isToday) {
                if (!todayMoodId.isEmpty()) {
                    icon = R.drawable.ic_smile;
                    color = todayMoodColor;
                }
            }

            moodDays.add(new MoodDay(dayName, dateStr, icon, color, isToday));
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        weeklyMoodAdapter = new WeeklyMoodAdapter(this, moodDays);
        binding.rvWeeklyMood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvWeeklyMood.setAdapter(weeklyMoodAdapter);

        // Scroll to current day
        binding.rvWeeklyMood.post(() -> {
            for (int i = 0; i < moodDays.size(); i++) {
                if (moodDays.get(i).isCurrentDay()) {
                    binding.rvWeeklyMood.scrollToPosition(i);
                    break;
                }
            }
        });
    }

    private void setupAnimations() {
        // Initial state
        binding.ivBearFace.setAlpha(0f);
        binding.ivBearFace.setTranslationY(-50f);
        binding.tvAiResponseText.setAlpha(0f);
        binding.tvAiResponseText.setTranslationY(50f);
        binding.btnListenVoice.setAlpha(0f);
        binding.btnListenVoice.setTranslationY(30f);
        binding.rvWeeklyMood.setAlpha(0f);
        binding.scrollView.setAlpha(0f);

        binding.btnSwitchMode.setAlpha(0f);
        binding.btnSwitchProfile.setAlpha(0f);

        // Bear Animation
        binding.ivBearFace.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(200)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        // Top bar icons animation
        binding.btnSwitchMode.animate().alpha(1f).setDuration(500).setStartDelay(100).start();
        binding.btnSwitchProfile.animate().alpha(1f).setDuration(500).setStartDelay(100).start();

        // Response Bubble Animation
        binding.tvAiResponseText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(400)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();

        // Weekly mood fade in
        binding.rvWeeklyMood.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(500)
                .start();

        // Listen button Animation
        binding.btnListenVoice.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(600)
                .start();

        // Scroll View (contains cards) fade in
        binding.scrollView.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(500)
                .start();
    }

    @Override
    public void onBackPressed() {
        stopSpeech();
        super.onBackPressed();
        ActivityUtils.applyBackTransition(this);
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("ar"));
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true;
                    speakText(responseText);
                }
            }
        });
    }

    private void speakText(String text) {
        if (textToSpeech != null && text != null && !text.isEmpty() && isTtsReady) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NourResponseSpeechID");
        }
    }

    private void stopSpeech() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSpeech();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
