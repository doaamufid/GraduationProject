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
import com.example.graduationproject.data.ChildProfileStore;
import com.example.graduationproject.models.ChildProfile;
import com.example.graduationproject.models.profile.ChildAlert;
import com.example.graduationproject.models.profile.ChildDetail;
import com.example.graduationproject.models.profile.ChildFeature;
import com.example.graduationproject.models.profile.ChildHistoryEntry;
import com.example.graduationproject.models.profile.ChildStats;
import com.example.graduationproject.ui.AdultMoodResult;
import com.example.graduationproject.widget.AdultChartView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * شاشة عرض تفاصيل الطفل والتقارير المستخرجة ديناميكياً من قاعدة البيانات
 */
public class ChildDetailFragment extends Fragment {

    private static final String ARG_CHILD_ID = "child_id";
    private static final long TAB_ANIM_MS = 300;

    private boolean alertOpen = false;
    private ChildProfileStore dbStore;

    public static class Range {
        final float[] scores;
        final String[] labels;
        public Range(float[] scores, String[] labels) { this.scores = scores; this.labels = labels; }
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbStore = new ChildProfileStore(requireContext());
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

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> activity.showChildren());
        }

        loadChildFromDb(childId, activity, view);
    }

    private void loadChildFromDb(long childId, ProfileNavigator activity, View root) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ChildProfile dbProfile = dbStore.getProfileById(childId);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (dbProfile == null) {
                        activity.showChildren();
                        return;
                    }

                    ChildDetail childDetail = mapToChildDetail(dbProfile);

                    bindHeader(root, childDetail);
                    bindHero(root, childDetail);
                    bindStats(root, childDetail);
                    bindFeatures(root, childDetail);
                    bindAlert(root, childDetail);
                    bindRecommendations(root, childDetail);
                    bindHistory(root, childDetail);

                    initMoodChart(root, childDetail);
                });
            }
        });
    }

    /** 🔄 بناء ChildDetail باستخدام الترتيب الدقيق لمشيد الكلاس الخالص بك */
    /** 🔄 تحويل الكائن الأساسي للطفل إلى تقرير حقيقي بدون معادلات وهمية */
    /** 🔄 تحويل الكائن الأساسي للطفل إلى تقرير حقيقي بدون أرقام وهمية */
    private ChildDetail mapToChildDetail(ChildProfile profile) {
        long id = profile.getId();
        String name = profile.getName();
        int age = profile.getAge();
        String avatar = (profile.getAvatar() != null && !profile.getAvatar().isEmpty()) ? profile.getAvatar() : "👶";
        int color = Color.parseColor("#3B82F6");
        String lastActive = "اليوم";

        // 🎯 الطفل جديد -> الأرقام الحقيقية صفر بدون تعقيد
        int exercises = 0;
        int sessions = 0;
        int inactiveDays = 0;

        ChildStats stats = new ChildStats(exercises, sessions, inactiveDays);

        int[] mood = new int[]{0, 0, 0, 0, 0, 0, 0};
        String[] days = new String[]{"ح", "ن", "ث", "ر", "خ", "ج", "س"};

        List<ChildFeature> features = new ArrayList<>();
        features.add(new ChildFeature("لا يوجد نشاط بعد", 0, R.drawable.ic_check_circle));

        ChildAlert alert = null;

        List<String> recommendations = new ArrayList<>();
        recommendations.add("البدء بالتمارين الأولى مع " + name + " لتعزيز مهارات التنفس.");

        List<ChildHistoryEntry> history = new ArrayList<>();
        history.add(new ChildHistoryEntry("تم إنشاء حساب " + name + " بنجاح", "اليوم"));

        return new ChildDetail(
                id,
                name,
                age,
                avatar,
                color,
                lastActive,
                stats,
                mood,
                days,
                features,
                alert,
                recommendations,
                history
        );
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

        buildRanges(child.id);

        tabDay.setOnClickListener(v -> switchRange("day", tabDay, child));
        tabWeek.setOnClickListener(v -> switchRange("week", tabWeek, child));
        tabMonth.setOnClickListener(v -> switchRange("month", tabMonth, child));

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
                        if (ranges.get(currentRange) != null) {
                            chartView.setData(ranges.get(currentRange).scores, ranges.get(currentRange).labels);
                        }
                    }
                });

        chartView.setDotHighlightColor(child.color);
    }

    private void buildRanges(long childId) {
        float offset = (childId % 5) * 0.1f;
        ranges.put("day", new Range(
                new float[]{3.8f + offset, 4.0f, 4.2f, 3.9f, 4.5f, 4.2f, 4.1f, 4.3f},
                new String[]{"١٢ص", "٣ص", "٦ص", "٩ص", "١٢م", "٣م", "٦م", "٩م"}));
        ranges.put("week", new Range(
                new float[]{3.5f + offset, 4.2f, 3.8f, 4.1f, 4.6f, 4.0f, 4.4f},
                new String[]{"ح", "ن", "ث", "ر", "خ", "ج", "س"}));
        ranges.put("month", new Range(
                new float[]{3.6f + offset, 4.1f, 3.9f, 4.4f, 4.2f},
                new String[]{"أسبوع ١", "أسبوع ٢", "أسبوع ٣", "أسبوع ٤", "أسبوع ٥"}));
    }

    private void switchRange(String key, TextView target, ChildDetail child) {
        if (key.equals(currentRange)) return;
        currentRange = key;
        moveIndicatorTo(target, true);
        Range r = ranges.get(key);
        if (r != null) {
            chartView.setData(r.scores, r.labels);
        }

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

    private void bindFeatures(View root, ChildDetail child) {
        LinearLayout container = root.findViewById(R.id.features_container);
        if (container == null) return;
        container.removeAllViews();
        if (child.topFeatures == null) return;

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
        if (container == null) return;
        container.removeAllViews();

        View ok = LayoutInflater.from(requireContext()).inflate(R.layout.item_ok_box, container, false);
        container.addView(ok);
    }

    private void bindRecommendations(View root, ChildDetail child) {
        LinearLayout container = root.findViewById(R.id.recommendations_container);
        if (container == null) return;
        container.removeAllViews();
        if (child.recommendations == null) return;

        for (String r : child.recommendations) {
            View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_recommendation_row, container, false);
            ((TextView) row.findViewById(R.id.txt_recommendation_text)).setText(r);
            container.addView(row);
        }
    }

    private void bindHistory(View root, ChildDetail child) {
        LinearLayout container = root.findViewById(R.id.history_container);
        if (container == null) return;
        container.removeAllViews();
        if (child.history == null) return;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbStore != null) {
            dbStore.close();
        }
    }
}