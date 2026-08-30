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
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;
import java.util.Locale;

public class WordOfWeekActivity extends AppCompatActivity {

    public static final String EXTRA_PHRASE = "extra_phrase";

    private static final float NOOR_VOICE_PITCH = 1.15f;
    private static final float NOOR_VOICE_SPEECH_RATE = 1f;

    private TextView listenFirstVoiceButton;
    private TextToSpeech textToSpeech;
    private String currentPhrase;
    private TextView phraseText;
    private boolean isTtsReady = false;
    private long childId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_of_week);

        childId = getChildId();

        // 🌟 تحميل الأفاتار الخاص بكِ
        loadChildAvatar();

        currentPhrase = getString(R.string.default_phrase);

        phraseText = findViewById(R.id.phraseText);
        phraseText.setText(getString(R.string.loading_phrase));

        // جلب الجملة من Gemini (من كود صديقتك)
        loadPhraseFromGemini();

        // تهيئة محرك Natively TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && textToSpeech != null) {
                textToSpeech.setLanguage(new Locale("ar"));
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
            intent.putExtra("CHILD_ID", childId);
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
                runOnUiThread(() -> phraseText.setText(currentPhrase));
            }
        });
    }

    private void speakPhrase() {
        if (!isTtsReady || textToSpeech == null || currentPhrase == null) {
            return;
        }

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

    // 🌟 دالة تحميل الأفاتار الخاصة بكِ
    private void loadChildAvatar() {
        ChildProfileStore store = new ChildProfileStore(this);
        try {
            List<ChildProfile> profiles = store.getProfiles();
            for (ChildProfile profile : profiles) {
                if (profile.getId() == childId) {
                    String avatar = profile.getAvatar();
                    if (avatar != null && !avatar.trim().isEmpty()) {
                        TextView tvAvatar = findViewById(R.id.mascotFox);
                        if (tvAvatar != null) tvAvatar.setText(avatar);
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        } finally {
            store.close();
        }
    }

    private long getChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);
        return (id == -1L) ? 1L : id;
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