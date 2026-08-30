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

import com.example.graduationproject.databinding.ActivityKidsAiResponseBinding;

import java.util.Locale;

public class KidsAiResponseActivity extends AppCompatActivity {
    private ActivityKidsAiResponseBinding binding;
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;
    private String responseText = "";

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
            startActivity(intent);
        });

        // 2. زر إعادة الاستماع للصوت
        binding.btnListenVoice.setOnClickListener(v -> speakText(responseText));

        // 3. باقي أزرار الأنشطة
        binding.btnActionBreath.setOnClickListener(v -> {
            stopSpeech();
            startActivity(new Intent(KidsAiResponseActivity.this, KidsBubbleBreathingActivity.class));
        });

        binding.btnActionTree.setOnClickListener(v -> {
            stopSpeech();
            startActivity(new Intent(KidsAiResponseActivity.this, KidsTreeActivity.class));
        });

        binding.btnActionDraw.setOnClickListener(v -> {
            stopSpeech();
            startActivity(new Intent(KidsAiResponseActivity.this, DrawInstructionActivity.class));
        });

        binding.btnBack.setOnClickListener(v -> {
            stopSpeech();
            finish();
        });

        binding.btnSwitchProfile.setOnClickListener(v -> {
            stopSpeech();
            Intent intent = new Intent(KidsAiResponseActivity.this, ChildProfilesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
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
