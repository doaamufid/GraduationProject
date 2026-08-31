package com.example.graduationproject.Kids;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityMoodCheckInBinding;
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class MoodCheckInActivity extends AppCompatActivity {

    public static final String EXTRA_CHILD_ID = "CHILD_ID";

    private ActivityMoodCheckInBinding binding;

    private ChildProfileStore childProfileStore;
    private long currentChildId;

    private TextView[] moodViews;
    private TextView selectedMoodView;
    private String selectedMoodValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make task bar and status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        binding = ActivityMoodCheckInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        childProfileStore = new ChildProfileStore(this);

        // جلب معرف الطفل مع القيمة الافتراضية الصريحة لمنع حدوث Crash
        currentChildId = getChildId();

        // 🌟 تحميل الأفاتار النصي الخاِص بكِ وتحديث الواجهة والفقاعة
        loadChildAvatar();

        // ربط عناصر المزاج
        moodViews = new TextView[]{
                binding.moodHappy,
                binding.moodSad,
                binding.moodScared,
                binding.moodAngry,
                binding.moodTired,
                binding.moodUpset
        };

        View.OnClickListener moodClickListener = this::onMoodSelected;
        for (TextView moodView : moodViews) {
            moodView.setOnClickListener(moodClickListener);
        }

        binding.btnConfirmMood.setOnClickListener(v -> onConfirmClicked());
        binding.btnBack.setOnClickListener(v -> finish());

        // التنقل بين الشاشات مع إرسال ID الطفل (من كود صديقتك)
        binding.cardVideos.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideosActivity.class);
            intent.putExtra(EXTRA_CHILD_ID, currentChildId);
            startActivity(intent);
        });

        binding.cardSafetyTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, DrawInstructionActivity.class);
            intent.putExtra(EXTRA_CHILD_ID, currentChildId);
            startActivity(intent);
        });

        binding.cardBreathe.setOnClickListener(v -> {
            Intent intent = new Intent(this, KidsAiChatActivity.class);
            intent.putExtra(EXTRA_CHILD_ID, currentChildId);
            startActivity(intent);
        });

        binding.cardComfort.setOnClickListener(v -> {
            Intent intent = new Intent(this, SoundsActivity.class);
            intent.putExtra(EXTRA_CHILD_ID, currentChildId);
            startActivity(intent);
        });

        binding.cardPlayBushes.setOnClickListener(v -> {
            Intent intent = new Intent(this, WordOfWeekActivity.class);
            intent.putExtra(EXTRA_CHILD_ID, currentChildId);
            startActivity(intent);
        });

        binding.cardDailyRoutine.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.graduationproject.KidsRoutineMainActivity.class);
            intent.putExtra(EXTRA_CHILD_ID, currentChildId);
            startActivity(intent);
        });

        binding.cardCalmCorner.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.graduationproject.KidsCalmCornerActivity.class);
            intent.putExtra(EXTRA_CHILD_ID, currentChildId);
            startActivity(intent);
        });

        // جدولة التذكيرات والإشعارات
        KidsReminderScheduler.scheduleReminder(this);
        requestNotificationPermissionIfNeeded();
    }

    // 🌟 دالة قراءة الأفاتار وتحديث الـ TextView الخاص بكِ (tvChildAvatar)
    private void loadChildAvatar() {
        if (childProfileStore == null || currentChildId == -1L) return;

        try {
            List<ChildProfile> profiles = childProfileStore.getProfiles();
            for (ChildProfile profile : profiles) {
                if (profile.getId() == currentChildId) {
                    String avatar = profile.getAvatar();
                    if (avatar != null && !avatar.trim().isEmpty()) {
                        // تحديث عنصر الأفاتار الخاص بكِ
                        binding.tvChildAvatar.setText(avatar);
                        // تحديث نص الترحيب بنفس الأفاتار
                        binding.txtGreeting.setText("أنا " + avatar + " معك دائماً 💛");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("MoodCheckIn", "Error loading child avatar: " + e.getMessage());
        }
    }

    private long getChildId() {
        long id = getIntent().getLongExtra(EXTRA_CHILD_ID, -1L);
        return (id == -1L) ? 1L : id;
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
            }
        }
    }

    private void onMoodSelected(View view) {
        TextView clicked = (TextView) view;

        if (selectedMoodView != null) {
            selectedMoodView.setSelected(false);
        }

        clicked.setSelected(true);
        selectedMoodView = clicked;
        selectedMoodValue = String.valueOf(clicked.getTag());

        binding.btnConfirmMood.setEnabled(true);
    }

    private void onConfirmClicked() {
        if (selectedMoodValue == null) {
            Toast.makeText(this, "اختاري مزاجك الأول 💛", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnConfirmMood.setEnabled(false);

        childProfileStore.addBehaviorEvent(
                currentChildId,
                "mood",
                selectedMoodValue,
                null,
                System.currentTimeMillis()
        );

        GeminiService geminiService = new GeminiService();
        geminiService.generateMoodMessage(selectedMoodValue, new GeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    childProfileStore.addBotMessage(
                            currentChildId,
                            message,
                            selectedMoodValue,
                            System.currentTimeMillis()
                    );

                    Intent intent = new Intent(MoodCheckInActivity.this, MessagesActivity.class);
                    intent.putExtra(MessagesActivity.EXTRA_CHILD_ID, currentChildId);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    binding.btnConfirmMood.setEnabled(true);
                    Toast.makeText(MoodCheckInActivity.this,
                            "حدث خطأ: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (childProfileStore != null) {
            childProfileStore.close();
        }
        binding = null;
    }
}