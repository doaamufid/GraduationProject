package com.example.graduationproject.Kids;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.graduationproject.databinding.ActivityKidsAiChatBinding;
import com.example.graduationproject.databinding.LayoutVoiceRecordingBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class KidsAiChatActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private ActivityKidsAiChatBinding binding;
    private LayoutVoiceRecordingBottomSheetBinding sheetBinding;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private BottomSheetDialog recordingBottomSheet;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int secondsRecorded = 0;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            secondsRecorded++;
            int minutes = secondsRecorded / 60;
            int secs = secondsRecorded % 60;
            if (sheetBinding != null) {
                Locale arLocale = new Locale("ar");
                sheetBinding.tvTimer.setText(String.format(arLocale, "%d:%02d", minutes, secs));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKidsAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        binding.btnSwitchToText.setPaintFlags(binding.btnSwitchToText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.btnSwitchToVoice.setPaintFlags(binding.btnSwitchToVoice.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.btnSwitchToText.setOnClickListener(v -> {
            binding.layoutVoiceInput.setVisibility(View.GONE);
            binding.layoutTextInput.setVisibility(View.VISIBLE);
        });

        binding.btnSwitchToVoice.setOnClickListener(v -> {
            binding.layoutTextInput.setVisibility(View.GONE);
            binding.layoutVoiceInput.setVisibility(View.VISIBLE);
        });

        binding.btnBack.setOnClickListener(v -> handleBackNavigation());

        binding.btnRecordMic.setOnClickListener(v -> checkPermissionAndShowRecordingSheet());

        binding.btnTalkNour.setOnClickListener(v -> processAiRequest());

        binding.btnActionBreath.setOnClickListener(v -> {
            Intent intent = new Intent(KidsAiChatActivity.this, KidsBubbleBreathingActivity.class);
            startActivity(intent);
        });

        binding.btnActionBetter.setOnClickListener(v -> finish());
    }

    private void handleBackNavigation() {
        if (binding.groupResponseState.getVisibility() == View.VISIBLE) {
            binding.groupResponseState.setVisibility(View.GONE);
            binding.groupInputState.setVisibility(View.VISIBLE);
            binding.tvNourBadge.setVisibility(View.VISIBLE);
            binding.btnTalkNour.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackNavigation();
    }

    private void checkPermissionAndShowRecordingSheet() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            showRecordingBottomSheet();
        }
    }

    private void showRecordingBottomSheet() {
        recordingBottomSheet = new BottomSheetDialog(this);
        sheetBinding = LayoutVoiceRecordingBottomSheetBinding.inflate(getLayoutInflater());
        recordingBottomSheet.setContentView(sheetBinding.getRoot());

        startAudioRecording();

        sheetBinding.tvCancel.setOnClickListener(v -> {
            stopAudioRecording(false);
            recordingBottomSheet.dismiss();
        });

        sheetBinding.btnFinishRecording.setOnClickListener(v -> {
            stopAudioRecording(true);
            recordingBottomSheet.dismiss();
        });

        recordingBottomSheet.setOnDismissListener(dialog -> stopAudioRecording(false));
        recordingBottomSheet.show();
    }

    private void startAudioRecording() {
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/kid_speech.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            secondsRecorded = 0;
            startTimer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopAudioRecording(boolean processWithAi) {
        timerHandler.removeCallbacks(timerRunnable);
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (RuntimeException ignored) {
            }
            mediaRecorder.release();
            mediaRecorder = null;

            if (processWithAi) {
                File audioFile = new File(audioFilePath);
                sendAudioToAi(audioFile);
            }
        }
    }

    private void sendAudioToAi(File audioFile) {
        Toast.makeText(this, "جاري إرسال الصوت للتحليل بواسطة الـ AI...", Toast.LENGTH_SHORT).show();
        processAiRequest();
    }

    public void processAiRequest() {
        binding.groupInputState.setVisibility(View.GONE);
        binding.tvNourBadge.setVisibility(View.GONE);
        binding.btnTalkNour.setVisibility(View.GONE);

        binding.groupLoadingState.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.groupLoadingState.setVisibility(View.GONE);
            binding.groupResponseState.setVisibility(View.VISIBLE);
            binding.tvAiResponseText.setText("تعبك مسموع، خذ راحتك شوي.");
        }, 2000L);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showRecordingBottomSheet();
        }
    }
}