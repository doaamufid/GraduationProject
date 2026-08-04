package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

import java.util.Locale;

/**
 * الشاشة الأولى: بتعرض "كلمة القوة" لهالأسبوع وتخلي الطفل يسمعها ثم يسجلها بصوته.
 *
 * ملاحظة: الجملة حالياً ثابتة (DEFAULT_PHRASE). لو بدك كل أسبوع جملة مختلفة،
 * أسهل طريقة إنك تحط مصفوفة جمل + رقم الأسبوع الحالي وتختار منها هون.
 */
public class WordOfWeekActivity extends AppCompatActivity {

    public static final String EXTRA_PHRASE = "extra_phrase";

    private TextToSpeech textToSpeech;
    private String currentPhrase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_of_week);

        currentPhrase = getString(R.string.default_phrase);

        TextView phraseText = findViewById(R.id.phraseText);
        phraseText.setText(currentPhrase);

        // تهيئة "قراءة النص بصوت عالي" لزر "اسمعها بصوت نور الأول".
        // ده حل شغّال فوراً بدون ملفات صوتية جاهزة؛ لو عندك تسجيل حقيقي
        // بصوت الشخصية (نور)، بدّل هالجزء بتشغيل MediaPlayer لملف raw بدل الـ TTS.
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                textToSpeech.setLanguage(new Locale("ar"));
            }
        });

        findViewById(R.id.listenFirstVoiceButton).setOnClickListener(v -> speakPhrase());

        ImageButton micButton = findViewById(R.id.micButton);
        micButton.setOnClickListener(v -> {
            Intent intent = new Intent(WordOfWeekActivity.this, RecordingActivity.class);
            intent.putExtra(EXTRA_PHRASE, currentPhrase);
            startActivity(intent);
        });
    }

    private void speakPhrase() {
        if (textToSpeech != null) {
            textToSpeech.speak(currentPhrase, TextToSpeech.QUEUE_FLUSH, null, "phrase_utterance");
        }
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
