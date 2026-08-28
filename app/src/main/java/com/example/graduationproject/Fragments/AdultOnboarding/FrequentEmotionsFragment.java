package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.OnboardingData;
import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.FlowLayout;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class FrequentEmotionsFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 4; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_NEUTRAL; }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_frequent_emotions_title), Color.WHITE), 4);
        addToContent(content, Widgets.subtext(requireContext(), getString(R.string.adaptive_adult_onboarding_frequent_emotions_subtext), Color.WHITE), 2);

        FlowLayout flow = new FlowLayout(requireContext());
        flow.setGravityCenter(true);
        addToContent(content, flow, 10);
        render(flow);
    }

    private void render(FlowLayout flow) {
        flow.removeAllViews();
        for (Option em : AdultOnboardingAppData.EMOTIONS) {
            boolean selected = data.frequentEmotions.contains(em.id);
            View bubble = Widgets.emotionBubble(requireContext(), em.emoji, getString(em.labelRes), em.color, selected, () -> {
                OnboardingData.toggle(data.frequentEmotions, em.id);
                pulse();
                render(flow);
            });
            flow.addView(bubble);
        }
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.footerButtons(requireContext(), 
                getString(R.string.adaptive_adult_onboarding_continue), 
                () -> host.goNext(), 
                () -> host.goBack()));
    }
}
