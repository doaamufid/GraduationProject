package com.example.graduationproject.Fragments;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;

public class KidsAdaptivePrivacyFragment extends KidsAdaptiveBaseOnboardingFragment {

    @Override public int getScreenIndex() { return 1; }
    @Override protected boolean showSkip() { return false; }
    @Override protected String getPrimaryButtonText() { return getString(R.string.kids_adaptive_privacy_cta); }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        TextView title = KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_privacy_title), 22);
        LinearLayout.LayoutParams tlp = wrap();
        tlp.topMargin = dp(16);
        tlp.bottomMargin = dp(14);
        container.addView(title, tlp);

        TextView subtitle = KidsAdaptiveUiHelpers.subtitle(requireContext(), getString(R.string.kids_adaptive_privacy_body));
        LinearLayout.LayoutParams stlp = matchWrap();
        stlp.bottomMargin = dp(20);
        container.addView(subtitle, stlp);

        String[] emojis = {"🔒", "🤍", "✏️", "🗑"};
        String[] texts = {
                getString(R.string.kids_adaptive_privacy_card_1), getString(R.string.kids_adaptive_privacy_card_2),
                getString(R.string.kids_adaptive_privacy_card_3), getString(R.string.kids_adaptive_privacy_card_4)
        };

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(2);
        LinearLayout.LayoutParams glp = matchWrap();
        glp.topMargin = dp(12);
        container.addView(grid, glp);

        for (int i = 0; i < 4; i++) {
            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER);
            card.setBackgroundResource(R.drawable.kids_adaptive_bg_card_white);
            int padH = dp(12), padV = dp(18);
            card.setPadding(padH, padV, padH, padV);

            GridLayout.LayoutParams glParams = new GridLayout.LayoutParams();
            glParams.width = 0;
            glParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            glParams.columnSpec = GridLayout.spec(i % 2, 1f);
            glParams.rowSpec = GridLayout.spec(i / 2);
            glParams.setMargins(dp(8), dp(8), dp(8), dp(8));
            grid.addView(card, glParams);

            TextView emoji = new TextView(requireContext());
            emoji.setText(emojis[i]);
            emoji.setTextSize(26);
            emoji.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams elp = wrap();
            elp.bottomMargin = dp(8);
            card.addView(emoji, elp);

            TextView label = new TextView(requireContext());
            label.setText(texts[i]);
            label.setTextSize(13.5f);
            label.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
            label.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
            label.setGravity(Gravity.CENTER);
            card.addView(label, wrap());
        }
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
