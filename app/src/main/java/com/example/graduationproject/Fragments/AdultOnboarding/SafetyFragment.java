package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class SafetyFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 5; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_CALM; }

    @Override
    protected void onSkip() {
        data.safetyFeeling = "skip";
        host.goNext();
    }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_safety_title), Color.WHITE), 4);
        addToContent(content, Widgets.subtext(requireContext(), getString(R.string.adaptive_adult_onboarding_no_right_wrong), Color.WHITE), 2);

        LinearLayout list = new LinearLayout(requireContext());
        list.setOrientation(LinearLayout.VERTICAL);
        addToContent(content, list, 4);
        render(list);
    }

    private void render(LinearLayout list) {
        list.removeAllViews();
        for (Option o : AdultOnboardingAppData.SAFETY_OPTIONS) {
            boolean selected = o.id.equals(data.safetyFeeling);
            list.addView(Widgets.choiceCard(requireContext(), null, getString(o.labelRes), null, selected, () -> {
                data.safetyFeeling = o.id;
                render(list);
            }));
        }
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), false, () -> host.goNext()));
    }
}
