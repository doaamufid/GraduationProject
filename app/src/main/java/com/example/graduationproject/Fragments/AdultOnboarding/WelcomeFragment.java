package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.graduationproject.R;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class WelcomeFragment extends BaseScreenFragment {
    @Override protected int getScreenIndex() { return 0; }
    @Override protected boolean showSkip() { return false; }
    @Override protected boolean showShellCompanion() { return false; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_NEUTRAL; }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);

        CompanionView big = new CompanionView(requireContext());
        big.setReducedMotion(host.isReducedMotion());
        big.setMood(CompanionView.MOOD_NEUTRAL);
        addToContent(content, big, 40, dp(150)); // Moved down a bit

        androidx.appcompat.widget.AppCompatTextView title = new androidx.appcompat.widget.AppCompatTextView(requireContext());
        title.setText(R.string.adaptive_adult_onboarding_welcome_title);
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.cairo(true));
        addToContent(content, title, 22); // More margin

        addToContent(content, Widgets.paragraph(requireContext(),
                getString(R.string.adaptive_adult_onboarding_welcome_body),
                Color.WHITE), 12);
    }

    private void addToContent(LinearLayout content, android.view.View v, int marginTopDp, int fixedSizePx) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(fixedSizePx, fixedSizePx);
        lp.topMargin = dp(marginTopDp);
        content.addView(v, lp);
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_welcome_button), false, () -> host.goNext()));
    }
}
