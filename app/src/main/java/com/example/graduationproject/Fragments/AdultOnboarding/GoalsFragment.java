package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.OnboardingData;
import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.FlowLayout;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class GoalsFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 9; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_WARM; }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_goals_title), Color.WHITE), 4);

        FlowLayout flow = new FlowLayout(requireContext());
        flow.setGravityCenter(true);
        flow.setSpacing(dp(6), dp(8));
        addToContent(content, flow, 10);
        render(flow);
    }

    private void render(FlowLayout flow) {
        flow.removeAllViews();
        for (Option g : AdultOnboardingAppData.GOALS) {
            boolean selected = data.goals.contains(g.id);
            android.view.View chip = Widgets.lightChip(requireContext(), g.emoji, getString(g.labelRes), g.color, selected, () -> {
                OnboardingData.toggle(data.goals, g.id);
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
