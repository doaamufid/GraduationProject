package com.example.graduationproject.Kids;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.graduationproject.R;
import com.example.graduationproject.data.RecordingStorage;
import com.example.graduationproject.models.Recording;

import java.io.File;
import java.io.IOException;

/**
 * شاشة "استمع لنفسك وأنت تقولها!".
 * بتشغل الملف المؤقت اللي انسجل بـ RecordingActivity، وتعطي خيارين:
 * - "أعجبني، احفظه": ننقل الملف من الكاش لمكان دائم، نحفظ بيانات التسجيل،
 *   وبعدين نبعت الملف لـ Gemini يحلله ويعطينا فيدباك حقيقي قبل شاشة الاحتفال.
 * - "سجل مرة تانية": نحذف الملف المؤقت ونرجع لشاشة التسجيل.
 */
public class PlaybackActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_PATH = "extra_file_path";

    private MediaPlayer mediaPlayer;
    private ImageButton playButton;
    private boolean isPlaying = false;

    private String phrase;
    private String tempFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        phrase = getIntent().getStringExtra(WordOfWeekActivity.EXTRA_PHRASE);
        tempFilePath = getIntent().getStringExtra(EXTRA_FILE_PATH);

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(v -> togglePlayback());

        findViewById(R.id.saveButton).setOnClickListener(v -> saveRecordingAndCelebrate());
        findViewById(R.id.recordAgainButton).setOnClickListener(v -> recordAgain());
    }

    private void togglePlayback() {
        if (isPlaying) {
            stopPlayback();
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(tempFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            playButton.setImageResource(R.drawable.ic_pause);
            mediaPlayer.setOnCompletionListener(mp -> stopPlayback());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (IllegalStateException ignored) {
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
        playButton.setImageResource(R.drawable.ic_play);
    }

    private void saveRecordingAndCelebrate() {
        stopPlayback();

        // ننقل الملف من مجلد الكاش المؤقت لمجلد دائم جوا مساحة التطبيق
        File tempFile = new File(tempFilePath);
        File permanentFile = new File(getFilesDir(), "saved_" + System.currentTimeMillis() + ".m4a");
        boolean moved = tempFile.renameTo(permanentFile);
        String finalPath = moved ? permanentFile.getAbsolutePath() : tempFilePath;

        Recording recording = new Recording(phrase, finalPath, System.currentTimeMillis());
        new RecordingStorage(this).saveRecording(recording);

        // TODO: فعّلي مؤشر تحميل (progress bar) هون عشان الطفل يعرف إنه في انتظار الرد
        File audioFileForAnalysis = new File(finalPath);
        new GeminiService().analyzeRecording(audioFileForAnalysis, phrase, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String feedback) {
                goToCelebration(feedback);
            }

            @Override
            public void onError(String errorMessage) {
                // ما في نت أو صار خطأ - منعرض رسالة افتراضية بدل ما نعلّق الطفل بالانتظار
                goToCelebration(getString(R.string.default_celebration_feedback));
            }
        });
    }

    private void goToCelebration(String feedback) {
        runOnUiThread(() -> {
            Intent intent = new Intent(PlaybackActivity.this, CelebrationActivity.class);
            intent.putExtra(CelebrationActivity.EXTRA_FEEDBACK, feedback);
            startActivity(intent);
            finish();
        });
    }

    private void recordAgain() {
        stopPlayback();
        // نحذف الملف المؤقت اللي ما بدنا نحتفظ فيه
        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        }
        Intent intent = new Intent(PlaybackActivity.this, RecordingActivity.class);
        intent.putExtra(WordOfWeekActivity.EXTRA_PHRASE, phrase);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        stopPlayback();
        super.onDestroy();
    }
}