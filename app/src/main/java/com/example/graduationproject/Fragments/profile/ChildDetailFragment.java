package com.example.graduationproject.Fragments.profile;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.graduationproject.ui.profile.MoodArcView;

/**
 * Mirrors <ChildDetailScreen/>: hero card, stat grid, mood chart, top
 * features, an expandable alert (or "all good" box when alert is null),
 * recommendations, and an alert history list.
 */
public class ChildDetailFragment extends Fragment {

    private static final String ARG_CHILD_ID = "child_id";

    private boolean alertOpen = false;

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
        bindMoodChart(view, child);
        bindFeatures(view, child);
        bindAlert(view, child);
        bindRecommendations(view, child);
        bindHistory(view, child);
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

    private void bindMoodChart(View root, ChildDetail child) {
        MoodArcView arc = root.findViewById(R.id.mood_arc_view);
        arc.setData(child.mood, child.color);

        LinearLayout daysRow = root.findViewById(R.id.mood_days_row);
        daysRow.removeAllViews();
        for (String day : child.days) {
            TextView t = new TextView(requireContext());
            t.setText(day);
            t.setTextSize(11);
            t.setTypeface(t.getTypeface(), android.graphics.Typeface.BOLD);
            t.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_soft));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            t.setLayoutParams(lp);
            t.setGravity(android.view.Gravity.CENTER);
            daysRow.addView(t);
        }
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

    /** Mirrors the ternary: child.alert ? <expandable accordion> : <"all good" box>. */
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
