package com.example.graduationproject.Kids;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.R;
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
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                sheetBinding.tvTimer.setText(formattedTime);
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityKidsAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            binding.btnBack.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        setupListeners();
        startEntranceAnimations();
    }

    private void startEntranceAnimations() {
        // Initial State
        binding.tvBearAvatar.setScaleX(0f);
        binding.tvBearAvatar.setScaleY(0f);
        binding.tvNourBadge.setAlpha(0f);
        binding.tvNourBadge.setTranslationY(20f);
        binding.tvQuestionTitle.setAlpha(0f);
        binding.tvQuestionTitle.setTranslationY(30f);
        
        for (int i = 0; i < binding.gridMoods.getChildCount(); i++) {
            View child = binding.gridMoods.getChildAt(i);
            child.setAlpha(0f);
            child.setScaleX(0.8f);
            child.setScaleY(0.8f);
        }

        binding.layoutVoiceInput.setAlpha(0f);
        binding.layoutVoiceInput.setTranslationY(40f);
        binding.btnTalkNour.setAlpha(0f);
        binding.btnTalkNour.setTranslationY(80f);

        // Sequence
        AnimatorSet mascotPop = new AnimatorSet();
        mascotPop.playTogether(
                ObjectAnimator.ofFloat(binding.tvBearAvatar, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvBearAvatar, "scaleY", 0f, 1f)
        );
        mascotPop.setDuration(800);
        mascotPop.setInterpolator(new OvershootInterpolator());

        AnimatorSet badgeSet = new AnimatorSet();
        badgeSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvNourBadge, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvNourBadge, "translationY", 20f, 0f)
        );
        badgeSet.setDuration(600);
        badgeSet.setStartDelay(400);

        AnimatorSet questionSet = new AnimatorSet();
        questionSet.playTogether(
                ObjectAnimator.ofFloat(binding.tvQuestionTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvQuestionTitle, "translationY", 30f, 0f)
        );
        questionSet.setDuration(600);
        questionSet.setStartDelay(600);

        AnimatorSet gridSet = new AnimatorSet();
        for (int i = 0; i < binding.gridMoods.getChildCount(); i++) {
            View child = binding.gridMoods.getChildAt(i);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child, "alpha", 0f, 1f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(child, "scaleX", 0.8f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(child, "scaleY", 0.8f, 1f);
            alpha.setDuration(400);
            scaleX.setDuration(500);
            scaleY.setDuration(500);
            long delay = 800L + (i * 100L);
            alpha.setStartDelay(delay);
            scaleX.setStartDelay(delay);
            scaleY.setStartDelay(delay);
            gridSet.playTogether(alpha, scaleX, scaleY);
        }

        AnimatorSet footerSet = new AnimatorSet();
        footerSet.playTogether(
                ObjectAnimator.ofFloat(binding.layoutVoiceInput, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.layoutVoiceInput, "translationY", 40f, 0f),
                ObjectAnimator.ofFloat(binding.btnTalkNour, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.btnTalkNour, "translationY", 80f, 0f)
        );
        footerSet.setDuration(800);
        footerSet.setStartDelay(1400);
        footerSet.setInterpolator(new DecelerateInterpolator());

        mascotPop.start();
        badgeSet.start();
        questionSet.start();
        gridSet.start();
        footerSet.start();

        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(binding.tvBearAvatar, "translationY", -10f, 10f);
        floatAnim.setDuration(2000);
        floatAnim.setRepeatCount(ValueAnimator.INFINITE);
        floatAnim.setRepeatMode(ValueAnimator.REVERSE);
        floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnim.start();
    }

    private void setupListeners() {
        binding.btnSwitchToText.setPaintFlags(binding.btnSwitchToText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.btnSwitchToVoice.setPaintFlags(binding.btnSwitchToVoice.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.btnSwitchToText.setOnClickListener(v -> animateStateTransition(binding.layoutVoiceInput, binding.layoutTextInput));
        binding.btnSwitchToVoice.setOnClickListener(v -> animateStateTransition(binding.layoutTextInput, binding.layoutVoiceInput));

        binding.btnBack.setOnClickListener(v -> handleBackNavigation());
        binding.btnRecordMic.setOnClickListener(v -> checkPermissionAndShowRecordingSheet());
        binding.btnTalkNour.setOnClickListener(v -> processAiRequest());

        binding.btnActionBreath.setOnClickListener(v -> {
            Intent intent = new Intent(KidsAiChatActivity.this, KidsBubbleBreathingActivity.class);
            startActivity(intent);
        });

        binding.btnActionBetter.setOnClickListener(v -> finish());
        
        for (int i = 0; i < binding.gridMoods.getChildCount(); i++) {
            View child = binding.gridMoods.getChildAt(i);
            child.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()).start();
            });
        }
    }

    private void animateStateTransition(View outView, View inView) {
        outView.animate().alpha(0f).setDuration(200).withEndAction(() -> {
            outView.setVisibility(View.GONE);
            inView.setVisibility(View.VISIBLE);
            inView.setAlpha(0f);
            inView.setTranslationY(20f);
            inView.animate().alpha(1f).translationY(0f).setDuration(300).start();
        }).start();
    }

    private void handleBackNavigation() {
        if (binding.groupResponseState.getVisibility() == View.VISIBLE) {
            animateBackToInput();
        } else {
            finish();
        }
    }

    private void animateBackToInput() {
        binding.groupResponseState.animate().alpha(0f).translationX(200f).setDuration(300).withEndAction(() -> {
            binding.groupResponseState.setVisibility(View.GONE);
            binding.groupInputState.setVisibility(View.VISIBLE);
            binding.tvNourBadge.setVisibility(View.VISIBLE);
            binding.btnTalkNour.setVisibility(View.VISIBLE);
            
            binding.groupInputState.setAlpha(0f);
            binding.groupInputState.setTranslationX(-200f);
            binding.groupInputState.animate().alpha(1f).translationX(0f).setDuration(300).start();
            binding.tvNourBadge.setAlpha(1f);
            binding.btnTalkNour.setAlpha(1f);
        }).start();
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
        File cacheDir = getExternalCacheDir();
        if (cacheDir == null) cacheDir = getCacheDir();
        audioFilePath = cacheDir.getAbsolutePath() + "/kid_speech.3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            secondsRecorded = 0;
            if (sheetBinding != null) sheetBinding.tvTimer.setText("٠٠:٠٠");
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
            } catch (RuntimeException ignored) {}
            mediaRecorder.release();
            mediaRecorder = null;

            if (processWithAi && audioFilePath != null) {
                sendAudioToAi(new File(audioFilePath));
            }
        }
    }

    private void sendAudioToAi(File audioFile) {
        Toast.makeText(this, "جاري إرسال الصوت للتحليل بواسطة الـ AI...", Toast.LENGTH_SHORT).show();
        processAiRequest();
    }

    public void processAiRequest() {
        AnimatorSet fadeOut = new AnimatorSet();
        fadeOut.playTogether(
                ObjectAnimator.ofFloat(binding.groupInputState, "alpha", 0f),
                ObjectAnimator.ofFloat(binding.tvNourBadge, "alpha", 0f),
                ObjectAnimator.ofFloat(binding.btnTalkNour, "alpha", 0f)
        );
        fadeOut.setDuration(400);
        fadeOut.start();

        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                binding.groupInputState.setVisibility(View.GONE);
                binding.tvNourBadge.setVisibility(View.GONE);
                binding.btnTalkNour.setVisibility(View.GONE);

                binding.groupLoadingState.setVisibility(View.VISIBLE);
                binding.groupLoadingState.setAlpha(0f);
                binding.groupLoadingState.animate().alpha(1f).setDuration(300).start();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    binding.groupLoadingState.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                        binding.groupLoadingState.setVisibility(View.GONE);
                        showAiResponse();
                    }).start();
                }, 2000L);
            }
        });
    }

    private void showAiResponse() {
        binding.groupResponseState.setVisibility(View.VISIBLE);
        binding.groupResponseState.setAlpha(0f);
        binding.groupResponseState.setTranslationY(100f);

        binding.groupResponseState.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator())
                .start();

        binding.tvAiResponseText.setText("تعبك مسموع، خذ راحتك شوي.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showRecordingBottomSheet();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaRecorder != null) mediaRecorder.release();
        super.onDestroy();
    }
}