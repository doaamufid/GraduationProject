package com.example.graduationproject.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.models.KidsAdaptiveOnboardingData;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.AdultOnboarding.FlowLayout;
import com.example.graduationproject.widget.KidsAdaptiveEmotionBubbleView;

public class KidsAdaptiveFrequentEmotionsFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {
            "tension", "fear", "sadness", "irritation", "anxiety", "terror", "loneliness", "exhaustion", "unsure", "okay"
    };
    private static final String[] EMOJIS = {"\uD83D\uDE1F", "\uD83D\uDE28", "\uD83D\uDE14", "\uD83D\uDE16", "\uD83D\uDE30", "\uD83D\uDE31", "\uD83D\uDE36", "\uD83D\uDE35", "\uD83D\uDE36", "\uD83C\uDF24"};

    @Override public int getScreenIndex() { return 4; }

    // 🌟 إضافة هذه الدالة لضمان ظهور مظهر الأفاتار المختار في أعلى هذه الشاشة
    @Override
    protected String getCompanionMood() {
        return data().getAvatarMoodFromSelection();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_em_tension), getString(R.string.kids_adaptive_em_fear), getString(R.string.kids_adaptive_em_sadness),
                getString(R.string.kids_adaptive_em_irritation), getString(R.string.kids_adaptive_em_anxiety), getString(R.string.kids_adaptive_em_terror),
                getString(R.string.kids_adaptive_em_loneliness), getString(R.string.kids_adaptive_em_exhaustion), getString(R.string.kids_adaptive_em_unsure),
                getString(R.string.kids_adaptive_em_okay)
        };

        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tlp.topMargin = dp(20);
        tlp.bottomMargin = dp(10);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_emotions_title), 21), tlp);

        LinearLayout.LayoutParams stlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        stlp.bottomMargin = dp(20);
        container.addView(KidsAdaptiveUiHelpers.subtitle(requireContext(), getString(R.string.kids_adaptive_emotions_subtitle)), stlp);

        FlowLayout flow = new FlowLayout(requireContext());
        flow.setSpacing(dp(18), dp(18));
        flow.setGravityCenter(true);
        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        flp.topMargin = dp(12);
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

                // 🌟 تحريك الأفاتار المختار بالهيدر العلوي عند اختيار أي شعور
                if (host != null) {
                    host.pulseTeddy();
                }

                // 🌟 التأكد من حفظ الأفاتار المختار في SharedPreferences
                String chosenAvatar = data().demoMoodSelected;
                if (chosenAvatar != null && !chosenAvatar.trim().isEmpty()) {
                    SharedPreferences prefs = requireContext().getSharedPreferences("KidsApp", Context.MODE_PRIVATE);
                    prefs.edit().putString("current_child_avatar", chosenAvatar).apply();
                }
            });
            flow.addView(bubble);
        }
    }
}