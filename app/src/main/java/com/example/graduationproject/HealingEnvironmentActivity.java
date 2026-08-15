package com.example.graduationproject;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.adapters.EnvironmentAdapter;
import com.example.graduationproject.models.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HealingEnvironmentActivity extends AppCompatActivity {

    private TextView tvTimer, tvCurrentTitle, tvCurrentSub;
    private TextView btnTime10, btnTime20, btnTime40;
    private FloatingActionButton btnPlayPause;
    private ImageView btnBack, btnPrevious, btnNext, imgCurrentBg;
    private Slider sliderVolume;

    private RecyclerView rvEnvironments;
    private EnvironmentAdapter adapter;
    private List<Environment> environmentList;
    private int currentPlayIndex = 0;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 1200000;
    private boolean isTimerRunning = false;

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_healing_environment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutHeader).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            findViewById(R.id.layoutHeader).setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupVolumeControl();
        setupTimerButtons();
        setupPlayerControls();

        if (!environmentList.isEmpty()) {
            updateMainPlayer(environmentList.get(0));
        }

        startEntranceAnimations();
    }

    private void initViews() {
        tvTimer = findViewById(R.id.tvTimer);
        tvCurrentTitle = findViewById(R.id.tvCurrentTitle);
        tvCurrentSub = findViewById(R.id.tvCurrentSub);
        imgCurrentBg = findViewById(R.id.imgCurrentBg);

        btnTime10 = findViewById(R.id.btnTime10);
        btnTime20 = findViewById(R.id.btnTime20);
        btnTime40 = findViewById(R.id.btnTime40);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnBack = findViewById(R.id.btnBack);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        sliderVolume = findViewById(R.id.sliderVolume);
        rvEnvironments = findViewById(R.id.rvEnvironments);
    }

    private void startEntranceAnimations() {
        View cardPlayer = findViewById(R.id.cardMainPlayer);
        View tvSelectTitle = findViewById(R.id.tvSelectTitle);
        View layoutTimerSelector = findViewById(R.id.layoutTimerSelector);
        View layoutVolumeControl = findViewById(R.id.layoutVolumeControl);

        cardPlayer.setAlpha(0f);
        cardPlayer.setScaleX(0.8f);
        cardPlayer.setScaleY(0.8f);
        
        tvSelectTitle.setAlpha(0f);
        tvSelectTitle.setTranslationY(20f);
        
        rvEnvironments.setAlpha(0f);
        rvEnvironments.setTranslationX(-100f);
        
        layoutTimerSelector.setAlpha(0f);
        layoutTimerSelector.setTranslationY(30f);
        
        layoutVolumeControl.setAlpha(0f);
        layoutVolumeControl.setTranslationY(50f);

        cardPlayer.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(800).setInterpolator(new OvershootInterpolator()).start();
        
        tvSelectTitle.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(300).start();
        
        rvEnvironments.animate().alpha(1f).translationX(0f).setDuration(800).setStartDelay(400).setInterpolator(new DecelerateInterpolator()).start();
        
        layoutTimerSelector.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(600).start();
        
        layoutVolumeControl.animate().alpha(1f).translationY(0f).setDuration(700).setStartDelay(800).setInterpolator(new OvershootInterpolator()).start();
    }

    private void setupRecyclerView() {
        environmentList = new ArrayList<>();
        environmentList.add(new Environment(1, "غابة هادئة", "FOREST", R.drawable.video, R.raw.tranquil_forest));
        environmentList.add(new Environment(2, "صوت المطر", "RAIN", R.drawable.video, R.raw.tranquil_forest));
        environmentList.add(new Environment(3, "شاطئ البحر", "BEACH", R.drawable.video, R.raw.tranquil_forest));
        environmentList.add(new Environment(4, "نار دافئة", "FIREPLACE", R.drawable.video, R.raw.tranquil_forest));

        adapter = new EnvironmentAdapter(environmentList, environment -> {
            updateMainPlayer(environment);
            for (int i = 0; i < environmentList.size(); i++) {
                if (environmentList.get(i).getId() == environment.getId()) {
                    currentPlayIndex = i;
                    break;
                }
            }
        });

        rvEnvironments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvEnvironments.setAdapter(adapter);
    }

    private void updateMainPlayer(Environment environment) {
        tvCurrentTitle.setText(environment.getTitle());
        tvCurrentSub.setText(environment.getSubtitle());
        imgCurrentBg.setImageResource(environment.getImageResId());

        tvCurrentTitle.setAlpha(0f);
        tvCurrentTitle.setTranslationY(20f);
        tvCurrentTitle.animate().alpha(1f).translationY(0f).setDuration(400).start();

        if (isTimerRunning) {
            playNewSound(environment.getSoundResId());
        } else {
            prepareSoundOnly(environment.getSoundResId());
        }
    }

    private void playNewSound(int soundResId) {
        stopAndReleaseMediaPlayer();
        try {
            mediaPlayer = MediaPlayer.create(this, soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } else {
                Toast.makeText(this, "فشل تحميل ملف الصوت!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareSoundOnly(int soundResId) {
        stopAndReleaseMediaPlayer();
        try {
            mediaPlayer = MediaPlayer.create(this, soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVolumeControl() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sliderVolume.setValueFrom(0);
        sliderVolume.setValueTo(maxVolume);
        sliderVolume.setValue(currentVolume);

        sliderVolume.addOnChangeListener((slider, value, fromUser) -> {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) value, 0);
        });
    }

    private void setupTimerButtons() {
        btnTime10.setOnClickListener(v -> selectTimerButton(btnTime10, 10));
        btnTime20.setOnClickListener(v -> selectTimerButton(btnTime20, 20));
        btnTime40.setOnClickListener(v -> selectTimerButton(btnTime40, 40));
    }

    private void selectTimerButton(TextView selectedButton, int minutes) {
        resetTimerButtonsStyle();
        selectedButton.setBackgroundResource(R.drawable.bg_timer_chip_selected);
        selectedButton.setTextColor(getResources().getColor(android.R.color.white));

        pauseTimer();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        timeLeftInMillis = minutes * 60 * 1000;
        updateCountDownText();
    }

    private void resetTimerButtonsStyle() {
        int primaryColor = android.graphics.Color.parseColor("#2D587B");
        btnTime10.setBackgroundResource(R.drawable.bg_timer_chip);
        btnTime10.setTextColor(primaryColor);
        btnTime20.setBackgroundResource(R.drawable.bg_timer_chip);
        btnTime20.setTextColor(primaryColor);
        btnTime40.setBackgroundResource(R.drawable.bg_timer_chip);
        btnTime40.setTextColor(primaryColor);
    }

    private void setupPlayerControls() {
        btnBack.setOnClickListener(v -> finish());

        btnPlayPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } else {
                startTimer();
                if (mediaPlayer == null) {
                    prepareSoundOnly(environmentList.get(currentPlayIndex).getSoundResId());
                }
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPlayIndex < environmentList.size() - 1) {
                currentPlayIndex++;
            } else {
                currentPlayIndex = 0;
            }
            updateMainPlayer(environmentList.get(currentPlayIndex));
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentPlayIndex > 0) {
                currentPlayIndex--;
            } else {
                currentPlayIndex = environmentList.size() - 1;
            }
            updateMainPlayer(environmentList.get(currentPlayIndex));
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                tvTimer.setText("انتهت جلستك بنجاح 🌸");
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        }.start();

        isTimerRunning = true;
        btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "متبقي %02d:%02d د", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    private void stopAndReleaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAndReleaseMediaPlayer();
    }
}
