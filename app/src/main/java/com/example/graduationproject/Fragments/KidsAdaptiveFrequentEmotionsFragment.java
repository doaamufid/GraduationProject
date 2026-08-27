package com.example.graduationproject.Fragments;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.widget.KidsAdaptiveEmotionBubbleView;
import com.example.graduationproject.widget.KidsAdaptiveFlowLayout;

public class KidsAdaptiveFrequentEmotionsFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {
            "tension", "fear", "sadness", "irritation", "anxiety", "terror", "loneliness", "exhaustion", "unsure", "okay"
    };
    private static final String[] EMOJIS = {"😟", "😨", "😔", "😣", "😰", "😱", "😶", "😵", "🫥", "🌤"};

    @Override public int getScreenIndex() { return 4; }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_em_tension), getString(R.string.kids_adaptive_em_fear), getString(R.string.kids_adaptive_em_sadness),
                getString(R.string.kids_adaptive_em_irritation), getString(R.string.kids_adaptive_em_anxiety), getString(R.string.kids_adaptive_em_terror),
                getString(R.string.kids_adaptive_em_loneliness), getString(R.string.kids_adaptive_em_exhaustion), getString(R.string.kids_adaptive_em_unsure),
                getString(R.string.kids_adaptive_em_okay)
        };

        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tlp.topMargin = dp(4);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_emotions_title), 21), tlp);
        container.addView(KidsAdaptiveUiHelpers.subtitle(requireContext(), getString(R.string.kids_adaptive_emotions_subtitle)),
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        KidsAdaptiveFlowLayout flow = new KidsAdaptiveFlowLayout(requireContext());
        flow.setSpacing(dp(12), dp(12));
        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        flp.topMargin = dp(4);
        container.addView(flow, flp);

        KidsAdaptiveOnboardingData data = data();
        for (int i = 0; i < IDS.length; i++) {
            KidsAdaptiveEmotionBubbleView bubble = new KidsAdaptiveEmotionBubbleView(requireContext(), (i % 5) * 0.3f);
            bubble.setEmoji(EMOJIS[i]);
            bubble.setLabel(labels[i]);
            bubble.setSelectedState(data.frequentEmotions.contains(IDS[i]));
            final int idx = i;
            bubble.setOnClickListener(v -> {
                KidsAdaptiveOnboardingData.toggle(data().frequentEmotions, IDS[idx]);
                bubble.setSelectedState(data().frequentEmotions.contains(IDS[idx]));
                host.pulseTeddy();
            });
            flow.addView(bubble, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}
