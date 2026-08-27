package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

import java.util.Locale;

/**
 * الشاشة الأولى: بتعرض "كلمة القوة" لهالأسبوع وتخلي الطفل يسمعها ثم يسجلها بصوته.
 *
 * الكلمة هلأ بتنولد ديناميكياً من Gemini في كل مرة (مش ثابتة).
 * لحد ما يوصل الرد من Gemini، منعرض جملة افتراضية (fallback) من strings.xml
 * عشان الشاشة ما تظل فاضية أو معلّقة.
 */
public class WordOfWeekActivity extends AppCompatActivity {

    public static final String EXTRA_PHRASE = "extra_phrase";

    // إعدادات صوت "نور" لتصير ودودة ومناسبة للأطفال: نبرة أعلى شوي وسرعة أبطأ قليلاً
    private static final float NOOR_VOICE_PITCH = 1.15f;
    private static final float NOOR_VOICE_SPEECH_RATE = 1f;
    private TextView listenFirstVoiceButton;
    private TextToSpeech textToSpeech;
    private String currentPhrase;
    private TextView phraseText;
    private boolean isTtsReady = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_of_week);

        // fallback مؤقت لحد ما يوصل رد Gemini، أو لو صار خطأ بالاتصال
        currentPhrase = getString(R.string.default_phrase);

        phraseText = findViewById(R.id.phraseText);
        phraseText.setText(getString(R.string.loading_phrase));

        loadPhraseFromGemini();

        // تهيئة "قراءة النص بصوت عالي" لزر "اسمعها بصوت نور الأول".
        // ده حل شغّال فوراً بدون ملفات صوتية جاهزة؛ لو عندك تسجيل حقيقي
        // بصوت الشخصية (نور)، بدّل هالجزء بتشغيل MediaPlayer لملف raw بدل الـ TTS.
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                textToSpeech.setLanguage(new Locale("ar"));
                // نبرة وسرعة مضبوطتين لصوت أقرب لأسلوب دافئ ومناسب للأطفال
                textToSpeech.setPitch(NOOR_VOICE_PITCH);
                textToSpeech.setSpeechRate(NOOR_VOICE_SPEECH_RATE);
                isTtsReady = true;
            }
        });

        listenFirstVoiceButton = findViewById(R.id.listenFirstVoiceButton);
        listenFirstVoiceButton.setOnClickListener(v -> speakPhrase());

        ImageButton micButton = findViewById(R.id.micButton);
        micButton.setOnClickListener(v -> {
            Intent intent = new Intent(WordOfWeekActivity.this, RecordingActivity.class);
            intent.putExtra(EXTRA_PHRASE, currentPhrase);
            startActivity(intent);
        });
    }

    private void loadPhraseFromGemini() {
        new GeminiService().generatePhrase(new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    currentPhrase = message;
                    phraseText.setText(currentPhrase);
                });
            }

            @Override
            public void onError(String errorMessage) {
                // ما في نت أو صار خطأ - منرجع للجملة الافتراضية بدل ما نعلّق الطفل
                runOnUiThread(() -> phraseText.setText(currentPhrase));
            }
        });
    }

    private void speakPhrase() {
        if (!isTtsReady || textToSpeech == null || currentPhrase == null) {
            return;
        }

        // نعطّل الزر أثناء الكلام حتى ما يصير ضغط متكرر يشغّل أكتر من نسخة فوق بعضها
        listenFirstVoiceButton.setEnabled(false);

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(() -> listenFirstVoiceButton.setEnabled(true));
            }

            @Override
            public void onError(String utteranceId) {
                runOnUiThread(() -> listenFirstVoiceButton.setEnabled(true));
            }
        });

        textToSpeech.speak(currentPhrase, TextToSpeech.QUEUE_FLUSH, null, "phrase_utterance");
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}