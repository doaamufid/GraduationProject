package com.example.graduationproject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Modern AI Chat Welcome Screen with organic animations and tactile interactions.
 */
public class AiWelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_welcome);

        setupChipInteractions();
    }

    private void setupChipInteractions() {
        int[] chipIds = {
                R.id.chipRewrite,
                R.id.chipCreateImage,
                R.id.chipMakePlan,
                R.id.chipAnalyseData
        };

        for (int id : chipIds) {
            View chip = findViewById(id);
            attachTactileFeedback(chip);
            chip.setOnClickListener(v -> {
                String action = "";
                if (id == R.id.chipRewrite) action = "Rewrite";
                else if (id == R.id.chipCreateImage) action = "Create Image";
                else if (id == R.id.chipMakePlan) action = "Make Plan";
                else if (id == R.id.chipAnalyseData) action = "Analyse Data";
                
                Toast.makeText(this, "Selected: " + action, Toast.LENGTH_SHORT).show();
            });
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    /**
     * Attaches a scale-down micro-interaction for a premium tactile feel.
     */
    private void attachTactileFeedback(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animateScale(v, 0.96f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    animateScale(v, 1.0f);
                    break;
            }
            return false;
        });
    }

    private void animateScale(View view, float scale) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(150);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }
}
