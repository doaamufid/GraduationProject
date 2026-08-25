
package com.example.graduationproject.Fragments.profile;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.graduationproject.ProfileNavigator;
import com.example.graduationproject.R;
import com.example.graduationproject.data.profile.SeedData;
import com.example.graduationproject.models.profile.ChildAlert;
import com.example.graduationproject.models.profile.ChildDetail;
import com.example.graduationproject.models.profile.ChildFeature;
import com.example.graduationproject.models.profile.ChildHistoryEntry;
import com.example.graduationproject.ui.AdultMoodResult;
import com.example.graduationproject.widget.AdultChartView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Child Detail Screen with interactive bear-themed mood chart.
 */
public class ChildDetailFragment extends Fragment {

    private static final String ARG_CHILD_ID = "child_id";
    private static final long TAB_ANIM_MS = 300;

    private boolean alertOpen = false;

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
    private TextView scrubLabel, scrubSub, tabDay, tabWeek, tabMonth;

    public static ChildDetailFragment newInstance(long childId) {
        ChildDetailFragment f = new ChildDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CHILD_ID, childId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_detail, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileNavigator activity = (ProfileNavigator) requireActivity();

        long childId = getArguments() != null ? getArguments().getLong(ARG_CHILD_ID) : -1;
        ChildDetail child = SeedData.getChildDetail(requireContext(), childId);
        if (child == null) { activity.showChildren(); return; }

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> activity.showChildren());

        bindHeader(view, child);
        bindHero(view, child);
        bindStats(view, child);
        bindFeatures(view, child);
        bindAlert(view, child);
        bindRecommendations(view, child);
        bindHistory(view, child);

        initMoodChart(view, child);
    }

    private void bindHeader(View root, ChildDetail child) {
        ((TextView) root.findViewById(R.id.txt_child_title)).setText(child.name);
        ((TextView) root.findViewById(R.id.txt_child_sub)).setText(getString(R.string.age_years_fmt, child.age));
    }

    private void bindHero(View root, ChildDetail child) {
        LinearLayout heroCard = root.findViewById(R.id.hero_card);
        GradientDrawable bg = new GradientDrawable();
        bg.setOrientation(GradientDrawable.Orientation.TL_BR);
        bg.setColors(new int[]{ child.color, withAlpha(child.color, 0x99) });
        bg.setCornerRadius(24 * getResources().getDisplayMetrics().density);
        heroCard.setBackground(bg);

        ((TextView) root.findViewById(R.id.txt_hero_avatar)).setText(child.avatarEmoji);
        ((TextView) root.findViewById(R.id.txt_hero_name))
                .setText(getString(R.string.child_name_age_fmt, child.name, child.age));
        ((TextView) root.findViewById(R.id.txt_hero_last_active))
                .setText(getString(R.string.last_active_fmt, child.lastActive));
    }

    private void bindStats(View root, ChildDetail child) {
        ((TextView) root.findViewById(R.id.txt_stat_exercises)).setText(String.valueOf(child.stats.exercises));
        ((TextView) root.findViewById(R.id.txt_stat_sessions)).setText(String.valueOf(child.stats.sessions));
        ((TextView) root.findViewById(R.id.txt_stat_inactive)).setText(String.valueOf(child.stats.inactiveDays));
    }

    private void initMoodChart(View view, ChildDetail child) {
        segmentIndicator = view.findViewById(R.id.child_segment_indicator);
        segmentButtons = view.findViewById(R.id.child_segment_buttons);
        chartView = view.findViewById(R.id.child_chart_view);
        scrubLabel = view.findViewById(R.id.child_scrub_label);
        scrubSub = view.findViewById(R.id.child_scrub_sub);
        tabDay = view.findViewById(R.id.child_tab_day);
        tabWeek = view.findViewById(R.id.child_tab_week);
        tabMonth = view.findViewById(R.id.child_tab_month);

        buildRanges();

        tabDay.setOnClickListener(v -> switchRange("day", tabDay));
        tabWeek.setOnClickListener(v -> switchRange("week", tabWeek));
        tabMonth.setOnClickListener(v -> switchRange("month", tabMonth));

        chartView.setListener((shownIndex, isScrubbing) -> {
            Range r = ranges.get(currentRange);
            if (r == null || shownIndex < 0 || shownIndex >= r.scores.length) return;
            AdultMoodResult mood = AdultMoodResult.from(r.scores[shownIndex], requireContext());
            scrubLabel.setText(mood.label);
            scrubLabel.setTextColor(child.color);
            scrubSub.setText("· " + r.labels[shownIndex]);
            chartView.setDotHighlightColor(child.color);
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
        
        // Kids chart colors: Turquoise/Green
        chartView.setDotHighlightColor(child.color);
    }

    private void buildRanges() {
        ranges.put("day", new Range(
                new float[]{4.0f, 3.8f, 4.2f, 3.5f, 4.8f, 4.5f, 4.1f, 4.3f},
                new String[]{"١٢ص", "٣ص", "٦ص", "٩ص", "١٢م", "٣م", "٦م", "٩م"}));
        ranges.put("week", new Range(
                new float[]{3.2f, 4.5f, 2.8f, 4.0f, 4.6f, 3.9f, 4.4f},
                new String[]{"ح", "ن", "ث", "ر", "خ", "ج", "س"}));
        ranges.put("month", new Range(
                new float[]{3.6f, 4.1f, 3.9f, 4.4f, 4.0f},
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
        
        tabDay.setTypeface(null, key.equals("day") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        tabWeek.setTypeface(null, key.equals("week") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        tabMonth.setTypeface(null, key.equals("month") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
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

    private void bindFeatures(View root, ChildDetail child) {
        LinearLayout container = root.findViewById(R.id.features_container);
        container.removeAllViews();
        for (ChildFeature f : child.topFeatures) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_feature_row, container, false);
            TextView count = row.findViewById(R.id.txt_feature_count);
            count.setText(getString(R.string.times_fmt2, f.count));
            count.setTextColor(child.color);
            ((TextView) row.findViewById(R.id.txt_feature_label)).setText(f.label);
            ImageView icon = row.findViewById(R.id.img_feature_icon);
            icon.setImageResource(f.iconRes);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_soft));
            container.addView(row);
        }
    }

    private void bindAlert(View root, ChildDetail child) {
        FrameLayout container = root.findViewById(R.id.alert_container);
        container.removeAllViews();
        ChildAlert alert = child.alert;

        if (alert == null) {
            View ok = LayoutInflater.from(requireContext()).inflate(R.layout.item_ok_box, container, false);
            container.addView(ok);
            return;
        }

        View box = LayoutInflater.from(requireContext()).inflate(R.layout.item_alert_box, container, false);
        TextView title = box.findViewById(R.id.txt_alert_title);
        TextView text = box.findViewById(R.id.txt_alert_text);
        TextView detail = box.findViewById(R.id.txt_alert_detail);
        View divider = box.findViewById(R.id.alert_divider);
        ImageView chevron = box.findViewById(R.id.img_alert_chevron);

        title.setText(alert.title);
        text.setText(alert.text);
        detail.setText(alert.detail);

        Runnable refresh = () -> {
            detail.setVisibility(alertOpen ? View.VISIBLE : View.GONE);
            divider.setVisibility(alertOpen ? View.VISIBLE : View.GONE);
            chevron.setRotation(alertOpen ? 180f : 0f);
        };
        refresh.run();

        box.setOnClickListener(v -> {
            alertOpen = !alertOpen;
            refresh.run();
        });

        container.addView(box);
    }

    private void bindRecommendations(View root, ChildDetail child) {
        LinearLayout container = root.findViewById(R.id.recommendations_container);
        container.removeAllViews();
        for (String r : child.recommendations) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_recommendation_row, container, false);
            ((TextView) row.findViewById(R.id.txt_recommendation_text)).setText(r);
            container.addView(row);
        }
    }

    private void bindHistory(View root, ChildDetail child) {
        LinearLayout container = root.findViewById(R.id.history_container);
        container.removeAllViews();
        for (ChildHistoryEntry h : child.history) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_history_row, container, false);
            ((TextView) row.findViewById(R.id.txt_history_text)).setText(h.text);
            ((TextView) row.findViewById(R.id.txt_history_date)).setText(h.date);
            container.addView(row);
        }
    }

    private int withAlpha(int color, int alpha0to255) {
        return Color.argb(alpha0to255, Color.red(color), Color.green(color), Color.blue(color));
    }
}
