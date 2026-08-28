package com.example.graduationproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.HealingEnvironment;
import com.example.graduationproject.models.HealingEnvironmentRepository;

import java.util.Locale;

public class HealingPlayerActivity extends AppCompatActivity {

    private int envIdx = 0;
    private boolean playing = true;
    private int elapsed = 0;

    private ImageView ivPlayerBg, ivPlayerArt;
    private TextView tvPlayerTitle, tvPlayerArtist, tvPlayerElapsed, tvPlayerTotal;
    private SeekBar playerSeekBar;
    private ImageButton btnPlayerPlayPause, btnPlayerBack;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable ticker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healing_player);

        envIdx = getIntent().getIntExtra("env_idx", 0);
        playing = getIntent().getBooleanExtra("playing", true);
        elapsed = getIntent().getIntExtra("elapsed", 0);

        bindViews();
        renderEnv();
        setupListeners();
        
        if (playing) {
            startTicker();
        }
    }

    private void bindViews() {
        ivPlayerBg = findViewById(R.id.ivPlayerBg);
        ivPlayerArt = findViewById(R.id.ivPlayerArt);
        tvPlayerTitle = findViewById(R.id.tvPlayerTitle);
        tvPlayerArtist = findViewById(R.id.tvPlayerArtist);
        tvPlayerElapsed = findViewById(R.id.tvPlayerElapsed);
        tvPlayerTotal = findViewById(R.id.tvPlayerTotal);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        btnPlayerPlayPause = findViewById(R.id.btnPlayerPlayPause);
        btnPlayerBack = findViewById(R.id.btnPlayerBack);
    }

    private void renderEnv() {
        HealingEnvironment env = HealingEnvironmentRepository.getAll().get(envIdx);
        ivPlayerBg.setImageResource(env.gradientBackgroundRes);
        ivPlayerArt.setImageResource(env.gifRes);
        tvPlayerTitle.setText(env.label);
        tvPlayerArtist.setText(env.tag);
        tvPlayerTotal.setText(fmt(HealingEnvironmentRepository.TRACK_LEN));
        playerSeekBar.setMax(HealingEnvironmentRepository.TRACK_LEN);
        updateProgress();
        updatePlayPauseIcon();
    }

    private void setupListeners() {
        btnPlayerBack.setOnClickListener(v -> finish());
        btnPlayerPlayPause.setOnClickListener(v -> {
            playing = !playing;
            updatePlayPauseIcon();
            if (playing) startTicker();
            else stopTicker();
        });
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    elapsed = progress;
                    updateProgress();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { stopTicker(); }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { if (playing) startTicker(); }
        });
    }

    private void updatePlayPauseIcon() {
        btnPlayerPlayPause.setImageResource(playing ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateProgress() {
        tvPlayerElapsed.setText(fmt(elapsed));
        playerSeekBar.setProgress(elapsed);
    }

    private void startTicker() {
        stopTicker();
        ticker = new Runnable() {
            @Override
            public void run() {
                elapsed = (elapsed + 1 >= HealingEnvironmentRepository.TRACK_LEN) ? 0 : elapsed + 1;
                updateProgress();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(ticker, 1000);
    }

    private void stopTicker() {
        if (ticker != null) handler.removeCallbacks(ticker);
    }

    private String fmt(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format(Locale.US, "%d:%02d", m, s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTicker();
    }
}
