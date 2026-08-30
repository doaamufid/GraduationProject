package com.example.graduationproject.Kids;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ActiveChildManager;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.data.RecordingStorage;
import com.example.graduationproject.models.ChildProfile;
import com.example.graduationproject.models.Recording;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

        // 🌟 دالة الأفاتار الخاصة بكِ
        loadChildAvatar();
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

        File tempFile = new File(tempFilePath);
        File permanentFile = new File(getFilesDir(), "saved_" + System.currentTimeMillis() + ".m4a");
        boolean moved = tempFile.renameTo(permanentFile);
        String finalPath = moved ? permanentFile.getAbsolutePath() : tempFilePath;

        Recording recording = new Recording(phrase, finalPath, System.currentTimeMillis());
        new RecordingStorage(this).saveRecording(recording);

        // 🌟 نقاط الشجرة والنجوم للطفل
        long childId = getIntent().getLongExtra("CHILD_ID", 1L);
        TreeProgressManager progressManager = new TreeProgressManager(this, childId);
        progressManager.addPoints(10);

        long currentChildId = ActiveChildManager.getActiveChildId(this);
        if (currentChildId != ActiveChildManager.NO_ACTIVE_CHILD) {
            new ChildProfileStore(this).addStar(currentChildId);
        }

        // تحليل الصوت بواسطة Gemini ثم الانتقال للاحتفال
        File audioFileForAnalysis = new File(finalPath);
        new GeminiService().analyzeRecording(audioFileForAnalysis, phrase, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String feedback) {
                goToCelebration(feedback, childId);
            }

            @Override
            public void onError(String errorMessage) {
                goToCelebration(getString(R.string.default_audio_label), childId);
            }
        });
    }

    private void goToCelebration(String feedback, long childId) {
        runOnUiThread(() -> {
            Intent intent = new Intent(PlaybackActivity.this, CelebrationActivity.class);
            intent.putExtra(CelebrationActivity.EXTRA_FEEDBACK, feedback);
            intent.putExtra("CHILD_ID", childId);
            startActivity(intent);
            finish();
        });
    }

    private void recordAgain() {
        stopPlayback();
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

    // 🌟 دالة تحميل الأفاتار
    private void loadChildAvatar() {
        long childId = getIntent().getLongExtra("CHILD_ID", 1L);
        ChildProfileStore store = new ChildProfileStore(this);
        try {
            List<ChildProfile> profiles = store.getProfiles();
            for (ChildProfile profile : profiles) {
                if (profile.getId() == childId) {
                    String avatar = profile.getAvatar();
                    TextView tvAvatar = findViewById(R.id.tvMascotAvatar);
                    if (tvAvatar != null && avatar != null && !avatar.trim().isEmpty()) {
                        tvAvatar.setText(avatar);
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        } finally {
            store.close();
        }
    }
}