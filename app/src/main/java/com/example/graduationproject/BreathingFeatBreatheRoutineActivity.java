package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.models.BreathingFeatBreathMode;
import com.example.graduationproject.util.Prefs;
import com.example.graduationproject.view.BreathingFeatRoutineProgressView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Port of the JS <RoutineHomeScreen/> + <KidsRoutineHomeScreen/> + the
 * <BreatheRoutineDemo/> wrapper that toggles between the two audiences.
 * Both audiences share the exact same data/logic; only the visual theme differs,
 * matching the original (kb-* vs rt-* CSS classes were cosmetic only).
 */
public class BreathingFeatBreatheRoutineActivity extends AppCompatActivity {

    public static final String EXTRA_MODE_KEY = "mode_key";
    private static final int REQ_SESSION = 42;

    private Prefs prefs;
    private final Map<String, BreathingFeatBreathMode> modes = BreathingFeatBreathMode.buildRoutineModes();
    private boolean kidsAudience = true;

    private TextView routineTitle, routineSub, routineStreak, btnCta, btnAudienceKids, btnAudienceAdult, btnDarkToggle;
    private BreathingFeatRoutineProgressView progressView;
    private ScrollView rootScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breathing_feat_activity_breathe_routine);
        prefs = new Prefs(this);

        rootScroll = findViewById(R.id.rootScroll);
        routineTitle = findViewById(R.id.routineTitle);
        routineSub = findViewById(R.id.routineSub);
        routineStreak = findViewById(R.id.routineStreak);
        btnCta = findViewById(R.id.btnCta);
        btnAudienceKids = findViewById(R.id.btnAudienceKids);
        btnAudienceAdult = findViewById(R.id.btnAudienceAdult);
        btnDarkToggle = findViewById(R.id.btnDarkToggle);
        progressView = findViewById(R.id.routineProgressView);

        List<BreathingFeatBreathMode> modeList = new ArrayList<>(modes.values());
        progressView.setModes(modeList);
        progressView.setOnModeClickListener(this::startExercise);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnDarkToggle.setOnClickListener(v -> {
            prefs.setDark(!prefs.isDark());
            applyTheme();
        });
        btnAudienceKids.setOnClickListener(v -> { kidsAudience = true; applyAudience(); });
        btnAudienceAdult.setOnClickListener(v -> { kidsAudience = false; applyAudience(); });

        applyAudience();
        applyTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void applyAudience() {
        int accentBg = R.drawable.bg_pill_accent;
        btnAudienceKids.setBackgroundResource(kidsAudience ? accentBg : 0);
        btnAudienceKids.setTextColor(kidsAudience ? Color.WHITE : 0xFF4F7364);
        btnAudienceAdult.setBackgroundResource(!kidsAudience ? accentBg : 0);
        btnAudienceAdult.setTextColor(!kidsAudience ? Color.WHITE : 0xFF4F7364);
        refresh();
    }

    private void applyTheme() {
        boolean dark = prefs.isDark();
        rootScroll.setBackgroundColor(dark ? 0xFF0E1A13 : getColor(R.color.breathing_feat_breathe_light_bg1));
        int textColor = dark ? 0xFFDCF3E2 : getColor(R.color.green_deep);
        int subColor = dark ? 0xFFA9CDB5 : getColor(R.color.hub_sub);
        routineTitle.setTextColor(textColor);
        routineSub.setTextColor(subColor);
        btnDarkToggle.setText(dark ? getString(R.string.breathing_feat_light_mode_toggle) : getString(R.string.breathing_feat_dark_mode_toggle));
    }

    private void refresh() {
        Set<String> completedKeys = prefs.getCompletedToday();
        Map<String, Boolean> completedMap = new LinkedHashMap<>();
        for (String key : modes.keySet()) completedMap.put(key, completedKeys.contains(key));
        progressView.setCompleted(completedMap);

        int done = completedKeys.size();
        int total = modes.size();
        boolean isComplete = done >= total;

        routineTitle.setText(isComplete ? getString(R.string.breathing_feat_routine_title_done) : getString(R.string.breathing_feat_routine_title_progress));
        routineSub.setText(done + " من " + total + " تمارين تنفس مكتملة");

        int streak = prefs.getStreak();
        if (streak > 0) {
            routineStreak.setVisibility(TextView.VISIBLE);
            routineStreak.setText("🔥 " + streak + (streak == 1 ? " يوم متتالي" : " أيام متتالية"));
        } else {
            routineStreak.setVisibility(TextView.GONE);
        }

        String nextKey = null;
        for (String key : modes.keySet()) {
            if (!completedKeys.contains(key)) { nextKey = key; break; }
        }
        if (isComplete) {
            btnCta.setText(getString(R.string.breathing_feat_redo_any));
        } else {
            btnCta.setText(getString(R.string.breathing_feat_start_next_fmt, modes.get(nextKey).name));
        }
        final String startKey = isComplete ? modeList().get(0).key : nextKey;
        btnCta.setOnClickListener(v -> startExercise(startKey));
    }

    private List<BreathingFeatBreathMode> modeList() {
        return new ArrayList<>(modes.values());
    }

    private void startExercise(String modeKey) {
        Intent intent = new Intent(this, BreathingFeatBreatheSessionActivity.class);
        intent.putExtra(EXTRA_MODE_KEY, modeKey);
        startActivityForResult(intent, REQ_SESSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Completion is persisted via Prefs directly by BreathingFeatBreatheSessionActivity;
        // onResume() will pick it up regardless of the result code.
    }
}
