package com.example.graduationproject.Fragments;

import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.widget.KidsAdaptiveChoiceCardView;

public class KidsAdaptiveHelpfulFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {
            "audio", "breathing", "spiritual", "writing", "talking", "movement", "activity", "unsure"
    };
    private static final String[] EMOJIS = {"🎧", "🫁", "🕌", "✍️", "💬", "🚶", "🎮", "😶"};

    @Override public int getScreenIndex() { return 8; }
    @Override protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_WARM; }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_help_audio), getString(R.string.kids_adaptive_help_breathing), getString(R.string.kids_adaptive_help_spiritual),
                getString(R.string.kids_adaptive_help_writing), getString(R.string.kids_adaptive_help_talking), getString(R.string.kids_adaptive_help_movement),
                getString(R.string.kids_adaptive_help_activity), getString(R.string.kids_adaptive_help_unsure)
        };

        LinearLayout.LayoutParams tlp = matchWrap(); tlp.topMargin = dp(4); tlp.bottomMargin = dp(2);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_helpful_title), 20), tlp);

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(2);
        LinearLayout.LayoutParams glp = matchWrap(); glp.topMargin = dp(4);
        container.addView(grid, glp);

        KidsAdaptiveOnboardingData data = data();
        for (int i = 0; i < IDS.length; i++) {
            KidsAdaptiveChoiceCardView card = new KidsAdaptiveChoiceCardView(requireContext());
            card.setEmoji(EMOJIS[i]);
            card.setLabel(labels[i]);
            card.setSelectedState(data.helpfulActivities.contains(IDS[i]));

            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0;
            p.height = GridLayout.LayoutParams.WRAP_CONTENT;
            p.columnSpec = GridLayout.spec(i % 2, 1f);
            p.rowSpec = GridLayout.spec(i / 2);
            p.setMargins(dp(5), dp(5), dp(5), dp(5));
            grid.addView(card, p);

            final int idx = i;
            card.setOnClickListener(v -> {
                KidsAdaptiveOnboardingData.toggle(data().helpfulActivities, IDS[idx]);
                card.setSelectedState(data().helpfulActivities.contains(IDS[idx]));
            });
        }
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
