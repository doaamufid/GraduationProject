
package com.example.graduationproject;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.graduationproject.ui.AdultMoodResult;
import com.example.graduationproject.widget.AdultChartView;
import com.example.graduationproject.widget.AdultFaceView;
import com.example.graduationproject.data.profile.ArabicDateUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reports & Stats Screen.
 * Contains Mood Stats, Activity Stats, Personal Archive, and Children's Reports.
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

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

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

        segmentButtons.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        segmentButtons.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        moveIndicatorTo(tabWeek, false);
                        chartView.setData(ranges.get(currentRange).scores, ranges.get(currentRange).labels);
                    }
                });

        renderStats();
        renderArchiveLinks();

        findViewById(R.id.btn_children_link).setOnClickListener(v -> {
            // Open AdultProfileActivity and navigate to children
            Intent intent = new Intent(this, AdultProfileActivity.class);
            intent.putExtra("navigate_to", "children");
            startActivity(intent);
        });
    }

    private void renderStats() {
        LinearLayout statsRow = findViewById(R.id.stats_row);
        statsRow.removeAllViews();
        String[][] stats = { 
                { ArabicDateUtils.toAr(47), getString(R.string.stat_days_active) },
                { ArabicDateUtils.toAr(12), getString(R.string.stat_streak) },
                { ArabicDateUtils.toAr(69), getString(R.string.stat_sessions) } 
        };

        for (String[] stat : stats) {
            View box = LayoutInflater.from(this).inflate(R.layout.item_stat_box, statsRow, false);
            ((TextView) box.findViewById(R.id.txt_stat_number)).setText(stat[0]);
            ((TextView) box.findViewById(R.id.txt_stat_label)).setText(stat[1]);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) box.getLayoutParams();
            lp.width = 0;
            lp.weight = 1;
            lp.setMarginEnd(dp(6));
            box.setLayoutParams(lp);
            statsRow.addView(box);
        }
    }

    private void renderArchiveLinks() {
        LinearLayout container = findViewById(R.id.archive_links_container);
        container.removeAllViews();

        Object[][] links = {
                { "thoughts", R.string.link_thoughts_label, R.string.link_thoughts_sub, R.drawable.ic_pen_line, R.color.primary },
                { "strengths", R.string.link_strengths_label, R.string.link_strengths_sub, R.drawable.ic_heart, R.color.pink },
                { "messages", R.string.link_messages_label, R.string.link_messages_sub, R.drawable.ic_mail, R.color.purple },
        };

        for (Object[] link : links) {
            final String key = (String) link[0];
            int labelRes = (int) link[1];
            int subRes = (int) link[2];
            int iconRes = (int) link[3];
            int colorRes = (int) link[4];

            View row = LayoutInflater.from(this).inflate(R.layout.item_archive_link, container, false);
            ((TextView) row.findViewById(R.id.txt_link_label)).setText(labelRes);
            ((TextView) row.findViewById(R.id.txt_link_sub)).setText(subRes);

            ImageView iconBg = row.findViewById(R.id.img_link_icon_bg);
            iconBg.setImageResource(iconRes);
            int color = ContextCompat.getColor(this, colorRes);
            iconBg.setColorFilter(color);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setColor((color & 0x00FFFFFF) | 0x18000000);
            bg.setCornerRadius(dp(12));
            iconBg.setBackground(bg);

            row.setOnClickListener(v -> {
                animateTap(row);
                Intent intent = new Intent(this, AdultProfileActivity.class);
                intent.putExtra("navigate_to", key);
                startActivity(intent);
            });
            container.addView(row);
        }
    }

    private void animateTap(View v) {
        v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
        ).start();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

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
