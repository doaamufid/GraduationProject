package com.example.graduationproject.Kids;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.graduationproject.R;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * شاشة التسجيل الفعلي بصوت الطفل، باستخدام MediaRecorder.
 * الملف بينحفظ مؤقتاً بمجلد الكاش، ولما يضغط "خلصت" بننقل مسار الملف
 * لشاشة الاستماع (PlaybackActivity) عشان يسمعه قبل ما يقرر يحفظه.
 */
public class RecordingActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO = 100;

    private MediaRecorder mediaRecorder;
    private com.example.kalamati.util.WaveformView waveformView;
    private TextView timerText;

    private String phrase;
    private String outputFilePath;
    private boolean isRecording = false;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int secondsElapsed = 0;
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            secondsElapsed++;
            timerText.setText(formatTime(secondsElapsed));
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        phrase = getIntent().getStringExtra(WordOfWeekActivity.EXTRA_PHRASE);
        if (phrase == null) {
            phrase = getString(R.string.default_phrase);
        }

        TextView phraseText = findViewById(R.id.phraseText);
        phraseText.setText(phrase);

        waveformView = findViewById(R.id.waveformView);
        timerText = findViewById(R.id.timerText);

        findViewById(R.id.stopButton).setOnClickListener(v -> {
            if (isRecording) {
                stopRecordingAndContinue();
            } else {
                finish();
            }
        });

        checkPermissionAndStartRecording();
    }

    private void checkPermissionAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        } else {
            startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startRecording() {
        File outputFile = new File(getCacheDir(), "temp_recording_" + System.currentTimeMillis() + ".m4a");
        outputFilePath = outputFile.getAbsolutePath();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(outputFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            waveformView.start();
            secondsElapsed = 0;
            timerHandler.post(timerRunnable);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "صار في مشكلة، جرب مرة تانية", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void stopRecordingAndContinue() {
        try {
            mediaRecorder.stop();
        } catch (RuntimeException ignored) {
            // ممكن تصير لو المستخدم ضغط إيقاف بسرعة جداً قبل ما يبدأ فعلياً
        }
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        waveformView.stop();
        timerHandler.removeCallbacks(timerRunnable);

        Intent intent = new Intent(RecordingActivity.this, PlaybackActivity.class);
        intent.putExtra(WordOfWeekActivity.EXTRA_PHRASE, phrase);
        intent.putExtra(PlaybackActivity.EXTRA_FILE_PATH, outputFilePath);
        startActivity(intent);
        finish();
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        timerHandler.removeCallbacks(timerRunnable);
        if (mediaRecorder != null) {
            try {
                mediaRecorder.release();
            } catch (Exception ignored) {
            }
            mediaRecorder = null;
        }
        super.onDestroy();
    }
}
