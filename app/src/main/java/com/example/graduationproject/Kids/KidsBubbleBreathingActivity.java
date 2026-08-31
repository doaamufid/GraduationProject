package com.example.graduationproject.Kids;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.graduationproject.models.ChildProfile;

import java.util.List;

public class KidsBubbleBreathingActivity extends AppCompatActivity {
    private static final int TARGET_BUBBLES = 5;
    private static final long BREATH_TICK_MS = 100L;
    private static final float BREATH_PROGRESS_STEP = 0.08f;
    long childId;
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

            // تكبير الأفاتار مع التنفس
            float scale = 1.0f + (breathProgress * 0.4f);
            binding.tvChildAvatar.setScaleX(scale);
            binding.tvChildAvatar.setScaleY(scale);

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

        android.view.Window window = getWindow();
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.parseColor("#FAF1E6"));
        window.setNavigationBarColor(android.graphics.Color.parseColor("#FAF1E6"));

        androidx.core.view.WindowInsetsControllerCompat controller = androidx.core.view.WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(true);
        }

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        binding = ActivityKidsBubbleBreathingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🌟 تحميل أفاتار الطفل المختار من قاعدة البيانات
        loadChildAvatar();

        binding.btnGoToTree.setOnClickListener(v -> {
            Intent intent = new Intent(KidsBubbleBreathingActivity.this, KidsTreeActivity.class);
            intent.putExtra("CHILD_ID", getChildId());
            startActivity(intent);
            finish();
        });

        binding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(KidsBubbleBreathingActivity.this, KidsTreeActivity.class);
            intent.putExtra("CHILD_ID", getChildId());
            startActivity(intent);
            finish();
        });

        binding.btnBack.setOnClickListener(v -> finish());


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

    private void loadChildAvatar() {
         childId = getChildId();
        if (childId != -1L) {
            ChildProfileStore store = new ChildProfileStore(this);
            try {
                List<ChildProfile> profiles = store.getProfiles();
                for (ChildProfile profile : profiles) {
                    if (profile.getId() == childId) {
                        if (profile.getAvatar() != null && !profile.getAvatar().trim().isEmpty()) {
                            binding.tvChildAvatar.setText(profile.getAvatar());
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("KidsBubbleBreathing", "Error loading avatar: " + e.getMessage());
            } finally {
                store.close();
            }
        }
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
        binding.tvChildAvatar.setScaleX(1.0f);
        binding.tvChildAvatar.setScaleY(1.0f);
        binding.bubbleView.showMode(KidsBubbleView.Mode.WELCOME);
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
        binding.tvChildAvatar.setScaleX(1.0f);
        binding.tvChildAvatar.setScaleY(1.0f);
        if (breathProgress < 1f) {
            binding.bubbleView.showMode(KidsBubbleView.Mode.READY);
            binding.btnPrimary.setText(R.string.bubble_btn_hold);
        }
    }

    private void finishOneBubble() {
        isHolding = false;
        handler.removeCallbacks(breathRunnable);
        binding.tvChildAvatar.setScaleX(1.0f);
        binding.tvChildAvatar.setScaleY(1.0f);
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

    private void showDoneState() {
        isHolding = false;
        handler.removeCallbacks(breathRunnable);
        binding.tvChildAvatar.setScaleX(1.0f);
        binding.tvChildAvatar.setScaleY(1.0f);
        binding.bubbleView.showMode(KidsBubbleView.Mode.DONE);

        binding.tvStars.setVisibility(View.GONE);
        binding.tvBreathHint.setVisibility(View.GONE);

        binding.tvInstructionTitle.setVisibility(View.VISIBLE);
        binding.tvInstructionBody.setVisibility(View.VISIBLE);

        binding.tvInstructionTitle.setText(getString(R.string.bubble_done_title, completedBubbles));
        binding.tvInstructionBody.setText(R.string.bubble_done_body);

        binding.btnPrimary.setVisibility(View.GONE);
        binding.actionsRow.setVisibility(View.VISIBLE);

        saveBreathingAchievement();
    }

    private void saveBreathingAchievement() {
        long currentChildId = getChildId();

        if (currentChildId == -1L) {
            Log.w("KidsBubbleBreathing", "Child ID is invalid (-1), skipping achievement saving");
            return;
        }
        if (currentChildId == -1L) return;
        ChildProfileStore store = ChildProfileStore.getInstance(this); // استخدام السينجلتون مباشرة
        store.addCompletedEvent(currentChildId, "BREATHING_EXERCISE");
        store.recordEvent(currentChildId, "CALM_CORNER");
        // 1. تسليط نقاط وإنجاز التمرين

        TreeProgressManager progressManager = new TreeProgressManager(this, currentChildId);
        progressManager.addPoints(15);
        // 2. إضافة نجمة وتحديث تفضيلات الطفل
        store.addStar(currentChildId);
        android.content.SharedPreferences prefs = getSharedPreferences("child_stats_" + currentChildId, MODE_PRIVATE);
        int currentCount = prefs.getInt("completed_exercises", 0);
        prefs.edit().putInt("completed_exercises", currentCount + 1).apply();
    }
    private long getChildId() {
        long id = getIntent().getLongExtra("CHILD_ID", -1L);

        if (id == -1L) {
            id = getSharedPreferences("KidsApp", MODE_PRIVATE).getLong("current_child_id", -1L);
        }
        if (id == -1L) {
            id = getSharedPreferences("KidsAppPrefs", MODE_PRIVATE).getLong("active_child_id", -1L);
        }
        return id;
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