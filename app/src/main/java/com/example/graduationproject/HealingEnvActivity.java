package com.example.graduationproject;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.HealingEnvironment;
import com.example.graduationproject.models.HealingEnvironmentRepository;
import com.example.graduationproject.models.SoundLayer;
import com.example.graduationproject.models.TimerOption;
import com.example.graduationproject.widget.FadeUtils;
import com.example.graduationproject.widget.HeroPulseAnimator;
import com.example.graduationproject.widget.TapBounce;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Full Java/Android port of "HealingEnvironmentsFlow": an ambient-sound
 * player with 4 illustrated environments, per-layer volume sliders, a
 * scrubbing progress bar, and a simulated sleep timer.
 */
public class HealingEnvActivity extends AppCompatActivity {

    // ------- state (mirrors the React useState hooks) -------
    private int envIdx = HealingEnvironmentRepository.defaultIndex(); // starts on forest
    private boolean playing = false;
    private int elapsed = 0;
    private int timerMinutes = 30; // -1 == sleep
    private int remainingMin = 30;
    private int remainingSec = 0;
    private boolean timerPaused = false;
    private boolean ended = false;
    private final Map<String, Map<String, Integer>> layerLevels = new HashMap<>();
    private final Map<String, MediaPlayer> activePlayers = new HashMap<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable playbackTicker;
    private Runnable sleepTicker;
    private ValueAnimator heroPulseAnimator;
    private ValueAnimator scrubberAnimator;

    // ------- views -------
    private FrameLayout heroCard;
    private ImageView heroScene;
    private TextView tvEnvBadge, tvStatusLabel, tvElapsed, tvTotal, tvTimerStatus, tvEndedMessage, tvStatusClock;
    private View statusDot, heroPulseRing, scrubberTrack, scrubberFill;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            if (tvStatusClock != null) {
                tvStatusClock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
            }
            clockHandler.postDelayed(this, 60000L);
        }
    };
    private ImageButton btnHeroPlayPause, btnPlayPause, btnPrev, btnNext;
    private LinearLayout llEnvPicker, llLayers, llTimerChips;
    private androidx.cardview.widget.CardView cvActiveTimer;
    private TextView tvActiveEnvName, tvTimerRange;
    private ImageButton btnStopActiveTimer, btnPlayPauseActiveTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healing_env);

        tvStatusClock = findViewById(R.id.tvStatusClock);
        clockRunnable.run();

        initLayerLevels();
        bindViews();
        buildEnvironmentPicker();
        buildTimerChips();
        setListeners();

        renderHero(false);
        renderScrubber(false);
        renderTimerStatus();
        renderLayers(false);
        renderTimerChips();
        renderEndedMessage();
    }

    private void initLayerLevels() {
        for (HealingEnvironment env : HealingEnvironmentRepository.getAll()) {
            Map<String, Integer> levels = new HashMap<>();
            for (SoundLayer layer : env.layers) {
                levels.put(layer.key, layer.defaultLevel);
            }
            layerLevels.put(env.key, levels);
        }
    }

    private void bindViews() {
        heroCard = findViewById(R.id.heroCard);
        heroScene = findViewById(R.id.heroScene);
        tvEnvBadge = findViewById(R.id.tvEnvBadge);
        tvStatusLabel = findViewById(R.id.tvStatusLabel);
        statusDot = findViewById(R.id.statusDot);
        heroPulseRing = findViewById(R.id.heroPulseRing);
        btnHeroPlayPause = findViewById(R.id.btnHeroPlayPause);

        tvElapsed = findViewById(R.id.tvElapsed);
        tvTotal = findViewById(R.id.tvTotal);
        scrubberTrack = findViewById(R.id.scrubberTrack);
        scrubberFill = findViewById(R.id.scrubberFill);

        btnPrev = findViewById(R.id.btnPrev);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        tvTimerStatus = findViewById(R.id.tvTimerStatus);
        tvEndedMessage = findViewById(R.id.tvEndedMessage);

        llEnvPicker = findViewById(R.id.llEnvPicker);
        llLayers = findViewById(R.id.llLayers);
        llTimerChips = findViewById(R.id.llTimerChips);

        cvActiveTimer = findViewById(R.id.cvActiveTimer);
        tvActiveEnvName = findViewById(R.id.tvActiveEnvName);
        tvTimerRange = findViewById(R.id.tvTimerRange);
        btnStopActiveTimer = findViewById(R.id.btnStopActiveTimer);
        btnPlayPauseActiveTimer = findViewById(R.id.btnPlayPauseActiveTimer);

        tvTotal.setText(fmt(HealingEnvironmentRepository.TRACK_LEN));
    }

    private void setListeners() {
        btnHeroPlayPause.setOnClickListener(v -> togglePlaying());
        btnPlayPause.setOnClickListener(v -> togglePlaying());
        btnPlayPauseActiveTimer.setOnClickListener(v -> togglePlaying());
        btnPrev.setOnClickListener(v -> switchEnv(-1));
        btnNext.setOnClickListener(v -> switchEnv(1));
        btnStopActiveTimer.setOnClickListener(v -> stopTimerManually());
        TapBounce.attach(btnPrev);
        TapBounce.attach(btnNext);
    }

    // ===================== ENVIRONMENT PICKER =====================

    private void buildEnvironmentPicker() {
        llEnvPicker.removeAllViews();
        List<HealingEnvironment> envs = HealingEnvironmentRepository.getAll();
        for (int i = 0; i < envs.size(); i++) {
            final int index = i;
            HealingEnvironment env = envs.get(i);
            View item = LayoutInflater.from(this).inflate(R.layout.item_env_picker, llEnvPicker, false);

            View envBg = item.findViewById(R.id.envBg);
            ImageView ivIcon = item.findViewById(R.id.ivEnvIcon);
            TextView tvLabel = item.findViewById(R.id.tvEnvLabel);
            TextView tvTag = item.findViewById(R.id.tvEnvTag);
            View border = item.findViewById(R.id.envSelectedBorder);

            envBg.setBackgroundResource(env.gradientBackgroundRes);
            ivIcon.setImageResource(env.iconRes);
            tvLabel.setText(env.label);
            tvTag.setText(env.tag);
            border.setVisibility(index == envIdx ? View.VISIBLE : View.INVISIBLE);

            item.setOnClickListener(v -> {
                envIdx = index;
                elapsed = 0;
                onEnvironmentChanged();
            });
            TapBounce.attach(item);

            llEnvPicker.addView(item);
        }
    }

    private void refreshEnvironmentPickerSelection() {
        for (int i = 0; i < llEnvPicker.getChildCount(); i++) {
            View border = llEnvPicker.getChildAt(i).findViewById(R.id.envSelectedBorder);
            border.setVisibility(i == envIdx ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void switchEnv(int direction) {
        int count = HealingEnvironmentRepository.getAll().size();
        envIdx = (envIdx + direction + count) % count;
        elapsed = 0;
        onEnvironmentChanged();
    }

    private void onEnvironmentChanged() {
        refreshEnvironmentPickerSelection();
        renderHero(true);
        renderScrubber(false);
        renderLayers(true);
        startAudioForCurrentEnv();
    }

    private void startAudioForCurrentEnv() {
        stopAllAudio();
        if (!playing) return;

        HealingEnvironment env = currentEnv();
        Map<String, Integer> levels = layerLevels.get(env.key);

        for (SoundLayer layer : env.layers) {
            MediaPlayer mp = MediaPlayer.create(this, layer.soundRes);
            if (mp != null) {
                mp.setLooping(true);
                float volume = (levels.get(layer.key) / 100f);
                mp.setVolume(volume, volume);
                mp.start();
                activePlayers.put(layer.key, mp);
            }
        }
    }

    private void stopAllAudio() {
        for (MediaPlayer mp : activePlayers.values()) {
            if (mp.isPlaying()) mp.stop();
            mp.release();
        }
        activePlayers.clear();
    }

    private void pauseAllAudio() {
        for (MediaPlayer mp : activePlayers.values()) {
            if (mp.isPlaying()) mp.pause();
        }
    }

    private void resumeAllAudio() {
        if (activePlayers.isEmpty()) {
            startAudioForCurrentEnv();
        } else {
            for (MediaPlayer mp : activePlayers.values()) {
                mp.start();
            }
        }
    }

    // ===================== HERO CARD =====================

    private void renderHero(boolean animateEnvChange) {
        HealingEnvironment env = currentEnv();
        heroCard.setBackgroundResource(env.gradientBackgroundRes);
        loadGif(env.gifRes);

        tvEnvBadge.setText(getString(R.string.env_badge_format, env.label, env.tag));

        if (animateEnvChange) {
            FadeUtils.envFade(heroCard);
        }

        renderPlayPauseButtons();
        renderStatusIndicator();
    }

    private void loadGif(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                ImageDecoder.Source source = ImageDecoder.createSource(getResources(), resId);
                Drawable drawable = ImageDecoder.decodeDrawable(source);
                heroScene.setImageDrawable(drawable);
                if (drawable instanceof AnimatedImageDrawable) {
                    ((AnimatedImageDrawable) drawable).start();
                }
            } catch (IOException e) {
                heroScene.setImageResource(resId);
            }
        } else {
            heroScene.setImageResource(resId);
        }
    }

    private void renderPlayPauseButtons() {
        int icon = playing ? R.drawable.ic_pause : R.drawable.ic_play;
        btnHeroPlayPause.setImageResource(icon);
        btnPlayPause.setImageResource(icon);
        if (btnPlayPauseActiveTimer != null) {
            btnPlayPauseActiveTimer.setImageResource(icon);
        }

        if (playing) {
            if (heroPulseAnimator == null) {
                heroPulseAnimator = HeroPulseAnimator.start(heroPulseRing);
            }
            // Resume GIF if possible
            Drawable d = heroScene.getDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && d instanceof AnimatedImageDrawable) {
                ((AnimatedImageDrawable) d).start();
            }
        } else {
            HeroPulseAnimator.stop(heroPulseAnimator, heroPulseRing);
            heroPulseAnimator = null;
            // Stop GIF if possible
            Drawable d = heroScene.getDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && d instanceof AnimatedImageDrawable) {
                ((AnimatedImageDrawable) d).stop();
            }
        }
    }

    private void renderStatusIndicator() {
        statusDot.setBackgroundResource(playing ? R.drawable.bg_status_dot_playing : R.drawable.bg_status_dot_paused);
        tvStatusLabel.setText(playing ? R.string.status_playing : R.string.status_paused);
    }

    private void togglePlaying() {
        playing = !playing;
        renderHero(false);
        renderTimerStatus();

        if (playing) {
            startPlaybackTicker();
            startSleepTicker();
            resumeAllAudio();
        } else {
            stopPlaybackTicker();
            stopSleepTicker();
            pauseAllAudio();
        }
    }

    // ===================== SCRUBBER =====================

    private void renderScrubber(boolean animate) {
        tvElapsed.setText(fmt(elapsed));
        float fraction = elapsed / (float) HealingEnvironmentRepository.TRACK_LEN;

        if (scrubberAnimator != null) scrubberAnimator.cancel();

        scrubberTrack.post(() -> {
            int trackWidth = scrubberTrack.getWidth();
            int targetWidth = Math.round(trackWidth * fraction);

            if (!animate) {
                ViewGroup.LayoutParams lp = scrubberFill.getLayoutParams();
                lp.width = targetWidth;
                scrubberFill.setLayoutParams(lp);
                return;
            }

            int fromWidth = scrubberFill.getWidth();
            scrubberAnimator = ValueAnimator.ofInt(fromWidth, targetWidth);
            scrubberAnimator.setDuration(1000); // matches `transition: width 1s linear`
            scrubberAnimator.setInterpolator(new LinearInterpolator());
            scrubberAnimator.addUpdateListener(a -> {
                ViewGroup.LayoutParams lp = scrubberFill.getLayoutParams();
                lp.width = (int) a.getAnimatedValue();
                scrubberFill.setLayoutParams(lp);
            });
            scrubberAnimator.start();
        });
    }

    // ===================== PLAYBACK + SLEEP TIMERS =====================

    private void startPlaybackTicker() {
        stopPlaybackTicker();
        playbackTicker = new Runnable() {
            @Override
            public void run() {
                elapsed = (elapsed + 1 >= HealingEnvironmentRepository.TRACK_LEN) ? 0 : elapsed + 1;
                renderScrubber(true);
                if (playing) handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(playbackTicker, 1000);
    }

    private void stopPlaybackTicker() {
        if (playbackTicker != null) handler.removeCallbacks(playbackTicker);
    }

    /** Sleep-timer countdown. */
    private void startSleepTicker() {
        stopSleepTicker();
        if (remainingMin <= 0 && remainingSec <= 0) {
            timerEnded();
            return;
        }
        sleepTicker = new Runnable() {
            @Override
            public void run() {
                if (timerPaused) {
                    handler.postDelayed(this, 1000);
                    return;
                }

                if (remainingSec == 0) {
                    if (remainingMin > 0) {
                        remainingMin--;
                        remainingSec = 59;
                    } else {
                        timerEnded();
                        return;
                    }
                } else {
                    remainingSec--;
                }

                renderTimerStatus();
                updateActiveTimerCard();
                if (playing) handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(sleepTicker, 1000);
    }

    private void timerEnded() {
        playing = false;
        ended = true;
        remainingMin = 0;
        remainingSec = 0;
        if (cvActiveTimer != null) cvActiveTimer.setVisibility(View.GONE);
        renderHero(false);
        renderEndedMessage();
        stopPlaybackTicker();
        stopAllAudio();
    }

    private void stopTimerManually() {
        remainingMin = 0;
        remainingSec = 0;
        timerEnded();
        renderTimerStatus();
        renderTimerChips();
    }

    private void stopSleepTicker() {
        if (sleepTicker != null) handler.removeCallbacks(sleepTicker);
    }

    private void renderTimerStatus() {
        String suffix;
        if (timerMinutes == TimerOption.SLEEP && remainingMin > 60) {
            suffix = getString(R.string.timer_label_sleep);
        } else {
            suffix = String.format(Locale.US, "%02d:%02d متبقية", remainingMin, remainingSec);
        }
        tvTimerStatus.setText(getString(R.string.timer_prefix, suffix));
    }

    private void renderEndedMessage() {
        tvEndedMessage.setVisibility(ended ? View.VISIBLE : View.GONE);
    }

    // ===================== SLEEP TIMER CHIPS =====================

    private void buildTimerChips() {
        llTimerChips.removeAllViews();

        // Add standard timers
        for (TimerOption option : HealingEnvironmentRepository.TIMERS) {
            TextView chip = (TextView) LayoutInflater.from(this)
                    .inflate(R.layout.item_timer_chip, llTimerChips, false);
            chip.setText(option.label);
            chip.setOnClickListener(v -> pickTimer(option));
            llTimerChips.addView(chip);
        }

        // Add "+" button for custom timer
        TextView customChip = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.item_timer_chip, llTimerChips, false);
        customChip.setText("+");
        customChip.setOnClickListener(v -> showCustomTimerDialog());
        llTimerChips.addView(customChip);

        renderTimerChips();
    }

    private void pickTimer(TimerOption option) {
        timerMinutes = option.minutes;
        remainingMin = option.minutes;
        remainingSec = 0;
        timerPaused = false;
        ended = false;

        cvActiveTimer.setVisibility(View.VISIBLE);
        tvActiveEnvName.setText(currentEnv().label);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", Locale.US);
        String startTime = sdf.format(cal.getTime());
        cal.add(java.util.Calendar.MINUTE, remainingMin);
        String endTime = sdf.format(cal.getTime());
        tvTimerRange.setText("بدأ " + startTime + " · ينتهي " + endTime);

        renderTimerChips();
        renderTimerStatus();
        renderEndedMessage();

        if (playing) {
            startSleepTicker();
        }
    }

    private void updateActiveTimerCard() {
        if (cvActiveTimer.getVisibility() == View.VISIBLE) {
            // Updated to use the status text or range text since countdown is removed for thiner design
            // Or we could put the countdown in the name label.
        }
    }

    private void renderTimerChips() {
        int[] gradients = {
                R.drawable.bg_timer_1,
                R.drawable.bg_timer_2,
                R.drawable.bg_timer_3,
                R.drawable.bg_timer_4,
                R.drawable.bg_timer_1 // for the + button
        };

        for (int i = 0; i < llTimerChips.getChildCount(); i++) {
            TextView chip = (TextView) llTimerChips.getChildAt(i);
            boolean isPlusButton = (i == llTimerChips.getChildCount() - 1);
            boolean selected = false;

            if (!isPlusButton) {
                TimerOption option = HealingEnvironmentRepository.TIMERS.get(i);
                selected = option.minutes == timerMinutes;
            }

            if (selected) {
                chip.setBackgroundResource(R.drawable.bg_timer_selected);
                chip.setTextColor(Color.WHITE);
                chip.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
            } else {
                chip.setBackgroundResource(gradients[i % gradients.length]);
                chip.setTextColor(getResources().getColor(R.color.text_main));
                chip.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
            }
        }
    }

    private void showCustomTimerDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom_timer, null);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }

        android.widget.NumberPicker npHours = dialogView.findViewById(R.id.npHours);
        android.widget.NumberPicker npMinutes = dialogView.findViewById(R.id.npMinutes);
        Button btnSet = dialogView.findViewById(R.id.btnSetTimer);

        npHours.setMinValue(0);
        npHours.setMaxValue(23);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(59);
        npMinutes.setValue(30); // Default

        btnSet.setOnClickListener(v -> {
            int h = npHours.getValue();
            int m = npMinutes.getValue();
            int totalMins = (h * 60) + m;

            if (totalMins > 0) {
                String label = h > 0 ? (h + " س " + m + " د") : (m + " د");
                pickTimer(new TimerOption(totalMins, label));
                dialog.dismiss();
            }
        });

        dialog.show();

        // Simple scale animation for dialog entry
        dialogView.setScaleX(0.7f);
        dialogView.setScaleY(0.7f);
        dialogView.setAlpha(0f);
        dialogView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).start();
    }

    // ===================== SOUND LAYERS =====================

    private void renderLayers(boolean animate) {
        llLayers.removeAllViews();
        HealingEnvironment env = currentEnv();
        Map<String, Integer> levels = layerLevels.get(env.key);

        int[] gradResIds = {
                R.drawable.grad_layer_1,
                R.drawable.grad_layer_2,
                R.drawable.grad_layer_3,
                R.drawable.grad_layer_4,
                R.drawable.grad_layer_5
        };

        int index = 0;
        for (SoundLayer layer : env.layers) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_layer_row, llLayers, false);
            int gradResId = gradResIds[index % gradResIds.length];
            index++;

            ImageView ivIcon = row.findViewById(R.id.ivLayerIcon);
            TextView tvLabel = row.findViewById(R.id.tvLayerLabel);
            TextView tvPercent = row.findViewById(R.id.tvLayerPercent);
            SeekBar seekBar = row.findViewById(R.id.seekLayer);
            ImageView ivProgressFill = row.findViewById(R.id.ivProgressFill);

            ivIcon.setImageResource(layer.iconRes);
            tvLabel.setText(layer.label);
            int level = levels.get(layer.key);
            tvPercent.setText(String.valueOf(level));

            seekBar.setProgress(level);
            ivProgressFill.setImageResource(gradResId);
            ivProgressFill.setImageLevel(level * 100);

            // FIX: prevent ScrollView from intercepting touches when user is sliding the SeekBar
            seekBar.setOnTouchListener((v, event) -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    levels.put(layer.key, progress);
                    tvPercent.setText(String.valueOf(progress));
                    ivProgressFill.setImageLevel(progress * 100);

                    MediaPlayer mp = activePlayers.get(layer.key);
                    if (mp != null) {
                        float vol = progress / 100f;
                        mp.setVolume(vol, vol);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            llLayers.addView(row);
        }

        if (animate) {
            FadeUtils.layersFade(llLayers);
        }
    }

    private void updateNeonShadow(View shadowView, int level) {
        if (level > 0) {
            shadowView.setVisibility(View.VISIBLE);
            // Scale between 0.8 (at level 1) and 1.2 (at level 100)
            float scale = 0.8f + (level / 100f) * 0.4f;
            shadowView.setScaleX(scale);
            shadowView.setScaleY(scale);
            // Alpha between 0.2 and 0.6
            shadowView.setAlpha(0.2f + (level / 100f) * 0.4f);
        } else {
            shadowView.setVisibility(View.INVISIBLE);
        }
    }

    // ===================== HELPERS =====================

    private HealingEnvironment currentEnv() {
        return HealingEnvironmentRepository.getAll().get(envIdx);
    }

    private String fmt(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format(Locale.US, "%d:%02d", m, s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playing) {
            startPlaybackTicker();
            startSleepTicker();
            resumeAllAudio();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlaybackTicker();
        stopSleepTicker();
        pauseAllAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clockHandler.removeCallbacks(clockRunnable);
        handler.removeCallbacksAndMessages(null);
        HeroPulseAnimator.stop(heroPulseAnimator, heroPulseRing);
        if (scrubberAnimator != null) scrubberAnimator.cancel();
        stopAllAudio();
    }
}
