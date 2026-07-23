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
        binding = ActivityKidsBubbleBreathingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(KidsBubbleBreathingActivity.this, KidsAiChatActivity.class);
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
        binding.btnPrimary.setText("يلا نبدأ! 🫧");
        binding.tvInstructionTitle.setText("خلنا ننفخ فقاعات سوا!");
        binding.tvInstructionBody.setText("انفخ بفمك قدام التلفون بهدوء، وشوف الفقاعة تكبر وتطير — كل نفخة، أي حجم، تصير فقاعة حلوة!");
    }

    private void showReadyState() {
        binding.btnPrimary.setSelected(true);
        binding.bubbleView.showMode(KidsBubbleView.Mode.READY);
        binding.btnQuietExit.setVisibility(View.VISIBLE);
        binding.tvStars.setVisibility(View.VISIBLE);
        binding.tvBreathHint.setVisibility(View.VISIBLE);
        binding.tvInstructionTitle.setVisibility(View.GONE);
        binding.tvInstructionBody.setVisibility(View.GONE);
        binding.btnPrimary.setText("اضغط هنا وانفخ");
        updateStars();
    }

    private void startHoldingBreath() {
        isHolding = true;
        breathProgress = 0f;
        binding.bubbleView.setProgress(breathProgress);
        binding.bubbleView.showMode(KidsBubbleView.Mode.INFLATING);
        binding.btnPrimary.setText("استمر بالضغط...");
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
            binding.btnPrimary.setText("اضغط هنا وانفخ");
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
            binding.btnPrimary.setText("اضغط هنا وانفخ");
        }
    }

    private void showDoneState() {
        isHolding = false;
        handler.removeCallbacks(breathRunnable);
        binding.bubbleView.showMode(KidsBubbleView.Mode.DONE);

        binding.btnQuietExit.setVisibility(View.GONE);
        binding.tvStars.setVisibility(View.GONE);
        binding.tvBreathHint.setVisibility(View.GONE);

        binding.tvInstructionTitle.setVisibility(View.VISIBLE);
        binding.tvInstructionBody.setVisibility(View.VISIBLE);

        binding.tvInstructionTitle.setText("يلا! نفخنا " + completedBubbles + " فقاعات 🎉");
        binding.tvInstructionBody.setText("خمس نفسات هادئة، وأنت الحين أهدأ شوي");

        binding.btnPrimary.setVisibility(View.GONE);
        binding.actionsRow.setVisibility(View.VISIBLE);
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