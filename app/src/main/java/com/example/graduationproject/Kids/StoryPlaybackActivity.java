package com.example.graduationproject.Kids;

import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * شاشة "الفيديو المولّد": بدل فيديو حقيقي، بتعرض قصة قصيرة يولّدها Gemini
 * حسب التصنيف، وبتقرأها بصوت عالي عن طريق TextToSpeech، مع خلفية فيديو
 * صامتة متكررة (loop) مرتبطة بنفس التصنيف.
 */
public class StoryPlaybackActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_TITLE = "extra_title";

    private static final String UTTERANCE_ID = "story_utterance";

    // ربط كل تصنيف بملف الفيديو الخلفي (raw resource name بدون امتداد)
    private static final Map<String, String> CATEGORY_TO_VIDEO = new HashMap<>();
    static {
        CATEGORY_TO_VIDEO.put("لعبة", "brave_video");
        CATEGORY_TO_VIDEO.put("صداقة", "friends_video");
        CATEGORY_TO_VIDEO.put("نوم", "sleep_story_video");
        CATEGORY_TO_VIDEO.put("مشاعر", "feelings_video");
    }

    private VideoView backgroundVideoView;
    private LinearLayout loadingLayout;
    private LinearLayout controlsLayout;
    private ScrollView storyScroll;
    private TextView storyTextView;
    private ImageButton backButton;
    private Button replayButton;
    private Button newStoryButton;

    private GeminiService geminiService;
    private TextToSpeech textToSpeech;
    private boolean ttsReady = false;

    private String category;
    private String currentStoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        bindViews();

        category = getIntent().getStringExtra(EXTRA_CATEGORY);
        if (category == null || category.isEmpty()) {
            Toast.makeText(this, "التصنيف غير محدد", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        geminiService = new GeminiService();

        setupBackgroundVideo();
        setupTextToSpeech();
        setupClickListeners();

        generateNewStory();
    }

    /**
     * لازم تضيفي هالـ layout الملف بمجلد res/layout باسم activity_story_playback.xml
     * وبعدها بدّلي هون الـ id الصح حسب مشروعك لو استخدمتي ViewBinding بدل findViewById.
     */
    private int getLayoutId() {
        return getResources().getIdentifier("activity_story_playback", "layout", getPackageName());
    }

    private void bindViews() {
        backgroundVideoView = findViewById(getResources().getIdentifier("backgroundVideoView", "id", getPackageName()));
        loadingLayout = findViewById(getResources().getIdentifier("loadingLayout", "id", getPackageName()));
        controlsLayout = findViewById(getResources().getIdentifier("controlsLayout", "id", getPackageName()));
        storyScroll = findViewById(getResources().getIdentifier("storyScroll", "id", getPackageName()));
        storyTextView = findViewById(getResources().getIdentifier("storyTextView", "id", getPackageName()));
        backButton = findViewById(getResources().getIdentifier("backButton", "id", getPackageName()));
        replayButton = findViewById(getResources().getIdentifier("replayButton", "id", getPackageName()));
        newStoryButton = findViewById(getResources().getIdentifier("newStoryButton", "id", getPackageName()));
    }

    private void setupBackgroundVideo() {
        String videoResName = CATEGORY_TO_VIDEO.get(category);
        if (videoResName == null) {
            // احتياط: لو تصنيف مش موجود بالخريطة، استخدمي أي فيديو افتراضي
            videoResName = "friends_video";
        }

        int videoResId = getResources().getIdentifier(videoResName, "raw", getPackageName());
        if (videoResId == 0) {
            Toast.makeText(this, "ملف الخلفية غير موجود", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        backgroundVideoView.setVideoURI(uri);

        backgroundVideoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f); // صامت تماماً، الصوت الوحيد هو TTS
            backgroundVideoView.start();
        });

        // ما في MediaController لأنه المستخدم مش المفروض يتحكم بالخلفية يدوياً
    }

    private void setupTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("ar"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "اللغة العربية غير مدعومة بمحول النص لصوت بهذا الجهاز", Toast.LENGTH_LONG).show();
                    ttsReady = false;
                } else {
                    ttsReady = true;
                    textToSpeech.setSpeechRate(0.95f);

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            // ممكن نعرض حالة "عم تحكي" لو حبيتي
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            runOnUiThread(() -> controlsLayout.setVisibility(View.VISIBLE));
                        }

                        @Override
                        public void onError(String utteranceId) {
                            runOnUiThread(() -> controlsLayout.setVisibility(View.VISIBLE));
                        }
                    });

                    // لو القصة كانت خلصت التحميل وجاهزة من قبل ما TTS يخلص تجهيزه، احكيها فوراً
                    if (currentStoryText != null) {
                        speakStory(currentStoryText);
                    }
                }
            } else {
                Toast.makeText(this, "تعذر تشغيل محول النص لصوت", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        replayButton.setOnClickListener(v -> {
            if (currentStoryText != null) {
                speakStory(currentStoryText);
            }
        });

        newStoryButton.setOnClickListener(v -> generateNewStory());
    }

    private void generateNewStory() {
        showLoading();

        geminiService.generateStoryForCategory(category, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    currentStoryText = message;
                    showStory(message);

                    if (ttsReady) {
                        speakStory(message);
                    }
                    // لو TTS لسا ما جهز، رح تنحكى تلقائياً بمجرد ما يجهز (شوفي setupTextToSpeech)
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(StoryPlaybackActivity.this,
                            "تعذر توليد القصة، تأكدي من الاتصال بالإنترنت",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void speakStory(String text) {
        if (!ttsReady || textToSpeech == null) return;

        controlsLayout.setVisibility(View.GONE);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID + "_" + UUID.randomUUID());
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        storyScroll.setVisibility(View.GONE);
        controlsLayout.setVisibility(View.GONE);
    }

    private void showStory(String text) {
        loadingLayout.setVisibility(View.GONE);
        storyTextView.setText(text);
        storyScroll.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundVideoView != null && backgroundVideoView.isPlaying()) {
            backgroundVideoView.pause();
        }
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundVideoView != null) {
            backgroundVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundVideoView != null) {
            backgroundVideoView.stopPlayback();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
