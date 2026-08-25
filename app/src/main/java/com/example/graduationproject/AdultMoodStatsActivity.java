package com.example.graduationproject;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.ui.AdultMoodResult;
import com.example.graduationproject.widget.AdultChartView;
import com.example.graduationproject.widget.AdultFaceView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Port of MoodStatsScreen.jsx:
 *  - Day / Week / Month segmented control with a sliding white indicator
 *    (mirrors the "Reading goal" style pill toggle the user referenced)
 *  - interactive chart: drag/scrub anywhere to inspect any point, exactly
 *    like the currency chart reference — release to snap back to "today"
 */
public class AdultMoodStatsActivity extends AppCompatActivity {

    private static class Range {
        final float[] scores;
        final String[] labels;
        Range(float[] scores, String[] labels) { this.scores = scores; this.labels = labels; }
    }

    private final Map<String, Range> ranges = new LinkedHashMap<>();
    private String currentRange = "week";

    private View segmentIndicator;
    private LinearLayout segmentButtons;
    private AdultChartView chartView;
    private AdultFaceView scrubFace;
    private TextView scrubLabel, scrubSub, tabDay, tabWeek, tabMonth;

    private static final long TAB_ANIM_MS = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adult_mood_stats);

        segmentIndicator = findViewById(R.id.segment_indicator);
        segmentButtons = findViewById(R.id.segment_buttons);
        chartView = findViewById(R.id.chart_view);
        scrubFace = findViewById(R.id.scrub_face);
        scrubLabel = findViewById(R.id.scrub_label);
        scrubSub = findViewById(R.id.scrub_sub);
        tabDay = findViewById(R.id.tab_day);
        tabWeek = findViewById(R.id.tab_week);
        tabMonth = findViewById(R.id.tab_month);

        buildRanges();

        tabDay.setOnClickListener(v -> switchRange("day", tabDay));
        tabWeek.setOnClickListener(v -> switchRange("week", tabWeek));
        tabMonth.setOnClickListener(v -> switchRange("month", tabMonth));

        chartView.setListener((shownIndex, isScrubbing) -> {
            Range r = ranges.get(currentRange);
            if (r == null || shownIndex < 0 || shownIndex >= r.scores.length) return;
            AdultMoodResult mood = AdultMoodResult.from(r.scores[shownIndex], this);
            scrubFace.setMoodType(mood.face);
            scrubLabel.setText(mood.label);
            scrubLabel.setTextColor(mood.color);
            scrubSub.setText("· " + r.labels[shownIndex]);
            chartView.setDotHighlightColor(mood.color);
        });

        // wait for layout to know real pixel widths before placing the indicator
        segmentButtons.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        segmentButtons.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        moveIndicatorTo(tabWeek, false);
                        chartView.setData(ranges.get(currentRange).scores, ranges.get(currentRange).labels);
                    }
                });
    }

    /** Same three datasets as RANGES in the React source. */
    private void buildRanges() {
        ranges.put("day", new Range(
                new float[]{3.2f, 3.6f, 2.8f, 4.0f, 4.4f, 3.9f, 4.6f, 4.1f},
                new String[]{"١٢ص", "٣ص", "٦ص", "٩ص", "١٢م", "٣م", "٦م", "٩م"}));
        ranges.put("week", new Range(
                new float[]{3.8f, 4.2f, 2.6f, 3.1f, 4.5f, 4.8f, 3.9f},
                new String[]{
                        getString(R.string.adult_stats_day_sun), getString(R.string.adult_stats_day_mon), getString(R.string.adult_stats_day_tue),
                        getString(R.string.adult_stats_day_wed), getString(R.string.adult_stats_day_thu), getString(R.string.adult_stats_day_fri),
                        getString(R.string.adult_stats_day_sat)}));
        ranges.put("month", new Range(
                new float[]{2.4f, 3.6f, 1.8f, 4.2f, 3.1f},
                new String[]{"أسبوع ١", "أسبوع ٢", "أسبوع ٣", "أسبوع ٤", "أسبوع ٥"}));
    }

    private void switchRange(String key, TextView target) {
        if (key.equals(currentRange)) return;
        currentRange = key;
        moveIndicatorTo(target, true);
        Range r = ranges.get(key);
        chartView.setData(r.scores, r.labels);

        tabDay.setTextColor(Color.parseColor(key.equals("day") ? "#0F172A" : "#8598AC"));
        tabWeek.setTextColor(Color.parseColor(key.equals("week") ? "#0F172A" : "#8598AC"));
        tabMonth.setTextColor(Color.parseColor(key.equals("month") ? "#0F172A" : "#8598AC"));
    }

    /** Slides + resizes the white indicator to sit exactly under the target tab. */
    private void moveIndicatorTo(TextView target, boolean animate) {
        float toX = target.getLeft();
        int toWidth = target.getWidth();

        if (!animate) {
            segmentIndicator.setX(toX);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) segmentIndicator.getLayoutParams();
            lp.width = toWidth;
            segmentIndicator.setLayoutParams(lp);
            return;
        }

        float fromX = segmentIndicator.getX();
        int fromWidth = segmentIndicator.getWidth();
        PathInterpolator interpolator = new PathInterpolator(0.22f, 1f, 0.36f, 1f);

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(TAB_ANIM_MS);
        anim.setInterpolator(interpolator);
        anim.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            segmentIndicator.setX(fromX + (toX - fromX) * f);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) segmentIndicator.getLayoutParams();
            lp.width = (int) (fromWidth + (toWidth - fromWidth) * f);
            segmentIndicator.setLayoutParams(lp);
        });
        anim.start();
    }
}
