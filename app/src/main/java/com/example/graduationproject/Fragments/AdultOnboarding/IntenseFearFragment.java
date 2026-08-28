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

public class IntenseFearFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 6; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_CALM; }

    @Override
    protected void onSkip() {
        data.intenseFearExperience = "skip";
        host.goNext();
    }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.paragraph(requireContext(),
                getString(R.string.adaptive_adult_onboarding_intense_fear_body),
                Color.WHITE), 6);
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_intense_fear_title), Color.WHITE), 6); // More margin

        LinearLayout list = new LinearLayout(requireContext());
        list.setOrientation(LinearLayout.VERTICAL);
        addToContent(content, list, 8);
        render(list);
    }

    private void render(LinearLayout list) {
        list.removeAllViews();
        for (Option o : AdultOnboardingAppData.FEAR_OPTIONS) {
            boolean selected = o.id.equals(data.intenseFearExperience);
            list.addView(Widgets.choiceCard(requireContext(), null, getString(o.labelRes), null, o.color, selected, () -> {
                data.intenseFearExperience = o.id;
                render(list);
            }));
        }
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.footerButtons(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), () -> host.goNext(), () -> host.goBack()));
    }
}
