package com.example.graduationproject.Fragments;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.widget.KidsAdaptiveChoiceCardView;

public class KidsAdaptiveIntenseFearFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {"yes_sometimes", "yes_often", "rarely", "unsure", "skip"};

    @Override public int getScreenIndex() { return 6; }
    @Override protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_CALM; }

    @Override
    protected void onSkipClick() {
        data().intenseFearExperience = "skip";
        host.goNext();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_fear_yes_sometimes), getString(R.string.kids_adaptive_fear_yes_often),
                getString(R.string.kids_adaptive_fear_rarely), getString(R.string.kids_adaptive_unsure), getString(R.string.kids_adaptive_dont_want_answer)
        };

        LinearLayout.LayoutParams blp = matchWrap(); blp.topMargin = dp(20); blp.bottomMargin = dp(16);
        container.addView(KidsAdaptiveUiHelpers.body(requireContext(), getString(R.string.kids_adaptive_fear_body)), blp);

        LinearLayout.LayoutParams tlp = matchWrap(); tlp.bottomMargin = dp(20);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_fear_title), 20), tlp);

        KidsAdaptiveChoiceCardView[] cards = new KidsAdaptiveChoiceCardView[IDS.length];
        for (int i = 0; i < IDS.length; i++) {
            KidsAdaptiveChoiceCardView card = new KidsAdaptiveChoiceCardView(requireContext());
            card.setEmoji("");
            card.setLabel(labels[i]);
            card.setCardIndex(i);
            card.setSelectedState(IDS[i].equals(data().intenseFearExperience));
            final int idx = i;
            card.setOnClickListener(v -> {
                data().intenseFearExperience = IDS[idx];
                for (int j = 0; j < cards.length; j++) cards[j].setSelectedState(j == idx);
            });
            cards[i] = card;
            LinearLayout.LayoutParams lp = matchWrap();
            lp.topMargin = dp(14);
            lp.leftMargin = dp(36);
            lp.rightMargin = dp(36);
            container.addView(card, lp);
        }
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
