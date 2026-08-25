package com.example.graduationproject.Kids;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.R;
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.databinding.ActivityKidsBubbleBreathingBinding;

public class KidsBubbleBreathingActivity extends AppCompatActivity {
    private static final int TARGET_BUBBLES = 5;
    private static final long BREATH_TICK_MS = 100L;
    private static final float BREATH_PROGRESS_STEP = 0.08f;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ActivityKidsBubbleBreathingBinding binding;
    private int completedBubbles;
    private float breathProgress;
    private boolean isHolding;

    private final Runnable breathRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isHolding) {
                return;
            }

            breathProgress = Math.min(1f, breathProgress + BREATH_PROGRESS_STEP);
            binding.bubbleView.setProgress(breathProgress);

            if (breathProgress >= 1f) {
                finishOneBubble();
            } else {
                handler.postDelayed(this, BREATH_TICK_MS);
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        binding = ActivityKidsBubbleBreathingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnGoTree.setOnClickListener(v -> {
            Intent intent = new Intent(KidsBubbleBreathingActivity.this, KidsTreeActivity.class);
            intent.putExtra("CHILD_ID", getCurrentChildId());
            startActivity(intent);
            finish();
        });

        binding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(KidsBubbleBreathingActivity.this, KidsTreeActivity.class);
            intent.putExtra("CHILD_ID", getCurrentChildId());
            startActivity(intent);
            finish();
        });

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnQuietExit.setOnClickListener(v -> showDoneState());

        binding.btnBubblesAgain.setOnClickListener(v -> resetExercise());

        binding.btnPrimary.setOnTouchListener((v, event) -> {
            if (!binding.btnPrimary.isSelected()) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showReadyState();
                }
                return true;
            }
            return handlePrimaryTouch(event);
        });

        showWelcomeState();
    }

    private boolean handlePrimaryTouch(MotionEvent event) {
        if (completedBubbles >= TARGET_BUBBLES) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startHoldingBreath();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            stopHoldingBreath();
            return true;
        }

        return false;
    }

    private void showWelcomeState() {
        completedBubbles = 0;
        breathProgress = 0f;
        isHolding = false;
        binding.bubbleView.showMode(KidsBubbleView.Mode.WELCOME);
        binding.btnQuietExit.setVisibility(View.GONE);
        binding.tvStars.setVisibility(View.GONE);
        binding.tvBreathHint.setVisibility(View.GONE);
        binding.tvInstructionTitle.setVisibility(View.VISIBLE);
        binding.tvInstructionBody.setVisibility(View.VISIBLE);
        binding.actionsRow.setVisibility(View.GONE);
        binding.btnPrimary.setVisibility(View.VISIBLE);
        binding.btnPrimary.setSelected(false);
        binding.btnPrimary.setText(R.string.bubble_btn_start);
        binding.tvInstructionTitle.setText(R.string.bubble_welcome_title);
        binding.tvInstructionBody.setText(R.string.bubble_welcome_body);
    }

    private void showReadyState() {
        binding.btnPrimary.setSelected(true);
        binding.bubbleView.showMode(KidsBubbleView.Mode.READY);
        binding.btnQuietExit.setVisibility(View.VISIBLE);
        binding.tvStars.setVisibility(View.VISIBLE);
        binding.tvBreathHint.setVisibility(View.VISIBLE);
        binding.tvInstructionTitle.setVisibility(View.GONE);
        binding.tvInstructionBody.setVisibility(View.GONE);
        binding.btnPrimary.setText(R.string.bubble_btn_hold);
        updateStars();
    }

    private void startHoldingBreath() {
        isHolding = true;
        breathProgress = 0f;
        binding.bubbleView.setProgress(breathProgress);
        binding.bubbleView.showMode(KidsBubbleView.Mode.INFLATING);
        binding.btnPrimary.setText(R.string.bubble_btn_holding);
        handler.removeCallbacks(breathRunnable);
        handler.post(breathRunnable);
    }

    private void stopHoldingBreath() {
        if (!isHolding) {
            return;
        }

        isHolding = false;
        handler.removeCallbacks(breathRunnable);
        if (breathProgress < 1f) {
            binding.bubbleView.showMode(KidsBubbleView.Mode.READY);
            binding.btnPrimary.setText(R.string.bubble_btn_hold);
        }
    }

    private void finishOneBubble() {
        isHolding = false;
        handler.removeCallbacks(breathRunnable);
        completedBubbles++;
        updateStars();

        if (completedBubbles >= TARGET_BUBBLES) {
            showDoneState();
        } else {
            breathProgress = 0f;
            binding.bubbleView.setProgress(0f);
            binding.bubbleView.showMode(KidsBubbleView.Mode.READY);
            binding.btnPrimary.setText(R.string.bubble_btn_hold);
        }
    }

    // المكان الصحيح لحفظ الإنجاز والنقاط
    private void showDoneState() {
        isHolding = false;
        handler.removeCallbacks(breathRunnable);
        binding.bubbleView.showMode(KidsBubbleView.Mode.DONE);

        binding.btnQuietExit.setVisibility(View.GONE);
        binding.tvStars.setVisibility(View.GONE);
        binding.tvBreathHint.setVisibility(View.GONE);

        binding.tvInstructionTitle.setVisibility(View.VISIBLE);
        binding.tvInstructionBody.setVisibility(View.VISIBLE);

        binding.tvInstructionTitle.setText(getString(R.string.bubble_done_title, completedBubbles));
        binding.tvInstructionBody.setText(R.string.bubble_done_body);

        binding.btnPrimary.setVisibility(View.GONE);
        binding.actionsRow.setVisibility(View.VISIBLE);

        // --- حفظ الإنجاز وإضافة النقاط رسمياً ---
        saveBreathingAchievement();
    }

    private void saveBreathingAchievement() {
        long currentChildId = getCurrentChildId();

        ChildProfileStore store = new ChildProfileStore(this);
        // 1. تسجيل الحدث بالـ Long الموحد
        store.addCompletedEvent(currentChildId, "BREATHING_EXERCISE");

        // 2. استخدام نفس المفتاح بالضبط للشجرة
        TreeProgressManager progressManager = new TreeProgressManager(this, String.valueOf(currentChildId));
        progressManager.addPoints(15);
    }

    private long getCurrentChildId() {
        // 1. القراءة من الـ Intent إذا كان موجوداً
        long id = getIntent().getLongExtra("CHILD_ID", -1L);

        // 2. إذا لم يوجد في الـ Intent، نفحص الملفين الاحتياطيين المشهورين بالتطبيق
        if (id == -1L) {
            id = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
        }
        if (id == -1L) {
            id = getSharedPreferences("KidsAppPrefs", MODE_PRIVATE).getLong("active_child_id", 1L);
        }
        return (id == -1L) ? 1L : id; // إذا كان فارغاً تماماً يعتمد 1 كمعرف افتراضي
    }

    private void resetExercise() {
        showWelcomeState();
    }

    private void updateStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < TARGET_BUBBLES; i++) {
            stars.append(i < completedBubbles ? "★" : "☆");
            if (i < TARGET_BUBBLES - 1) {
                stars.append(' ');
            }
        }
        binding.tvStars.setText(stars.toString());
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}