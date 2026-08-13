package com.example.graduationproject;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.graduationproject.animation.AnimationManager;
import app.rive.runtime.kotlin.RiveAnimationView;

/**
 * A dedicated screen to test Lottie, Lordicon, dotLottie, and Rive assets.
 * Uses the centralized AnimationManager for all operations.
 */
public class AnimationDemoActivity extends AppCompatActivity {

    // --- Placeholders for your assets ---
    // Change these to your actual file names once you add them to res/raw/
    private static final String LOTTIE_FILE = "example_lottie.json";
    private static final String LORDICON_FILE = "example_lordicon.json";
    private static final String DOTLOTTIE_FILE = "example_animation.lottie";
    
    // Rive requires a raw resource ID. Using 0 as a placeholder to avoid compilation errors.
    // Replace with R.raw.example_rive once the file is added.
    private static final int RIVE_RES_ID = 0; 
    private static final String RIVE_SM = "STATE_MACHINE_NAME_HERE";

    private LottieAnimationView lottieView, lordiconView, dotLottieView;
    private RiveAnimationView riveView;
    private TextView tvLottieSpeed, tvRiveNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_demo);

        initViews();
        setupLottieSection();
        setupLordiconSection();
        setupDotLottieSection();
        setupRiveSection();
    }

    private void initViews() {
        lottieView = findViewById(R.id.lottieView);
        lordiconView = findViewById(R.id.lordiconView);
        dotLottieView = findViewById(R.id.dotLottieView);
        riveView = findViewById(R.id.riveView);
        
        tvLottieSpeed = findViewById(R.id.tvLottieSpeed);
        tvRiveNumber = findViewById(R.id.tvRiveNumber);
    }

    private void setupLottieSection() {
        findViewById(R.id.btnLottiePlay).setOnClickListener(v -> AnimationManager.playLottie(lottieView, LOTTIE_FILE));
        findViewById(R.id.btnLottiePause).setOnClickListener(v -> AnimationManager.pauseLottie(lottieView));
        findViewById(R.id.btnLottieRestart).setOnClickListener(v -> AnimationManager.restartLottie(lottieView));
        findViewById(R.id.btnLottieStop).setOnClickListener(v -> AnimationManager.stopLottie(lottieView));

        CheckBox cbLoop = findViewById(R.id.cbLottieLoop);
        cbLoop.setOnCheckedChangeListener((btn, isChecked) -> AnimationManager.setLottieLoop(lottieView, isChecked));

        SeekBar sbSpeed = findViewById(R.id.sbLottieSpeed);
        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = progress / 100f;
                if (speed < 0.25f) speed = 0.25f;
                AnimationManager.setLottieSpeed(lottieView, speed);
                tvLottieSpeed.setText(String.format("Speed: %.2fx", speed));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupLordiconSection() {
        findViewById(R.id.btnLordiconPlay).setOnClickListener(v -> AnimationManager.playLordicon(lordiconView, LORDICON_FILE));
        findViewById(R.id.btnLordiconRestart).setOnClickListener(v -> AnimationManager.restartLordicon(lordiconView));
        findViewById(R.id.btnLordiconStop).setOnClickListener(v -> AnimationManager.stopLordicon(lordiconView));
    }

    private void setupDotLottieSection() {
        findViewById(R.id.btnDotLottiePlay).setOnClickListener(v -> AnimationManager.playDotLottie(dotLottieView, DOTLOTTIE_FILE));
        findViewById(R.id.btnDotLottiePause).setOnClickListener(v -> AnimationManager.pauseLottie(dotLottieView));
        findViewById(R.id.btnDotLottieRestart).setOnClickListener(v -> AnimationManager.restartLottie(dotLottieView));
    }

    private void setupRiveSection() {
        findViewById(R.id.btnRivePlay).setOnClickListener(v -> {
            if (RIVE_RES_ID != 0) {
                AnimationManager.playRive(riveView, RIVE_RES_ID);
            } else {
                showResourceToast();
            }
        });

        findViewById(R.id.btnRivePause).setOnClickListener(v -> AnimationManager.pauseRive(riveView));
        findViewById(R.id.btnRiveRestart).setOnClickListener(v -> AnimationManager.resetRive(riveView));

        // State Machine Inputs
        findViewById(R.id.btnRiveTrigger).setOnClickListener(v -> 
            AnimationManager.triggerRive(riveView, RIVE_SM, "TRIGGER_NAME_HERE")
        );

        CheckBox cbBoolean = findViewById(R.id.cbRiveBoolean);
        cbBoolean.setOnCheckedChangeListener((btn, isChecked) -> 
            AnimationManager.setRiveBoolean(riveView, RIVE_SM, "BOOLEAN_INPUT_NAME_HERE", isChecked)
        );

        SeekBar sbNumber = findViewById(R.id.sbRiveNumber);
        sbNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = (float) progress;
                AnimationManager.setRiveNumber(riveView, RIVE_SM, "NUMBER_INPUT_NAME_HERE", value);
                tvRiveNumber.setText(String.format("Number Input: %.1f", value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void showResourceToast() {
        Toast.makeText(this, "Add a .riv file to res/raw/ and update RIVE_RES_ID in code.", Toast.LENGTH_LONG).show();
    }
}
