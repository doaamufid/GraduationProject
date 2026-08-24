package com.example.graduationproject.Kids;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
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
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityKidsAiChatBinding;
import com.example.graduationproject.databinding.LayoutVoiceRecordingBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

public class KidsAiChatActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private ActivityKidsAiChatBinding binding;
    private LayoutVoiceRecordingBottomSheetBinding sheetBinding;
    private GeminiService geminiService;
    private SpeechHelper speechHelper;
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;

    private BottomSheetDialog recordingBottomSheet;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int secondsRecorded = 0;

    private String lastRecognizedText = "";
    private String selectedMoodText = "";

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Services
        geminiService = new GeminiService();
        initTextToSpeech();
        initSpeechHelper();

        setupListeners();
        startEntranceAnimations();
        binding.btnActionTree.setOnClickListener(v -> {
            // الكود الخاص بالانتقال لشاشة الشجرة
            Intent intent = new Intent(KidsAiChatActivity.this, KidsTreeIntroActivity.class); // استبدلي TreeActivity باسم شاشتك
            startActivity(intent);
        });
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("ar"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "اللغة العربية غير مدعومة بالكامل على هذا المحاكي");
                }
                isTtsReady = true;
            }
        });
    }

    private void speakText(String text) {
        if (textToSpeech != null && !text.isEmpty()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NourSpeechID");
        }
    }

    private void initSpeechHelper() {
        speechHelper = new SpeechHelper(this, new SpeechHelper.SpeechResultCallback() {
            @Override
            public void onSpeechConverted(String text) {
                lastRecognizedText = text;
            }

            @Override
            public void onError(String errorMsg) {
                if (recordingBottomSheet != null && recordingBottomSheet.isShowing()) {
                    runOnUiThread(() -> Toast.makeText(KidsAiChatActivity.this, "لم أستطع سماعك جيداً، حاول ثانية", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void startEntranceAnimations() {
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

        binding.btnTalkNour.setOnClickListener(v -> {
            String promptToProcess = "";
            if (binding.layoutTextInput.getVisibility() == View.VISIBLE) {
                promptToProcess = binding.edtChildMessage.getText().toString().trim();
            } else if (!lastRecognizedText.isEmpty()) {
                promptToProcess = lastRecognizedText;
            } else if (!selectedMoodText.isEmpty()) {
                promptToProcess = selectedMoodText;
            }

            if (promptToProcess.isEmpty()) {
                Toast.makeText(this, "اختر شعوراً أو قل شيئاً لدبدوب نور 🐻", Toast.LENGTH_SHORT).show();
                return;
            }
            processAiRequest(promptToProcess);
        });

        binding.btnListenVoice.setOnClickListener(v -> {
            String response = binding.tvAiResponseText.getText().toString();
            speakText(response);
        });

        binding.btnActionBreath.setOnClickListener(v -> {
            if (textToSpeech != null && textToSpeech.isSpeaking()) {
                textToSpeech.stop();
            }
            Intent intent = new Intent(KidsAiChatActivity.this, KidsBubbleBreathingActivity.class);
            startActivity(intent);
        });

        binding.btnActionBetter.setOnClickListener(v -> navigateToTreeScreen());

//        binding.btnGoToTree.setOnClickListener(v -> navigateToTreeScreen());

        for (int i = 0; i < binding.gridMoods.getChildCount(); i++) {
            View child = binding.gridMoods.getChildAt(i);
            child.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                        .withEndAction(() -> v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()).start();

                if (child instanceof android.view.ViewGroup) {
                    android.view.ViewGroup group = (android.view.ViewGroup) child;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        View subView = group.getChildAt(j);
                        if (subView instanceof TextView && !((TextView) subView).getText().toString().matches(".*[\\u2000-\\u3300_\\u2600-\\u26FF_\\u2700-\\u27BF].*")) {
                            selectedMoodText = ((TextView) subView).getText().toString();
                            break;
                        }
                    }
                }
                processAiRequest(selectedMoodText);
            });
        }
    }

    private void navigateToTreeScreen() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        Intent intent = new Intent(KidsAiChatActivity.this, KidsTreeIntroActivity.class);
        startActivity(intent);
        finish();
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
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }

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
        secondsRecorded = 0;
        lastRecognizedText = "";
        if (sheetBinding != null) sheetBinding.tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", 0, 0));
        startTimer();
        if (speechHelper != null) {
            speechHelper.startListening();
        }
    }

    private void startTimer() {
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopAudioRecording(boolean processWithAi) {
        timerHandler.removeCallbacks(timerRunnable);
        if (speechHelper != null) {
            speechHelper.stopListening();
        }

        if (processWithAi) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!lastRecognizedText.isEmpty()) {
                    processAiRequest(lastRecognizedText);
                } else if (recordingBottomSheet != null && recordingBottomSheet.isShowing()) {
                    Toast.makeText(KidsAiChatActivity.this, "لم أستطع سماع صوتك بوضوح 🐻", Toast.LENGTH_SHORT).show();
                }
            }, 500);
        }
    }

    public void processAiRequest(String inputQuery) {
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

                geminiService.generateMoodMessage(inputQuery, new GeminiService.GeminiCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> {
                            binding.groupLoadingState.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                                binding.groupLoadingState.setVisibility(View.GONE);
                                showAiResponse(message);
                            }).start();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            binding.groupLoadingState.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                                binding.groupLoadingState.setVisibility(View.GONE);
                                showAiResponse("حدث خطأ في الخدمة: " + errorMessage);
                            }).start();
                        });
                    }
                });
            }
        });
    }

    private void showAiResponse(String aiMessage) {
        binding.groupResponseState.setVisibility(View.VISIBLE);

        binding.groupResponseState.setTranslationX(0f);
        binding.groupResponseState.setAlpha(0f);
        binding.groupResponseState.setTranslationY(40f);

        binding.groupResponseState.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .start();

        binding.tvAiResponseText.setText(aiMessage);

        speakText(aiMessage);

        onChildSentMessage();
    }

    private void onChildSentMessage() {
        long childId = getIntent().getLongExtra("CHILD_ID", -1L);
        if (childId == -1L) {
            childId = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
        }
        String childName = getIntent().getStringExtra("CHILD_NAME");

        ChildProfileStore store = new ChildProfileStore(this);

        // التثبت وتسجيل الحدث بشكل صحيح
        if (!store.hasCompletedEventToday(childId, "CHAT_SESSION")) {
            store.addCompletedEvent(childId, "CHAT_SESSION");

            TreeProgressManager progressManager = new TreeProgressManager(this, childName);
            progressManager.addPoints(10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showRecordingBottomSheet();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        if (speechHelper != null) {
            speechHelper.stopListening();
        }
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