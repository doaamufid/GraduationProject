package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class PrivacyFragment extends BaseScreenFragment {

    @Override protected int getScreenIndex() { return 1; }
    @Override protected boolean showSkip() { return false; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_NEUTRAL; }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_privacy_title), Color.WHITE), 8);
        addToContent(content, Widgets.subtext(requireContext(),
                getString(R.string.adaptive_adult_onboarding_privacy_subtext),
                Color.WHITE), 6);

        Object[][] cardData = {
                {"\uD83D\uDD12", getString(R.string.adaptive_adult_onboarding_privacy_card1), Color.parseColor("#E2F5FF")},
                {"\uD83E\uDD0D", getString(R.string.adaptive_adult_onboarding_privacy_card2), Color.parseColor("#E5F9E5")},
                {"\uD83D\uDCDD", getString(R.string.adaptive_adult_onboarding_privacy_card3), Color.parseColor("#F5E5FF")},
                {"\uD83D\uDDD1", getString(R.string.adaptive_adult_onboarding_privacy_card4), Color.parseColor("#FFE5E5")},
        };

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(2);
        for (Object[] c : cardData) {
            LinearLayout cell = new LinearLayout(requireContext());
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
            GradientDrawable gd = new GradientDrawable();
            // Blob radius
            gd.setCornerRadii(new float[]{
                dp(40), dp(40),
                dp(25), dp(25),
                dp(50), dp(50),
                dp(30), dp(30)
            });
            gd.setColor((int)c[2]);
            gd.setStroke(dp(1), Color.argb(40, 0, 0, 0));
            cell.setBackground(gd);
            cell.setPadding(dp(12), dp(22), dp(12), dp(22));

            TextView emoji = new TextView(requireContext());
            emoji.setText((String) c[0]);
            emoji.setTextSize(38); // Bigger icon
            emoji.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(54), dp(54));
            icLp.bottomMargin = dp(8);
            cell.addView(emoji, icLp);

            TextView label = new TextView(requireContext());
            label.setText((String) c[1]);
            label.setTextColor(com.example.graduationproject.AdultOnboardingAppData.INK); // Changed to ink
            label.setTextSize(14f);
            label.setGravity(Gravity.CENTER);
            label.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.tajawal(true));
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cell.addView(label, llp);

            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = 0;
            glp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            glp.setMargins(dp(10), dp(10), dp(10), dp(10));
            grid.addView(cell, glp);
        }
        addToContent(content, grid, 6);
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_privacy_button), false, () -> host.goNext()));
    }
}
