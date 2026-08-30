package com.example.graduationproject;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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

import com.example.graduationproject.models.KidsMood;
import com.example.graduationproject.view.KidsMoodBearView;

import java.util.ArrayList;
import java.util.List;

/**
 * Port of KidsMoodScreen.jsx — teddy-bear mood tracker for kids.
 * Same reactive full-screen background behaviour as the adult screen,
 * just with the BearView character and the kids' colour palette / copy.
 */
public class KidsMoodActivity extends AppCompatActivity {

    private LinearLayout rootLayout;
    private View progressFill;
    private FrameLayout heroCircle;
    private KidsMoodBearView heroBear;
    private TextView moodLabel;
    private LinearLayout selectorRow;

    private final List<KidsMood> moods = new ArrayList<>();
    private final List<View> chipViews = new ArrayList<>();
    private int selectedIndex = 3; // "neutral" default, same as the React version

    private static final long BG_ANIM_MS = 500;
    private static final long CHIP_ANIM_MS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kids_mood);

        rootLayout = findViewById(R.id.root_layout);
        progressFill = findViewById(R.id.progress_fill);
        heroCircle = findViewById(R.id.hero_circle);
        heroBear = findViewById(R.id.hero_bear);
        moodLabel = findViewById(R.id.mood_label);
        selectorRow = findViewById(R.id.selector_row);

        buildMoods();
        buildSelectorChips();
        renderInstant(selectedIndex);

        findViewById(R.id.btn_continue).setOnClickListener(v ->
                Toast.makeText(this, R.string.kids_mood_saved_toast, Toast.LENGTH_SHORT).show());
    }

    /** Same 7 moods / colours as KID_MOODS in the React source. */
    private void buildMoods() {
        String[] ids = {"awful", "sad", "low", "neutral", "calm", "happy", "overjoyed"};
        int[] labelRes = {
                R.string.kids_mood_awful, R.string.kids_mood_sad, R.string.kids_mood_low,
                R.string.kids_mood_neutral, R.string.kids_mood_calm, R.string.kids_mood_happy, R.string.kids_mood_overjoyed
        };
        String[] bg =     {"#F8DCDA", "#FCE6D2", "#EFE3D2", "#E6F3F6", "#DFF6F0", "#FDF3C9", "#E0F7E3"};
        String[] accent = {"#D9695F", "#DC9142", "#A47F4C", "#4C93AC", "#2E9884", "#CE9C15", "#3C9E47"};

        for (int i = 0; i < ids.length; i++) {
            moods.add(new KidsMood(ids[i], getString(labelRes[i]), ids[i],
                    Color.parseColor(bg[i]), Color.parseColor(accent[i])));
        }
    }

    private void buildSelectorChips() {
        int dp38 = dp(38), dp46 = dp(46);
        for (int i = 0; i < moods.size(); i++) {
            final int index = i;
            KidsMood mood = moods.get(i);

            FrameLayout chip = new FrameLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp46, dp46);
            lp.setMargins(dp(2), dp(4), dp(2), dp(4));
            chip.setLayoutParams(lp);
            chip.setClipChildren(false);
            chip.setClipToPadding(false);

            KidsMoodBearView icon = new KidsMoodBearView(this);
            FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(dp(30), dp(30), Gravity.CENTER);
            icon.setLayoutParams(iconLp);
            icon.setMoodType(mood.bearType);

            chip.addView(icon);
            chip.setOnClickListener(v -> selectMood(index));

            selectorRow.addView(chip);
            chipViews.add(chip);
        }
    }

    private void renderInstant(int index) {
        KidsMood mood = moods.get(index);
        rootLayout.setBackgroundColor(mood.bgColor);
        getWindow().setStatusBarColor(mood.bgColor);
        progressFill.setBackground(ovalOrRect(mood.accentColor, false));
        heroBear.setMoodType(mood.bearType);
        moodLabel.setText(mood.label);
        updateChipStyles(index, false);
    }

    private void selectMood(int newIndex) {
        if (newIndex == selectedIndex) return;
        KidsMood from = moods.get(selectedIndex);
        KidsMood to = moods.get(newIndex);
        selectedIndex = newIndex;

        animateColor(from.bgColor, to.bgColor,
                c -> {
                    rootLayout.setBackgroundColor(c);
                    getWindow().setStatusBarColor(c);
                });
        animateColor(from.accentColor, to.accentColor,
                c -> progressFill.setBackground(ovalOrRect(c, false)));

        moodLabel.setText(to.label);
        bounceBear(to.bearType);
        updateChipStyles(newIndex, true);
    }

    private void bounceBear(String newType) {
        heroBear.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100)
                .withEndAction(() -> {
                    heroBear.setMoodType(newType);
                    heroBear.animate().scaleX(1f).scaleY(1f).setDuration(150)
                            .setInterpolator(new DecelerateInterpolator()).start();
                }).start();
    }

    private void updateChipStyles(int selected, boolean animate) {
        for (int i = 0; i < chipViews.size(); i++) {
            View chip = chipViews.get(i);
            boolean isSelected = (i == selected);
            int targetSize = dp(isSelected ? 46 : 38);
            float targetAlpha = isSelected ? 1f : 0.45f;

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
            chip.setBackground(isSelected ? getDrawable(R.drawable.bg_chip_selected) : null);
            chip.setElevation(isSelected ? dp(4) : 0);
        }
    }

    private void animateColor(int from, int to, ColorConsumer consumer) {
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        anim.setDuration(BG_ANIM_MS);
        anim.addUpdateListener(a -> consumer.accept((int) a.getAnimatedValue()));
        anim.start();
    }

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
