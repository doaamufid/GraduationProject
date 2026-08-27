package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.OnboardingData;
import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.FlowLayout;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class HelpfulFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 8; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_WARM; }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_helpful_title), Color.WHITE), 4);
        addToContent(content, Widgets.subtext(requireContext(), getString(R.string.adaptive_adult_onboarding_helpful_subtext), Color.WHITE), 2);

        FlowLayout flow = new FlowLayout(requireContext());
        flow.setGravityCenter(true);
        flow.setSpacing(dp(6), dp(8));
        addToContent(content, flow, 10);
        render(flow);
    }

    private void render(FlowLayout flow) {
        flow.removeAllViews();
        for (Option h : AdultOnboardingAppData.HELPFUL) {
            boolean selected = data.helpfulActivities.contains(h.id);
            android.view.View chip = Widgets.lightChip(requireContext(), h.emoji, getString(h.labelRes), h.color, selected, () -> {
                OnboardingData.toggle(data.helpfulActivities, h.id);
                render(flow);
            });
            flow.addView(chip);
        }
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), false, () -> host.goNext()));
    }
}
