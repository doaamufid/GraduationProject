package com.example.graduationproject.Kids;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.LayoutVoiceRecordingBottomSheetBinding;
import com.example.graduationproject.models.ChildProfile;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KidsAiCompanionActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private ImageButton btnSend, btnMic, btnBack;
    private Button chipStory, chipRiddle, chipSad;
    private TextView tvAvatarEmoji, tvCharacterName;
    private ChildProfileStore dbStore;
    private long currentChildId = -1L;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private GeminiService geminiService;
    private SpeechHelper speechHelper;

    private LayoutVoiceRecordingBottomSheetBinding sheetBinding;
    private BottomSheetDialog recordingBottomSheet;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private int secondsRecorded = 0;
    private String lastRecognizedText = "";

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

        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kids_ai_companion);

        android.view.Window window = getWindow();
        window.setFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        androidx.core.view.WindowInsetsControllerCompat controller = androidx.core.view.WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        currentChildId = getChildId();

        initViews();
        setupChatRecyclerView();

        geminiService = new GeminiService();
        setupSpeechRecognizer();
        dbStore = new ChildProfileStore(this);

        // 🌟 تعيين اسم وشخصية/أفاتار الطفل مباشرة من قاعدة البيانات
        loadChildProfileFromDatabase();

        // 1. استرجاع وتعبئة كل الرسائل المحفوظة في SQLite سابقاً
        List<ChatMessage> savedMessages = dbStore.getChatHistory(currentChildId);
        if (!savedMessages.isEmpty()) {
            messageList.addAll(savedMessages);
            chatAdapter.notifyDataSetChanged();
        } else {
            addMessageToChat("أهلاً بك يا بطل! أنا صديقك دبدوب نور 🐻، عن ماذا تحب أن نتحدث اليوم؟", false);
        }

        // استقبال الرسالة الممررة من الشاشة السابقة إن وجدت
        String initialMessage = getIntent().getStringExtra("INITIAL_MESSAGE");
        if (initialMessage != null && !initialMessage.isEmpty()) {
            addMessageToChat(initialMessage, false);
        }

        btnSend.setOnClickListener(v -> sendMessage());
        btnMic.setOnClickListener(v -> checkPermissionAndShowRecordingSheet());
        btnBack.setOnClickListener(v -> finish());

        // أزرار الاقتراحات السريعة
        chipStory.setOnClickListener(v -> sendQuickMessage("احكيلي قصة قصيرة وممتعة 📖"));
        chipRiddle.setOnClickListener(v -> sendQuickMessage("إعطيني حزورة ذكية للأطفال 🧩"));
        chipSad.setOnClickListener(v -> sendQuickMessage("أنا أسبوعي كان صعباً وزعلان 😢"));
    }

    private void initViews() {
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnMic = findViewById(R.id.btnMic);
        btnBack = findViewById(R.id.btnBack);
        chipStory = findViewById(R.id.chipStory);
        chipRiddle = findViewById(R.id.chipRiddle);
        chipSad = findViewById(R.id.chipSad);
        tvAvatarEmoji = findViewById(R.id.tvAvatarEmoji);
        tvCharacterName = findViewById(R.id.tvCharacterName);
    }

    /**
     * جلب اسم الطفل والأفاتار الخاص به من قاعدة البيانات المشفرة وتحديث الترويسة
     */
    private void loadChildProfileFromDatabase() {
        if (currentChildId != -1L) {
            try {
                List<ChildProfile> profiles = dbStore.getProfiles();
                for (ChildProfile profile : profiles) {
                    if (profile.getId() == currentChildId) {
                        if (profile.getName() != null && !profile.getName().trim().isEmpty()) {
                            tvCharacterName.setText(profile.getName());
                        }
                        if (profile.getAvatar() != null && !profile.getAvatar().trim().isEmpty()) {
                            tvAvatarEmoji.setText(profile.getAvatar());
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("KidsAiCompanion", "Error loading profile from database: " + e.getMessage());
            }
        }
    }

    private long getChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);
        if (id == -1L) {
            id = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
        }
        if (id == -1L) {
            id = getSharedPreferences("KidsAppPrefs", MODE_PRIVATE).getLong("active_child_id", -1L);
        }
        return id != -1L ? id : 1L;
    }

    private void setupChatRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);
    }

    private void setupSpeechRecognizer() {
        speechHelper = new SpeechHelper(this, new SpeechHelper.SpeechResultCallback() {
            @Override
            public void onSpeechConverted(String text) {
                lastRecognizedText = text;
            }

            @Override
            public void onError(String errorMsg) {
                if (recordingBottomSheet != null && recordingBottomSheet.isShowing()) {
                    runOnUiThread(() -> Toast.makeText(KidsAiCompanionActivity.this, "لم أستطع سماعك جيداً، حاول ثانية 🐻", Toast.LENGTH_SHORT).show());
                }
            }
        });
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

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(sheetBinding.btnFinishRecording, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(sheetBinding.btnFinishRecording, "scaleY", 1f, 1.2f, 1f);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setDuration(1000);
        scaleY.setDuration(1000);

        AnimatorSet pulseAnim = new AnimatorSet();
        pulseAnim.playTogether(scaleX, scaleY);
        pulseAnim.start();

        startAudioRecording();

        sheetBinding.tvCancel.setOnClickListener(v -> {
            pulseAnim.cancel();
            stopAudioRecording(false);
            recordingBottomSheet.dismiss();
        });

        sheetBinding.btnFinishRecording.setOnClickListener(v -> {
            pulseAnim.cancel();
            stopAudioRecording(true);
            recordingBottomSheet.dismiss();
        });

        recordingBottomSheet.setOnDismissListener(dialog -> {
            pulseAnim.cancel();
            stopAudioRecording(false);
        });

        recordingBottomSheet.show();
    }

    private void startAudioRecording() {
        secondsRecorded = 0;
        lastRecognizedText = "";
        if (sheetBinding != null) sheetBinding.tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", 0, 0));
        timerHandler.postDelayed(timerRunnable, 1000);
        if (speechHelper != null) {
            speechHelper.startListening();
        }
    }

    private void stopAudioRecording(boolean sendToChat) {
        timerHandler.removeCallbacks(timerRunnable);
        if (speechHelper != null) {
            speechHelper.stopListening();
        }

        if (sendToChat) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!lastRecognizedText.isEmpty()) {
                    etMessage.setText(lastRecognizedText);
                    sendMessage();
                } else {
                    Toast.makeText(KidsAiCompanionActivity.this, "لم أستطع سماع صوتك بوضوح 🐻", Toast.LENGTH_SHORT).show();
                }
            }, 500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showRecordingBottomSheet();
            } else {
                Toast.makeText(this, "يلزم السماح بالمايكروفون للتحدث مع صديقك", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendQuickMessage(String text) {
        etMessage.setText(text);
        sendMessage();
    }

    private void sendMessage() {
        if (!dbStore.hasCompletedEventToday(currentChildId, "CHAT_SESSION")) {
            dbStore.addCompletedEvent(currentChildId, "CHAT_SESSION");

            TreeProgressManager progressManager = new TreeProgressManager(KidsAiCompanionActivity.this, currentChildId);
            progressManager.addPoints(15);
        }
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        addMessageToChat(text, true);
        etMessage.setText("");

        String loadingMessage = "دبدوب نور يفكر في الرد... 💭🐻";
        addMessageToChat(loadingMessage, false);
        int loadingPosition = messageList.size() - 1;

        geminiService.sendChatHistory(messageList, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    if (loadingPosition < messageList.size()) {
                        messageList.set(loadingPosition, new ChatMessage(message, false));
                        chatAdapter.notifyItemChanged(loadingPosition);

                        dbStore.addChatMessage(currentChildId, message, false);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    if (loadingPosition < messageList.size()) {
                        String fallback = "أنا هنا معك يا صديقي، حدث خطأ بسيط بالاتصال! 🐻";
                        messageList.set(loadingPosition, new ChatMessage(fallback, false));
                        chatAdapter.notifyItemChanged(loadingPosition);
                    }
                });
            }
        });
    }

    private void addMessageToChat(String text, boolean isUser) {
        messageList.add(new ChatMessage(text, isUser));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.smoothScrollToPosition(messageList.size() - 1);

        if (isUser) {
            dbStore.addChatMessage(currentChildId, text, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speechHelper != null) {
            speechHelper.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbStore != null) {
            dbStore.close();
        }
    }
}