package com.example.graduationproject.Fragments;

import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.widget.KidsAdaptiveChoiceCardView;

public class KidsAdaptiveSafetyFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {"rarely", "sometimes", "often", "most", "skip"};

    @Override public int getScreenIndex() { return 5; }
    @Override protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_CALM; }

    @Override
    protected void onSkipClick() {
        data().safetyFeeling = "skip";
        host.goNext();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_safety_rarely), getString(R.string.kids_adaptive_safety_sometimes),
                getString(R.string.kids_adaptive_safety_often), getString(R.string.kids_adaptive_safety_most),
                getString(R.string.kids_adaptive_dont_want_answer)
        };

        LinearLayout.LayoutParams tlp = matchWrap(); tlp.topMargin = dp(4); tlp.bottomMargin = dp(2);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_safety_title), 20), tlp);
        container.addView(KidsAdaptiveUiHelpers.subtitle(requireContext(), getString(R.string.kids_adaptive_safety_subtitle)), matchWrap());

        KidsAdaptiveChoiceCardView[] cards = new KidsAdaptiveChoiceCardView[IDS.length];
        for (int i = 0; i < IDS.length; i++) {
            KidsAdaptiveChoiceCardView card = new KidsAdaptiveChoiceCardView(requireContext());
            card.setEmoji("");
            card.setLabel(labels[i]);
            card.setSelectedState(IDS[i].equals(data().safetyFeeling));
            final int idx = i;
            card.setOnClickListener(v -> {
                data().safetyFeeling = IDS[idx];
                for (int j = 0; j < cards.length; j++) cards[j].setSelectedState(j == idx);
            });
            cards[i] = card;
            LinearLayout.LayoutParams lp = matchWrap(); lp.topMargin = dp(10);
            container.addView(card, lp);
        }
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
