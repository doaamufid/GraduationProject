package com.example.graduationproject;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.BreathingFeatBreathMode;
import com.example.graduationproject.util.ChimePlayer;
import com.example.graduationproject.util.Prefs;
import com.example.graduationproject.view.BreathingFeatBreathingCircleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Full port of the JS <BreatheSessionScreen/>: home panel (pick/preview a mode),
 * session panel (live animated breathing circle + phase timer), celebrate panel,
 * and the modes/settings bottom sheet. Handles both:
 *  - being launched from the routine with a preset mode (auto-starts, like startModeKey),
 *  - being opened standalone (home panel lets you pick any mode).
 */
public class BreathingFeatBreatheSessionActivity extends AppCompatActivity {

    private static final String[] QUOTES = {
            "خذ وقتك، ما فيه استعجال", "أنت بخير، فقط تنفس", "كل نفس خطوة نحو الهدوء",
            "الهدوء يبدأ بنفس واحد", "دع جسدك يرتاح، ودع عقلك يصمت قليلاً", "أنتِ أقوى مما تتخيلين",
            "نفس بطيء يهدئ عاصفة الأفكار", "التنفس العميق هو عناق للروح", "أنتِ هنا، وهذا يكفي",
            "خطوة بخطوة، ونفس بنفس", "تنفسي، فالدنيا لن تفوتك بدقيقة راحة", "أنتِ تستحقين هذه اللحظة من الهدوء"
    };
    private static final String[] START_PHRASES = {
            "يلا نبدأ رحلة الهدوء 🌿", "جاهزة نتنفس سوا؟ 🌱", "خذي نفس عميق وابدئي 🍃",
            "لحظتك الهادئة بانتظارك 🌿", "هيا نمنح عقلك استراحة 🌱", "خطوة صغيرة نحو السكينة 🍃"
    };

    private Prefs prefs;
    private final Map<String, BreathingFeatBreathMode> modes = BreathingFeatBreathMode.buildRoutineModes();
    private BreathingFeatBreathMode customMode;
    private String currentModeKey = "box";
    private boolean soundOn = true;
    private final ChimePlayer chime = new ChimePlayer();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    // session runtime state
    private List<BreathingFeatBreathMode.Phase> steps;
    private int phaseIndex = 0;
    private int cycle = 1;
    private int totalCycles = 1;
    private long phaseStartElapsed = 0;
    private boolean running = false;
    private final Runnable tickRunnable = this::onTick;

    // views
    private View homePanel, sessionPanel, celebratePanel;
    private TextView homeStreakPill, homeModeInfo, homeModeDesc, homeGoal, homeQuote, btnStartSession;
    private BreathingFeatBreathingCircleView homeCircle, sessionCircle;
    private TextView sessionCycleText, sessionPercentText, btnStopSession;
    private ProgressBar sessionProgressBar;
    private TextView modeIconBadge, modeNameText, btnSoundToggle;
    private TextView statMinutes, statCycles, statStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breathing_feat_activity_breathe_session);
        prefs = new Prefs(this);
        chime.setSoundEnabled(soundOn);
        customMode = buildCustomMode();

        bindViews();
        applyTheme();

        String startKey = getIntent().getStringExtra(BreathingFeatBreatheRoutineActivity.EXTRA_MODE_KEY);
        if (startKey != null && modes.containsKey(startKey)) currentModeKey = startKey;

        findViewById(R.id.btnCloseSession).setOnClickListener(v -> finish());
        btnSoundToggle.setOnClickListener(v -> {
            soundOn = !soundOn;
            chime.setSoundEnabled(soundOn);
            btnSoundToggle.setText(soundOn ? "🔊" : "🔇");
        });
        findViewById(R.id.btnSettings).setOnClickListener(v -> showModesSheet());

        populateHome();

        if (startKey != null) {
            startSession(startKey);
        } else {
            showPanel(homePanel);
        }

        btnStartSession.setOnClickListener(v -> startSession(currentModeKey));
        btnStopSession.setOnClickListener(v -> stopSession());
    }

    private BreathingFeatBreathMode buildCustomMode() {
        return new BreathingFeatBreathMode("custom", "تنفس مخصص", "اضغط لإنشاء نمط التنفس الخاص بك", 2,
                4, 2, 4, 2, 0xFFF1ECFA, 0xFFDCCDF2, 0xFF6B4F9E, 0xFFB28FDB);
    }

    private void bindViews() {
        homePanel = findViewById(R.id.homePanel);
        sessionPanel = findViewById(R.id.sessionPanel);
        celebratePanel = findViewById(R.id.celebratePanel);

        homeStreakPill = findViewById(R.id.homeStreakPill);
        homeModeInfo = findViewById(R.id.homeModeInfo);
        homeModeDesc = findViewById(R.id.homeModeDesc);
        homeGoal = findViewById(R.id.homeGoal);
        homeQuote = findViewById(R.id.homeQuote);
        btnStartSession = findViewById(R.id.btnStartSession);
        homeCircle = findViewById(R.id.homeCircle);

        sessionCircle = findViewById(R.id.sessionCircle);
        sessionCycleText = findViewById(R.id.sessionCycleText);
        sessionPercentText = findViewById(R.id.sessionPercentText);
        sessionProgressBar = findViewById(R.id.sessionProgressBar);
        btnStopSession = findViewById(R.id.btnStopSession);

        modeIconBadge = findViewById(R.id.modeIconBadge);
        modeNameText = findViewById(R.id.modeNameText);
        btnSoundToggle = findViewById(R.id.btnSoundToggle);

        statMinutes = findViewById(R.id.statMinutes);
        statCycles = findViewById(R.id.statCycles);
        statStreak = findViewById(R.id.statStreak);
    }

    private void applyTheme() {
        int textColor = getColor(R.color.green_deep);
        int subColor = getColor(R.color.hub_sub);
        homeCircle.setColors(textColor, subColor, Color.WHITE, 0xFFEEF8F0);
        sessionCircle.setColors(textColor, subColor, Color.WHITE, 0xFFEEF8F0);
    }

    private BreathingFeatBreathMode currentMode() {
        return "custom".equals(currentModeKey) ? customMode : modes.get(currentModeKey);
    }

    private void populateHome() {
        BreathingFeatBreathMode m = currentMode();
        modeNameText.setText(m.name);
        modeIconBadge.setText(glyphFor(m.key));
        modeIconBadge.getBackground().mutate().setTint(m.iconColor);

        homeModeInfo.setText("النمط: " + m.name + " (" + m.ratioLabel() + ")");
        homeModeDesc.setText(m.desc);

        int cycles = computeTotalCycles(m);
        String lengthType = prefs.getLengthType();
        homeGoal.setText("🎯 الهدف: " + ("cycles".equals(lengthType)
                ? (prefs.getCyclesTarget() + " دورة")
                : (m.minutes + " دقائق (" + cycles + " دورة)")));

        homeQuote.setText("\"" + QUOTES[random.nextInt(QUOTES.length)] + "\"");
        btnStartSession.setText(START_PHRASES[random.nextInt(START_PHRASES.length)]);

        List<BreathingFeatBreathMode.Phase> previewSteps = m.buildSteps();
        BreathingFeatBreathMode.Phase first = previewSteps.get(0);
        homeCircle.setIdle(true);
        homeCircle.setRingColor(0xFF6FAE6F);
        homeCircle.setCenterText(String.valueOf(first.seconds), phaseLabel(first.key));

        int streak = prefs.getStreak();
        if (streak > 0) {
            homeStreakPill.setVisibility(View.VISIBLE);
            homeStreakPill.setText("🔥 " + streak + (streak == 1 ? " يوم متتالي" : " أيام متتالية"));
        } else {
            homeStreakPill.setVisibility(View.GONE);
        }
    }

    private int computeTotalCycles(BreathingFeatBreathMode m) {
        if ("cycles".equals(prefs.getLengthType())) return Math.max(1, prefs.getCyclesTarget());
        int pattern = Math.max(1, m.patternTotalSeconds());
        return Math.max(1, Math.round((m.minutes * 60f) / pattern));
    }

    private String phaseLabel(String key) {
        switch (key) {
            case "in": return "شهيق";
            case "out": return "زفير";
            default: return "حبس";
        }
    }

    private float phaseTone(String key) {
        switch (key) {
            case "in": return 330f;
            case "hold1": return 392f;
            case "out": return 262f;
            default: return 220f;
        }
    }

    private String glyphFor(String key) {
        switch (key) {
            case "equal": return "≋";
            case "box": return "▢";
            case "relax478": return "☾";
            case "calm711": return "☁";
            default: return "✦";
        }
    }

    private int ringColorForPhase(String key) {
        switch (key) {
            case "in": return 0xFF6FAE6F;
            case "hold1": return 0xFF8BC98F;
            case "out": return 0xFF4F9D6C;
            default: return 0xFFA9C98A;
        }
    }

    // ---------------- session flow ----------------

    private void startSession(String modeKey) {
        currentModeKey = modeKey;
        BreathingFeatBreathMode m = currentMode();
        steps = m.buildSteps();
        phaseIndex = 0;
        cycle = 1;
        totalCycles = computeTotalCycles(m);

        modeNameText.setText(m.name);
        modeIconBadge.setText(glyphFor(m.key));
        modeIconBadge.getBackground().mutate().setTint(m.iconColor);

        sessionCircle.setIdle(false);
        showPanel(sessionPanel);
        running = true;
        phaseStartElapsed = SystemClock.elapsedRealtime();
        handler.post(tickRunnable);
    }

    private void stopSession() {
        running = false;
        handler.removeCallbacks(tickRunnable);
        showPanel(homePanel);
        populateHome();
    }

    private void onTick() {
        if (!running || steps == null) return;
        BreathingFeatBreathMode.Phase step = steps.get(phaseIndex);
        long elapsedMs = SystemClock.elapsedRealtime() - phaseStartElapsed;
        long durMs = step.seconds * 1000L;
        float progress = Math.min(1f, elapsedMs / (float) durMs);

        updateSessionUi(step, progress);

        if (progress >= 1f) {
            advancePhase();
        } else {
            handler.postDelayed(tickRunnable, 30);
        }
    }

    private void updateSessionUi(BreathingFeatBreathMode.Phase step, float progress) {
        int secondsLeft = Math.max(1, (int) Math.ceil(step.seconds - progress * step.seconds));
        int ringColor = ringColorForPhase(step.key);
        float growth;
        switch (step.key) {
            case "in": growth = progress; break;
            case "hold1": growth = 1f; break;
            case "out": growth = 1f - progress; break;
            default: growth = 0f; break;
        }
        sessionCircle.setRingColor(ringColor);
        sessionCircle.setProgress(progress);
        sessionCircle.setGrowth(growth);
        sessionCircle.setCenterText(String.valueOf(secondsLeft), phaseLabel(step.key));

        sessionCycleText.setText("دورة " + cycle + " من " + totalCycles);

        BreathingFeatBreathMode m = currentMode();
        int patternTotal = Math.max(1, m.patternTotalSeconds());
        int cumBefore = 0;
        for (int i = 0; i < phaseIndex; i++) cumBefore += steps.get(i).seconds;
        float elapsedSessionSec = (cycle - 1) * patternTotal + cumBefore + progress * step.seconds;
        float overall = Math.min(1f, elapsedSessionSec / (totalCycles * (float) patternTotal));
        int pct = Math.round(overall * 100);
        sessionPercentText.setText(pct + "%");
        sessionProgressBar.setProgress(pct);
    }

    private void advancePhase() {
        BreathingFeatBreathMode.Phase justFinished = steps.get(phaseIndex);
        int nextIndex = phaseIndex + 1;
        if (nextIndex >= steps.size()) {
            if (cycle >= totalCycles) {
                finishSession();
                return;
            }
            vibrate(60);
            cycle++;
            phaseIndex = 0;
            if (soundOn) chime.playPhaseTone(phaseTone(steps.get(0).key));
        } else {
            phaseIndex = nextIndex;
            if (soundOn) chime.playPhaseTone(phaseTone(steps.get(phaseIndex).key));
        }
        phaseStartElapsed = SystemClock.elapsedRealtime();
        handler.postDelayed(tickRunnable, 16);
    }

    private void finishSession() {
        running = false;
        vibrate(new long[]{0, 100, 60, 100, 60, 220}, -1);
        if (soundOn) chime.playSummit();

        // persist completion + streak, mirrors the JS localStorage logic
        int newStreak = prefs.registerFullCompletion();
        Set<String> completedToday = prefs.getCompletedToday();
        completedToday.add(currentModeKey);
        prefs.setCompletedToday(completedToday);

        BreathingFeatBreathMode m = currentMode();
        int patternTotal = Math.max(1, m.patternTotalSeconds());
        float minutes = Math.round((totalCycles * patternTotal) / 6f) / 10f;

        statMinutes.setText("⏱\n" + minutes + "\nدقيقة");
        statCycles.setText("🔁\n" + totalCycles + "\nدورة");
        statStreak.setText("🔥\n" + newStreak + "\n" + (newStreak == 1 ? "يوم" : "أيام"));

        showPanel(celebratePanel);
        handler.postDelayed(this::finish, 2800);
    }

    private void showPanel(View panel) {
        homePanel.setVisibility(panel == homePanel ? View.VISIBLE : View.GONE);
        sessionPanel.setVisibility(panel == sessionPanel ? View.VISIBLE : View.GONE);
        celebratePanel.setVisibility(panel == celebratePanel ? View.VISIBLE : View.GONE);
    }

    private void vibrate(long ms) {
        vibrate(new long[]{0, ms}, -1);
    }

    private void vibrate(long[] pattern, int repeat) {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) v.vibrate(pattern, repeat);
    }

    // ---------------- modes / settings bottom sheet ----------------

    private void showModesSheet() {
        Dialog dialog = new Dialog(this);
        View content = LayoutInflater.from(this).inflate(R.layout.breathing_feat_dialog_modes_settings, null);
        dialog.setContentView(content);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        LinearLayout modesContainer = content.findViewById(R.id.modesContainer);
        LinearLayout settingsContainer = content.findViewById(R.id.settingsContainer);
        TextView tabModes = content.findViewById(R.id.tabModes);
        TextView tabSettings = content.findViewById(R.id.tabSettings);

        List<BreathingFeatBreathMode> allModes = new ArrayList<>(modes.values());
        allModes.add(customMode);
        for (BreathingFeatBreathMode m : allModes) {
            modesContainer.addView(buildModeCard(m, dialog));
        }

        tabModes.setOnClickListener(v -> {
            modesContainer.setVisibility(View.VISIBLE);
            settingsContainer.setVisibility(View.GONE);
            tabModes.setBackgroundResource(R.drawable.bg_pill_accent);
            tabModes.setTextColor(Color.WHITE);
            tabSettings.setBackgroundResource(R.drawable.bg_hub_item);
            tabSettings.setTextColor(getColor(R.color.hub_sub));
        });
        tabSettings.setOnClickListener(v -> {
            modesContainer.setVisibility(View.GONE);
            settingsContainer.setVisibility(View.VISIBLE);
            tabSettings.setBackgroundResource(R.drawable.bg_pill_accent);
            tabSettings.setTextColor(Color.WHITE);
            tabModes.setBackgroundResource(R.drawable.bg_hub_item);
            tabModes.setTextColor(getColor(R.color.hub_sub));
        });

        setupSettingsTab(content);
        dialog.show();
    }

    private View buildModeCard(BreathingFeatBreathMode m, Dialog dialog) {
        View card = LayoutInflater.from(this).inflate(R.layout.breathing_feat_item_mode_card, null);
        TextView icon = card.findViewById(R.id.modeCardIcon);
        TextView name = card.findViewById(R.id.modeCardName);
        TextView desc = card.findViewById(R.id.modeCardDesc);
        TextView ratio = card.findViewById(R.id.modeCardRatio);
        TextView start = card.findViewById(R.id.modeCardStart);
        TextView duration = card.findViewById(R.id.modeCardDuration);

        icon.setText(glyphFor(m.key));
        icon.getBackground().mutate().setTint(m.iconColor);
        name.setText(m.name);
        desc.setText(m.desc);
        ratio.setText(m.ratioLabel());
        String lengthType = prefs.getLengthType();
        duration.setText("⏱ " + ("cycles".equals(lengthType) ? (prefs.getCyclesTarget() + " دورة") : (m.minutes + " دقائق")));

        card.setOnClickListener(v -> {
            currentModeKey = m.key;
            populateHome();
            dialog.dismiss();
        });
        start.setOnClickListener(v -> {
            dialog.dismiss();
            startSession(m.key);
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(lp);
        return card;
    }

    private void setupSettingsTab(View content) {
        Switch switchDark = content.findViewById(R.id.switchDark);
        Switch switchReminder = content.findViewById(R.id.switchReminder);
        TextView reminderTimeText = content.findViewById(R.id.reminderTimeText);
        TextView btnLengthMinutes = content.findViewById(R.id.btnLengthMinutes);
        TextView btnLengthCycles = content.findViewById(R.id.btnLengthCycles);
        LinearLayout cyclesTargetRow = content.findViewById(R.id.cyclesTargetRow);
        TextView cyclesTargetText = content.findViewById(R.id.cyclesTargetText);
        TextView btnCyclesMinus = content.findViewById(R.id.btnCyclesMinus);
        TextView btnCyclesPlus = content.findViewById(R.id.btnCyclesPlus);

        switchDark.setChecked(prefs.isDark());
        switchDark.setOnCheckedChangeListener((CompoundButton b, boolean checked) -> prefs.setDark(checked));

        switchReminder.setChecked(prefs.isReminderOn());
        reminderTimeText.setVisibility(prefs.isReminderOn() ? View.VISIBLE : View.GONE);
        reminderTimeText.setText("وقت التذكير: " + prefs.getReminderTime());
        switchReminder.setOnCheckedChangeListener((CompoundButton b, boolean checked) -> {
            prefs.setReminderOn(checked);
            reminderTimeText.setVisibility(checked ? View.VISIBLE : View.GONE);
        });
        reminderTimeText.setOnClickListener(v -> {
            String[] parts = prefs.getReminderTime().split(":");
            int h = Integer.parseInt(parts[0]), min = Integer.parseInt(parts[1]);
            new android.app.TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
                String t = String.format("%02d:%02d", hourOfDay, minute);
                prefs.setReminderTime(t);
                reminderTimeText.setText("وقت التذكير: " + t);
            }, h, min, true).show();
        });

        boolean isCycles = "cycles".equals(prefs.getLengthType());
        setLengthToggleUi(btnLengthMinutes, btnLengthCycles, !isCycles);
        cyclesTargetRow.setVisibility(isCycles ? View.VISIBLE : View.GONE);
        cyclesTargetText.setText(String.valueOf(prefs.getCyclesTarget()));

        btnLengthMinutes.setOnClickListener(v -> {
            prefs.setLengthType("minutes");
            setLengthToggleUi(btnLengthMinutes, btnLengthCycles, true);
            cyclesTargetRow.setVisibility(View.GONE);
            populateHome();
        });
        btnLengthCycles.setOnClickListener(v -> {
            prefs.setLengthType("cycles");
            setLengthToggleUi(btnLengthMinutes, btnLengthCycles, false);
            cyclesTargetRow.setVisibility(View.VISIBLE);
            populateHome();
        });
        btnCyclesMinus.setOnClickListener(v -> {
            int val = Math.max(1, prefs.getCyclesTarget() - 1);
            prefs.setCyclesTarget(val);
            cyclesTargetText.setText(String.valueOf(val));
            populateHome();
        });
        btnCyclesPlus.setOnClickListener(v -> {
            int val = Math.min(60, prefs.getCyclesTarget() + 1);
            prefs.setCyclesTarget(val);
            cyclesTargetText.setText(String.valueOf(val));
            populateHome();
        });
    }

    private void setLengthToggleUi(TextView minutesBtn, TextView cyclesBtn, boolean minutesActive) {
        minutesBtn.setBackgroundResource(minutesActive ? R.drawable.bg_pill_accent : R.drawable.bg_hub_item);
        minutesBtn.setTextColor(minutesActive ? Color.WHITE : getColor(R.color.hub_sub));
        cyclesBtn.setBackgroundResource(!minutesActive ? R.drawable.bg_pill_accent : R.drawable.bg_hub_item);
        cyclesBtn.setTextColor(!minutesActive ? Color.WHITE : getColor(R.color.hub_sub));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        handler.removeCallbacks(tickRunnable);
    }
}
