package com.example.graduationproject.Fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveSceneIconView;
import com.example.graduationproject.widget.KidsAdaptiveFlowLayout;
import com.example.graduationproject.widget.KidsAdaptivePillTagView;
import com.example.graduationproject.widget.KidsAdaptiveTimePeriodCardView;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class KidsAdaptiveTimelineFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] PERIOD_IDS = {"morning", "day", "evening", "night"};

    private LinearLayout followupsHost;

    @Override public int getScreenIndex() { return 7; }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] periodLabels = {
                getString(R.string.kids_adaptive_period_morning), getString(R.string.kids_adaptive_period_day),
                getString(R.string.kids_adaptive_period_evening), getString(R.string.kids_adaptive_period_night)
        };
        KidsAdaptiveSceneIconView.Scene[] scenes = {
                KidsAdaptiveSceneIconView.Scene.MORNING, KidsAdaptiveSceneIconView.Scene.DAY, KidsAdaptiveSceneIconView.Scene.EVENING, KidsAdaptiveSceneIconView.Scene.NIGHT
        };

        LinearLayout.LayoutParams tlp = matchWrap(); tlp.topMargin = dp(4); tlp.bottomMargin = dp(10);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_timeline_title), 20), tlp);

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(2);
        container.addView(grid, matchWrap());

        followupsHost = new LinearLayout(requireContext());
        followupsHost.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams flp = matchWrap(); flp.topMargin = dp(4);
        container.addView(followupsHost, flp);

        KidsAdaptiveTimePeriodCardView[] cards = new KidsAdaptiveTimePeriodCardView[4];
        for (int i = 0; i < 4; i++) {
            KidsAdaptiveTimePeriodCardView card = new KidsAdaptiveTimePeriodCardView(requireContext());
            card.setScene(scenes[i]);
            card.setLabel(periodLabels[i]);
            card.setSelectedState(data().difficultTimes.contains(PERIOD_IDS[i]));
            cards[i] = card;

            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = 0;
            glp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            glp.columnSpec = GridLayout.spec(i % 2, 1f);
            glp.rowSpec = GridLayout.spec(i / 2);
            glp.setMargins(dp(6), dp(6), dp(6), dp(6));
            grid.addView(card, glp);

            final int idx = i;
            card.setOnClickListener(v -> {
                KidsAdaptiveOnboardingData.toggle(data().difficultTimes, PERIOD_IDS[idx]);
                card.setSelectedState(data().difficultTimes.contains(PERIOD_IDS[idx]));
                rebuildFollowups(periodLabels);
            });
        }

        rebuildFollowups(periodLabels);
    }

    private void rebuildFollowups(String[] periodLabels) {
        followupsHost.removeAllViews();

        Map<String, String> prompts = new LinkedHashMap<>();
        prompts.put("night", getString(R.string.kids_adaptive_followup_night_prompt));
        prompts.put("day", getString(R.string.kids_adaptive_followup_day_prompt));
        prompts.put("morning", getString(R.string.kids_adaptive_followup_morning_prompt));
        prompts.put("evening", getString(R.string.kids_adaptive_followup_evening_prompt));

        Map<String, String[]> options = new LinkedHashMap<>();
        options.put("night", new String[]{"لحالي", "خايف", "أفكر كثير", "قلقان", "حزين", "صعب أنام"});
        options.put("day", new String[]{"متوتر", "تعبان", "خايف", "مضغوط", "صعب أركّز"});
        options.put("morning", new String[]{"قلقان", "تعبان", "متوقّع يوم صعب"});
        options.put("evening", new String[]{"متوتر", "حزين", "أبي أرتاح"});

        for (String periodId : PERIOD_IDS) {
            if (!data().difficultTimes.contains(periodId)) continue;
            LinkedHashSet<String> fieldSet = fieldFor(periodId);

            LinearLayout panel = new LinearLayout(requireContext());
            panel.setOrientation(LinearLayout.VERTICAL);
            panel.setBackgroundResource(R.drawable.kids_adaptive_bg_panel_soft);
            int pad = dp(14);
            panel.setPadding(pad, pad, pad, pad);
            LinearLayout.LayoutParams plp = matchWrap(); plp.topMargin = dp(4);
            followupsHost.addView(panel, plp);

            TextView prompt = new TextView(requireContext());
            prompt.setText(prompts.get(periodId));
            prompt.setTextSize(14);
            prompt.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), android.graphics.Typeface.BOLD);
            prompt.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
            LinearLayout.LayoutParams promptLp = matchWrap(); promptLp.bottomMargin = dp(10);
            panel.addView(prompt, promptLp);

            KidsAdaptiveFlowLayout wrapRow = new KidsAdaptiveFlowLayout(requireContext());
            wrapRow.setSpacing(dp(8), dp(8));
            panel.addView(wrapRow, matchWrap());

            for (String opt : options.get(periodId)) {
                KidsAdaptivePillTagView tag = new KidsAdaptivePillTagView(requireContext());
                tag.setText(opt);
                tag.setSelectedState(fieldSet.contains(opt));
                tag.setOnClickListener(v -> {
                    KidsAdaptiveOnboardingData.toggle(fieldSet, opt);
                    tag.setSelectedState(fieldSet.contains(opt));
                });
                wrapRow.addView(tag, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private LinkedHashSet<String> fieldFor(String periodId) {
        switch (periodId) {
            case "night": return data().nightFeelings;
            case "day": return data().dayFeelings;
            case "morning": return data().morningFeelings;
            case "evening": return data().eveningFeelings;
            default: return new LinkedHashSet<>();
        }
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
