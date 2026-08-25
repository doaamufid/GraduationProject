package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
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
        binding = ActivityKidsAiResponseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // استقبال نص الرد
        responseText = getIntent().getStringExtra("AI_RESPONSE");
        if (responseText != null && !responseText.isEmpty()) {
            binding.tvAiResponseText.setText(responseText);
        }

        // تهيئة محرك الصوت لقراءة الرد تلقائياً
        initTextToSpeech();

        // 1. زر "احكي مع صديقك" -> يفتح شاشة الشات المباشر (مثل الصورة الأولى)
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

        binding.btnActionBetter.setOnClickListener(v -> {
            stopSpeech();
            finish();
        });

        binding.btnBack.setOnClickListener(v -> {
            stopSpeech();
            finish();
        });
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("ar"));
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true;
                    // نطق الرد فور تجهيز محرك الصوت
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