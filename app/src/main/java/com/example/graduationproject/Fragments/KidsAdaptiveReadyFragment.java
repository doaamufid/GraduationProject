package com.example.graduationproject.Fragments;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;

public class KidsAdaptiveReadyFragment extends KidsAdaptiveBaseOnboardingFragment {

    @Override public int getScreenIndex() { return 12; }
    @Override protected boolean showSkip() { return false; }
    @Override protected String getPrimaryButtonText() { return getString(R.string.kids_adaptive_ready_cta); }
    @Override protected String getCompanionMood() { return KidsAdaptiveTeddyBuddyView.MOOD_CALM; }
    @Override protected void onPrimaryClick() { host.finishOnboarding(); }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        container.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dp(400));
        container.setLayoutParams(lp);

        KidsAdaptiveTeddyBuddyView hero = new KidsAdaptiveTeddyBuddyView(requireContext());
        hero.setReducedMotion(host.isReducedMotion());
        hero.setMood(KidsAdaptiveTeddyBuddyView.MOOD_CALM);
        LinearLayout.LayoutParams heroLp = new LinearLayout.LayoutParams(dp(110), dp(110));
        heroLp.gravity = Gravity.CENTER_HORIZONTAL;
        heroLp.bottomMargin = dp(32);
        container.addView(hero, heroLp);

        String nickname = data().nickname;
        String thanks = getString(R.string.kids_adaptive_ready_thanks) + (nickname != null && !nickname.trim().isEmpty() ? "، " + nickname.trim() : "") + " 🧸";
        LinearLayout.LayoutParams tlp = wrap();
        tlp.gravity = Gravity.CENTER_HORIZONTAL;
        tlp.bottomMargin = dp(24);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), thanks, 22), tlp);

        container.addView(KidsAdaptiveUiHelpers.body(requireContext(), getString(R.string.kids_adaptive_ready_body)), wrap());
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
