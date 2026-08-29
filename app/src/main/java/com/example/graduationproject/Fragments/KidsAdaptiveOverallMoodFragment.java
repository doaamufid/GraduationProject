package com.example.graduationproject.Fragments;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.widget.KidsAdaptiveChoiceCardView;

public class KidsAdaptiveOverallMoodFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {"calm", "pressured", "hard"};
    private static final String[] EMOJIS = {"🌤", "🌧", "🌪"};

    @Override public int getScreenIndex() { return 3; }

    @Override
    protected void onSkipClick() {
        data().overallMood = null;
        host.goNext();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_mood_calm), getString(R.string.kids_adaptive_mood_pressured), getString(R.string.kids_adaptive_mood_hard)
        };

        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_overall_mood_title), 22), titleParams());

        KidsAdaptiveChoiceCardView[] cards = new KidsAdaptiveChoiceCardView[3];
        for (int i = 0; i < 3; i++) {
            KidsAdaptiveChoiceCardView card = new KidsAdaptiveChoiceCardView(requireContext());
            card.setEmoji(EMOJIS[i]);
            card.setLabel(labels[i]);
            card.setCardIndex(i);
            card.setSelectedState(IDS[i].equals(data().overallMood));
            final int idx = i;
            card.setOnClickListener(v -> {
                data().overallMood = IDS[idx];
                for (int j = 0; j < cards.length; j++) if (cards[j] != null) cards[j].setSelectedState(j == idx);
            });
            cards[i] = card;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = dp(20);
            lp.leftMargin = dp(36);
            lp.rightMargin = dp(36);
            container.addView(card, lp);
        }

        TextView unsure = new TextView(requireContext());
        unsure.setText(getString(R.string.kids_adaptive_unsure));
        unsure.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        unsure.setAlpha(0.6f);
        unsure.setTextSize(14);
        unsure.setGravity(Gravity.CENTER);
        unsure.setPadding(dp(6), dp(20), dp(6), dp(6));
        unsure.setOnClickListener(v -> {
            data().overallMood = "unsure";
            for (KidsAdaptiveChoiceCardView c : cards) c.setSelectedState(false);
        });
        LinearLayout.LayoutParams ulp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ulp.topMargin = dp(10);
        container.addView(unsure, ulp);
    }

    private LinearLayout.LayoutParams titleParams() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(14); lp.bottomMargin = dp(12);
        return lp;
    }
}
