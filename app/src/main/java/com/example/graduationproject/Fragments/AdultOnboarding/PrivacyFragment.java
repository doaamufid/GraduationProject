package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
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

        int[][] cardData = {
                {R.drawable.ic_lock, R.string.adaptive_adult_onboarding_privacy_card1},
                {R.drawable.ic_check_circle, R.string.adaptive_adult_onboarding_privacy_card2},
                {R.drawable.ic_pencil, R.string.adaptive_adult_onboarding_privacy_card3},
                {R.drawable.ic_trash, R.string.adaptive_adult_onboarding_privacy_card4},
        };

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(2);
        for (int[] c : cardData) {
            LinearLayout cell = new LinearLayout(requireContext());
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(dp(16));
            gd.setColor(Color.argb(18, 255, 255, 255));
            gd.setStroke(dp(1), Color.argb(36, 255, 255, 255));
            cell.setBackground(gd);
            cell.setPadding(dp(12), dp(16), dp(12), dp(16));

            ImageView icon = new ImageView(requireContext());
            icon.setImageResource(c[0]);
            icon.setColorFilter(Color.WHITE);
            int iconSize = dp(24);
            LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(iconSize, iconSize);
            cell.addView(icon, icLp);

            TextView label = new TextView(requireContext());
            label.setText(c[1]);
            label.setTextColor(Color.WHITE);
            label.setTextSize(13.5f);
            label.setGravity(Gravity.CENTER);
            label.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.tajawal(false));
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.topMargin = dp(6);
            cell.addView(label, llp);

            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = 0;
            glp.height = GridLayout.LayoutParams.WRAP_CONTENT;
            glp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            glp.setMargins(dp(6), dp(6), dp(6), dp(6));
            grid.addView(cell, glp);
        }
        addToContent(content, grid, 6);
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_privacy_button), false, () -> host.goNext()));
    }
}
