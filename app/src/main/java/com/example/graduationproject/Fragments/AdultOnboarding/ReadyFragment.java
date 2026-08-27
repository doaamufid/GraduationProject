package com.example.graduationproject.Fragments.AdultOnboarding;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class ReadyFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 12; }
    @Override protected boolean showSkip() { return false; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_CALM; }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        content.setGravity(Gravity.CENTER);

        CompanionView big = new CompanionView(requireContext());
        big.setReducedMotion(host.isReducedMotion());
        big.setMood(CompanionView.MOOD_CALM);
        LinearLayout.LayoutParams bigLp = new LinearLayout.LayoutParams(dp(110), dp(110)); // Bigger
        bigLp.gravity = Gravity.CENTER_HORIZONTAL;
        bigLp.topMargin = dp(24);
        content.addView(big, bigLp);

        String nickname = data.nickname == null ? "" : data.nickname.trim();
        TextView title = new TextView(requireContext());
        String namePart = nickname.isEmpty() ? "" : (", " + nickname);
        title.setText(getString(R.string.adaptive_adult_onboarding_ready_title, namePart));
        title.setTextColor(AdultOnboardingAppData.CREAM);
        title.setTextSize(26);
        title.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.cairo(true));
        title.setGravity(Gravity.CENTER);
        addToContent(content, title, 20); // More margin

        TextView body = new TextView(requireContext());
        body.setText(R.string.adaptive_adult_onboarding_ready_body);
        body.setTextColor(AdultOnboardingAppData.CREAM);
        body.setAlpha(0.9f);
        body.setTextSize(16);
        body.setLineSpacing(0, 1.4f);
        body.setGravity(Gravity.CENTER);
        addToContent(content, body, 14); // More margin
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_ready_button), true, () -> host.completeOnboarding()));
    }
}
