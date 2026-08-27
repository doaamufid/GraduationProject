package com.example.graduationproject.Fragments;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;

public class KidsAdaptiveWelcomeFragment extends KidsAdaptiveBaseOnboardingFragment {

    @Override public int getScreenIndex() { return 0; }
    @Override protected boolean showSkip() { return false; }
    @Override protected String getPrimaryButtonText() { return getString(R.string.kids_adaptive_welcome_cta); }
    @Override protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_NEUTRAL; }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        container.setGravity(Gravity.CENTER);
        container.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, dp(420));
        container.setLayoutParams(lp);

        KidsAdaptiveTeddyBuddyView hero = new KidsAdaptiveTeddyBuddyView(requireContext());
        hero.setReducedMotion(host.isReducedMotion());
        hero.setMood(KidsAdaptiveTeddyBuddyView.MOOD_NEUTRAL);
        LinearLayout.LayoutParams heroLp = new LinearLayout.LayoutParams(dp(120), dp(120));
        heroLp.gravity = Gravity.CENTER_HORIZONTAL;
        heroLp.bottomMargin = dp(14);
        container.addView(hero, heroLp);

        container.addView(centered(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_welcome_title), 28), 14));
        container.addView(KidsAdaptiveUiHelpers.body(requireContext(), getString(R.string.kids_adaptive_welcome_body)));
    }

    private android.view.View centered(android.view.View v, int bottomMargin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = dp(bottomMargin);
        v.setLayoutParams(lp);
        return v;
    }
}
