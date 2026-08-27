package com.example.graduationproject;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.graduationproject.view.FaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Port of AdultMoodScreen.jsx.
 *
 * Behaviour kept identical to the React version:
 *  - 7 moods, default selection = index 3 ("neutral")
 *  - tapping a mood animates: full-screen background colour, hero circle
 *    colour, progress-bar fill colour, and the selector chip highlight
 *  - the continue button stays a fixed white pill regardless of mood
 *    (this was intentional in the original design, for contrast)
 */
public class AdultMoodActivity extends AppCompatActivity {

    private LinearLayout rootLayout;
    private View progressFill;
    private FrameLayout heroCircle;
    private FaceView heroFace;
    private TextView moodLabel;
    private LinearLayout selectorRow;

    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(AppLanguageManager.wrapContext(newBase));
    }

    private final List<com.example.graduationproject.models.Mood> moods = new ArrayList<>();
    private final List<View> chipViews = new ArrayList<>();
    private int selectedIndex = 3; // "neutral" — same default as the React version

    private static final long BG_ANIM_MS = 500;   // matches CSS "transition: background 0.5s ease"
    private static final long CHIP_ANIM_MS = 200;  // matches CSS "transition: all 0.2s ease"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adult_mood);

        rootLayout = findViewById(R.id.root_layout);
        progressFill = findViewById(R.id.progress_fill);
        heroCircle = findViewById(R.id.hero_circle);
        heroFace = findViewById(R.id.hero_face);
        moodLabel = findViewById(R.id.mood_label);
        selectorRow = findViewById(R.id.selector_row);

        buildMoods();
        buildSelectorChips();
        renderInstant(selectedIndex);

        findViewById(R.id.btn_continue).setOnClickListener(v -> {
            // Save selected mood for Home screen
            com.example.graduationproject.models.Mood mood = moods.get(selectedIndex);
            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
                    .putString("today_mood_id", mood.id)
                    .putInt("today_mood_color", mood.circleColor)
                    .apply();

            startActivity(new Intent(this, ReflectionActivity.class));
        });
    }

    /** Same 7 moods / colours as ADULT_MOODS in the React source. */
    private void buildMoods() {
        String[] ids = {"awful", "sad", "low", "neutral", "calm", "happy", "overjoyed"};
        String[] faces = ids; // face type strings match the ids 1:1
        int[] labelRes = {
                R.string.mood_awful, R.string.mood_sad, R.string.mood_low,
                R.string.mood_neutral, R.string.mood_calm, R.string.mood_happy, R.string.mood_overjoyed
        };
        String[] bg =      {"#F8DCDA", "#FBE3CE", "#EFE3D2", "#EAEEF3", "#DEF3EF", "#FCF0C6", "#DEF3E0"};
        String[] circle =  {"#F0AFA9", "#F3C495", "#D9C39B", "#C6D6E2", "#A2DBCF", "#F3DA80", "#A3DDA8"};
        String[] accent =  {"#D9695F", "#DC9142", "#A47F4C", "#5C7A93", "#2E9884", "#CE9C15", "#3C9E47"};

        for (int i = 0; i < ids.length; i++) {
            moods.add(new com.example.graduationproject.models.Mood(ids[i], getString(labelRes[i]), faces[i],
                    Color.parseColor(bg[i]), Color.parseColor(circle[i]), Color.parseColor(accent[i])));
        }
    }

    /** Builds the horizontal chip row once; each chip hosts its own small FaceView. */
    private void buildSelectorChips() {
        int dp36 = dp(36), dp42 = dp(42);
        for (int i = 0; i < moods.size(); i++) {
            final int index = i;
            com.example.graduationproject.models.Mood mood = moods.get(i);

            FrameLayout chip = new FrameLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp42, dp42);
            lp.setMargins(dp(2), 0, dp(2), 0);
            chip.setLayoutParams(lp);

            FaceView icon = new FaceView(this);
            FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER);
            icon.setLayoutParams(iconLp);
            icon.setMoodType(mood.faceType);

            chip.addView(icon);
            chip.setOnClickListener(v -> selectMood(index));

            selectorRow.addView(chip);
            chipViews.add(chip);
        }
    }

    /** Keeps the status and navigation bars the same colour as the screen background. */
    private void applySystemBarColors(int color) {
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);

        // All 7 mood backgrounds are light, so use dark icons for contrast.
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);
    }

    /** Draws the initial state with no animation (used once, on launch). */
    private void renderInstant(int index) {
        com.example.graduationproject.models.Mood mood = moods.get(index);
        rootLayout.setBackgroundColor(mood.bgColor);
        progressFill.setBackground(ovalOrRect(mood.accentColor, false));
        heroFace.setMoodType(mood.faceType);
        moodLabel.setText(mood.label);
        heroCircle.setBackground(ovalOrRect(mood.circleColor, true));
        updateChipStyles(index, false);
        applySystemBarColors(mood.bgColor);
    }

    /** User tapped a new mood chip — animate everything to the new mood. */
    private void selectMood(int newIndex) {
        if (newIndex == selectedIndex) return;
        com.example.graduationproject.models.Mood from = moods.get(selectedIndex);
        com.example.graduationproject.models.Mood to = moods.get(newIndex);
        selectedIndex = newIndex;

        animateColor(from.bgColor, to.bgColor, rootLayout::setBackgroundColor);
        animateColor(from.bgColor, to.bgColor, this::applySystemBarColors);
        animateColor(from.accentColor, to.accentColor,
                c -> progressFill.setBackground(ovalOrRect(c, false)));
        animateColor(from.circleColor, to.circleColor,
                c -> heroCircle.setBackground(ovalOrRect(c, true)));

        moodLabel.setText(to.label);
        bounceFace(to.faceType);
        updateChipStyles(newIndex, true);
    }

    /** Small bounce/scale transition on the hero face when it swaps expression. */
    private void bounceFace(String newFaceType) {
        heroFace.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100)
                .withEndAction(() -> {
                    heroFace.setMoodType(newFaceType);
                    heroFace.animate().scaleX(1f).scaleY(1f).setDuration(150)
                            .setInterpolator(new DecelerateInterpolator()).start();
                }).start();
    }

    private void updateChipStyles(int selected, boolean animate) {
        for (int i = 0; i < chipViews.size(); i++) {
            FrameLayout chip = (FrameLayout) chipViews.get(i);
            FaceView icon = (FaceView) chip.getChildAt(0);
            boolean isSelected = (i == selected);
            int targetSize = dp(isSelected ? 42 : 36);
            float targetAlpha = isSelected ? 1f : 0.45f;
            int targetIconColor = isSelected ? Color.WHITE : 0xFF26324A;

            if (animate) {
                chip.animate().alpha(targetAlpha).setDuration(CHIP_ANIM_MS).start();
                ValueAnimator sizeAnim = ValueAnimator.ofInt(chip.getLayoutParams().width, targetSize);
                sizeAnim.setDuration(CHIP_ANIM_MS);
                sizeAnim.addUpdateListener(a -> {
                    int val = (int) a.getAnimatedValue();
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) chip.getLayoutParams();
                    lp.width = val;
                    lp.height = val;
                    chip.setLayoutParams(lp);
                });
                sizeAnim.start();
            } else {
                chip.setAlpha(targetAlpha);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) chip.getLayoutParams();
                lp.width = targetSize;
                lp.height = targetSize;
                chip.setLayoutParams(lp);
            }
            icon.setLineColor(targetIconColor);
            chip.setBackground(isSelected ? getDrawable(R.drawable.bg_chip_selected) : null);
            chip.setElevation(isSelected ? dp(3) : 0);
        }
    }

    private void animateColor(int from, int to, ColorConsumer consumer) {
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        anim.setDuration(BG_ANIM_MS);
        anim.addUpdateListener(a -> consumer.accept((int) a.getAnimatedValue()));
        anim.start();
    }

    /** A simple oval (for the hero circle) or plain rect (for the progress fill) drawable. */
    private GradientDrawable ovalOrRect(int color, boolean oval) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(oval ? GradientDrawable.OVAL : GradientDrawable.RECTANGLE);
        if (!oval) d.setCornerRadius(dp(10));
        d.setColor(color);
        return d;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    private interface ColorConsumer {
        void accept(int color);
    }
}
