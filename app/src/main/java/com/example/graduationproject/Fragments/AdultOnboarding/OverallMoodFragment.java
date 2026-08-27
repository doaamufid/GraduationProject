package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class OverallMoodFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 3; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_NEUTRAL; }

    @Override
    protected void onSkip() {
        data.overallMood = null;
        host.goNext();
    }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_overall_mood_title), Color.WHITE), 4);

        View bar = new View(requireContext());
        GradientDrawable gd = new GradientDrawable();
        gd.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        gd.setColors(new int[]{AdultOnboardingAppData.TEAL_SOFT, AdultOnboardingAppData.GLOW_SOFT, AdultOnboardingAppData.ROSE});
        gd.setCornerRadius(999);
        gd.setAlpha(150);
        bar.setBackground(gd);
        LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(6));
        barLp.topMargin = dp(14);
        barLp.bottomMargin = dp(6);
        content.addView(bar, barLp);

        LinearLayout list = new LinearLayout(requireContext());
        list.setOrientation(LinearLayout.VERTICAL);
        addToContent(content, list, 6);
        renderOptions(list);

        LinearLayout linkRow = new LinearLayout(requireContext());
        linkRow.setGravity(android.view.Gravity.CENTER);
        linkRow.addView(Widgets.textLink(requireContext(), getString(R.string.adaptive_adult_onboarding_unsure), Color.WHITE, () -> {
            data.overallMood = "unsure";
            renderOptions(list);
        }));
        addToContent(content, linkRow, 4);
    }

    private void renderOptions(LinearLayout list) {
        list.removeAllViews();
        for (Option m : AdultOnboardingAppData.MOOD_OPTIONS) {
            boolean selected = m.id.equals(data.overallMood);
            View card = Widgets.choiceCard(requireContext(), m.emoji, getString(m.labelRes), null, m.color, selected, () -> {
                data.overallMood = m.id;
                renderOptions(list);
            });
            list.addView(card);
        }
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), false, () -> host.goNext()));
    }
}
