package com.example.graduationproject.Kids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityMoodCheckInBinding;

public class MoodCheckInActivity extends AppCompatActivity {

    // TODO: عدّلي هذا الاسم لو اسم المفتاح عندك تختلف
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

        // بدل setContentView(R.layout...)، منستخدم الـ binding
        binding = ActivityMoodCheckInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        childProfileStore = new ChildProfileStore(this);

        // نجيب رقم الطفل من الشاشة اللي قبلها
        currentChildId = getIntent().getLongExtra(EXTRA_CHILD_ID, -1);

        // ربط كل عناصر المزاج الستة (هلق عن طريق binding مباشرة، بدون findViewById)
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
        binding.cardVideos.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideosActivity.class);
            startActivity(intent);
        });
        // بطاقات التصنيفات (لسا placeholder، رح نربطها بواجهاتها لما نوصلها)
        binding.cardSafetyTeam.setOnClickListener(v -> {
            Intent intent = new Intent(this, DrawInstructionActivity.class);
            startActivity(intent);
        });
        binding.cardBreathe.setOnClickListener(v -> {
            Intent intent = new Intent(this, KidsAiChatActivity.class);
            startActivity(intent);
        });
        binding.cardComfort.setOnClickListener(v -> {
            Intent intent = new Intent(this, SoundsActivity.class);
            startActivity(intent);
        });
        binding.cardPlayBushes.setOnClickListener(v -> {
            Intent intent = new Intent(this, WordOfWeekActivity.class);
            startActivity(intent);
        });
    }

    private void onMoodSelected(View view) {
        TextView clicked = (TextView) view;

        // نشيل التحديد عن أي اختيار سابق
        if (selectedMoodView != null) {
            selectedMoodView.setSelected(false);
        }

        // نحدد الاختيار الجديد
        clicked.setSelected(true);
        selectedMoodView = clicked;
        selectedMoodValue = String.valueOf(clicked.getTag());

        // نفعّل زر التأكيد
        binding.btnConfirmMood.setEnabled(true);
    }

    private void onConfirmClicked() {
        if (selectedMoodValue == null) {
            Toast.makeText(this, "اختاري مزاجك الأول 💛", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentChildId == -1) {
            Toast.makeText(this, "لم يتم تحديد الطفل", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnConfirmMood.setEnabled(false); // منع دوس مزدوج

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
        binding = null; // تجنب تسريب الذاكرة
    }
}