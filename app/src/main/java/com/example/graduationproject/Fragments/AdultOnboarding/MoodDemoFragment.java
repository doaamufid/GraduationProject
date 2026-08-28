package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.models.AdultOnboarding.Option;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class MoodDemoFragment extends BaseScreenFragment {

    private TextView statusLine;
    private LinearLayout facesRow;

    @Override protected int getScreenIndex() { return 10; }
    @Override protected boolean showSkip() { return false; }

    @Override
    protected String getCompanionMood() {
        return data.demoMoodSelected != null ? CompanionView.MOOD_WARM : CompanionView.MOOD_NEUTRAL;
    }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.paragraph(requireContext(),
                getString(R.string.adaptive_adult_onboarding_mood_demo_paragraph), Color.WHITE), 4);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(20));
        gd.setColor(Color.argb(160, 255, 255, 255));
        gd.setStroke(dp(1), Color.argb(40, 0, 0, 0));
        card.setBackground(gd);
        card.setPadding(dp(14), dp(18), dp(14), dp(18));
        addToContent(content, card, 12);

        TextView question = new TextView(requireContext());
        question.setText(R.string.adaptive_adult_onboarding_mood_demo_question);
        question.setTextColor(com.example.graduationproject.AdultOnboardingAppData.INK);
        question.setTextSize(17);
        question.setGravity(Gravity.CENTER);
        question.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.cairo(true));
        LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qlp.bottomMargin = dp(14);
        card.addView(question, qlp);

        facesRow = new LinearLayout(requireContext());
        facesRow.setOrientation(LinearLayout.HORIZONTAL);
        facesRow.setWeightSum(AdultOnboardingAppData.DEMO_FACES.length);
        card.addView(facesRow, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        renderFaces();

        statusLine = new TextView(requireContext());
        statusLine.setTextColor(Color.WHITE);
        statusLine.setTextSize(13.5f);
        statusLine.setGravity(Gravity.CENTER);
        statusLine.setAlpha(0.85f);
        addToContent(content, statusLine, 10);
        updateStatus();

        addToContent(content, Widgets.subtext(requireContext(),
                getString(R.string.adaptive_adult_onboarding_mood_demo_subtext),
                Color.WHITE), 4);
    }

    private void renderFaces() {
        facesRow.removeAllViews();
        for (int i = 0; i < AdultOnboardingAppData.DEMO_FACES.length; i++) {
            Option f = AdultOnboardingAppData.DEMO_FACES[i];
            boolean isSel = f.id.equals(data.demoMoodSelected);
            LinearLayout col = new LinearLayout(requireContext());
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.CENTER);
            col.setPadding(dp(4), dp(10), dp(4), dp(10));
            
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(16));
            if (isSel) {
                // Light translucent white for selection to match other screens
                bg.setColor(Color.argb(80, 255, 255, 255));
            } else {
                bg.setColor(Color.TRANSPARENT);
            }
            col.setBackground(bg);
            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(dp(4), 0, dp(4), 0);
            col.setLayoutParams(lp);

            TextView emoji = new TextView(requireContext());
            emoji.setText(f.emoji);
            emoji.setTextSize(32); 
            emoji.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(44), dp(44));
            icLp.bottomMargin = dp(4);
            col.addView(emoji, icLp);

            TextView label = new TextView(requireContext());
            label.setText(f.labelRes);
            label.setTextColor(com.example.graduationproject.AdultOnboardingAppData.INK);
            label.setAlpha(0.9f);
            label.setTextSize(10.5f);
            label.setGravity(Gravity.CENTER);
            col.addView(label);

            col.setOnClickListener(v -> {
                data.demoMoodSelected = f.id;
                pulse();
                companionView.setMood(getCompanionMood());
                renderFaces();
                updateStatus();
            });
            facesRow.addView(col);
        }
    }

    private void updateStatus() {
        statusLine.setText(data.demoMoodSelected != null
                ? R.string.adaptive_adult_onboarding_mood_demo_status_active
                : R.string.adaptive_adult_onboarding_mood_demo_status_idle);
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.footerButtons(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), () -> host.goNext(), () -> host.goBack()));
    }
}
