package com.example.graduationproject.Fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.widget.KidsAdaptiveFlowLayout;
import com.example.graduationproject.widget.KidsAdaptiveGoalChipView;

public class KidsAdaptiveGoalsFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] EMOJIS = {"🌤", "🫁", "🧠", "🗓️", "🌙", "🤍", "🌱", "✍️", "🧭"};

    @Override public int getScreenIndex() { return 9; }
    @Override protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_WARM; }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_goal_calm), getString(R.string.kids_adaptive_goal_stress), getString(R.string.kids_adaptive_goal_understand),
                getString(R.string.kids_adaptive_goal_organize), getString(R.string.kids_adaptive_goal_sleep), getString(R.string.kids_adaptive_goal_alone),
                getString(R.string.kids_adaptive_goal_habits), getString(R.string.kids_adaptive_goal_express), getString(R.string.kids_adaptive_goal_watch)
        };

        LinearLayout.LayoutParams tlp = matchWrap(); tlp.topMargin = dp(4); tlp.bottomMargin = dp(10);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_goals_title), 20), tlp);

        KidsAdaptiveFlowLayout flow = new KidsAdaptiveFlowLayout(requireContext());
        flow.setSpacing(dp(10), dp(10));
        container.addView(flow, matchWrap());

        KidsAdaptiveOnboardingData data = data();
        for (int i = 0; i < labels.length; i++) {
            KidsAdaptiveGoalChipView chip = new KidsAdaptiveGoalChipView(requireContext());
            chip.setEmoji(EMOJIS[i]);
            chip.setLabel(labels[i]);
            chip.setSelectedState(data.goals.contains(labels[i]));
            final String label = labels[i];
            chip.setOnClickListener(v -> {
                KidsAdaptiveOnboardingData.toggle(data().goals, label);
                chip.setSelectedState(data().goals.contains(label));
            });
            flow.addView(chip, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
