package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

public class PreviewFragment extends BaseScreenFragment {

    private static class Card {
        String emoji; String text; Integer jump; int color;
        Card(String e, String t, Integer j, int c) { emoji = e; text = t; jump = j; color = c; }
    }

    @Override protected int getScreenIndex() { return 11; }
    @Override protected boolean showSkip() { return false; }
    @Override protected String getCompanionMood() { return CompanionView.MOOD_CALM; }

    private List<Card> buildPreview() {
        List<Card> cards = new ArrayList<>();
        if (data.difficultTimes.contains("night")) {
            cards.add(new Card("\uD83C\uDF19", getString(R.string.adaptive_adult_onboarding_preview_card_night), 7, Color.parseColor("#4B3E72")));
        }
        if (data.frequentEmotions.contains("anxiety") || data.frequentEmotions.contains("tension")) {
            cards.add(new Card("\uD83C\uDF2C", getString(R.string.adaptive_adult_onboarding_preview_card_anxiety), 4, Color.parseColor("#6FA79A")));
        }
        if (data.helpfulActivities.contains("spiritual")) {
            cards.add(new Card("\u2728", getString(R.string.adaptive_adult_onboarding_preview_card_spiritual), 8, Color.parseColor("#C99E82")));
        }
        if (data.helpfulActivities.contains("writing")) {
            cards.add(new Card("\u270D\uFE0F", getString(R.string.adaptive_adult_onboarding_preview_card_writing), 8, Color.parseColor("#7C6A9C")));
        }
        if (!data.goals.isEmpty() && !(data.goals.size() == 1 && data.goals.contains("explore"))) {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (String gId : data.goals) {
                if ("explore".equals(gId)) continue;
                if (count >= 2) break;
                if (count > 0) sb.append(getString(R.string.adaptive_adult_onboarding_app_name).equals("Salam") ? ", " : "، ");
                sb.append(getString(findGoalLabelRes(gId)));
                count++;
            }
            if (count > 0) {
                cards.add(new Card("\uD83C\uDF31", getString(R.string.adaptive_adult_onboarding_preview_card_goals_prefix) + sb + ".", 9, Color.parseColor("#59B28D")));
            }
        }
        if (cards.isEmpty()) {
            cards.add(new Card("\uD83E\uDD0D", getString(R.string.adaptive_adult_onboarding_preview_card_default), null, Color.parseColor("#4D91A5")));
        }
        return cards;
    }

    private int findGoalLabelRes(String id) {
        for (Option o : AdultOnboardingAppData.GOALS) if (o.id.equals(id)) return o.labelRes;
        return R.string.adaptive_adult_onboarding_goal_explore;
    }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        TextView title = new TextView(requireContext());
        title.setText(R.string.adaptive_adult_onboarding_preview_title);
        title.setTextColor(AdultOnboardingAppData.INK);
        title.setTextSize(20);
        title.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.cairo(true));
        title.setGravity(Gravity.CENTER);
        addToContent(content, title, 4);

        TextView sub = new TextView(requireContext());
        sub.setText(R.string.adaptive_adult_onboarding_preview_subtext);
        sub.setTextColor(AdultOnboardingAppData.INK);
        sub.setAlpha(0.6f);
        sub.setTextSize(12.5f);
        sub.setGravity(Gravity.CENTER);
        addToContent(content, sub, 2);

        LinearLayout list = new LinearLayout(requireContext());
        list.setOrientation(LinearLayout.VERTICAL);
        addToContent(content, list, 12);

        List<Card> cards = buildPreview();
        for (Card c : cards) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowLp.topMargin = dp(14);
            list.addView(row, rowLp);

            LinearLayout emojiBubble = new LinearLayout(requireContext());
            emojiBubble.setGravity(Gravity.CENTER);
            int circle = dp(46);
            LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(circle, circle);
            elp.setMarginEnd(dp(14));
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            gd.setColor(c.color);
            emojiBubble.setBackground(gd);
            
            TextView emoji = new TextView(requireContext());
            emoji.setText(c.emoji);
            emoji.setTextSize(20);
            emoji.setGravity(Gravity.CENTER);
            emojiBubble.addView(emoji);
            row.addView(emojiBubble, elp);
            
            Widgets.startPulse(emojiBubble);

            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setBackgroundResource(R.drawable.bg_preview_row);
            card.setPadding(dp(14), dp(13), dp(14), dp(13));
            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            row.addView(card, cardLp);

            TextView text = new TextView(requireContext());
            text.setText(c.text);
            text.setTextColor(AdultOnboardingAppData.INK);
            text.setTextSize(13);
            text.setLineSpacing(0, 1.3f);
            LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            card.addView(text, tLp);

            if (c.jump != null) {
                TextView edit = Widgets.textLink(requireContext(), getString(R.string.adaptive_adult_onboarding_edit), AdultOnboardingAppData.INK, () -> host.goTo(c.jump));
                edit.setAlpha(0.55f);
                edit.setTextSize(11.5f);
                card.addView(edit);
            }
        }

        TextView hint = new TextView(requireContext());
        hint.setText(R.string.adaptive_adult_onboarding_preview_hint);
        hint.setTextColor(AdultOnboardingAppData.INK);
        hint.setAlpha(0.65f);
        hint.setTextSize(11.5f);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(dp(16), dp(7), dp(16), dp(7));
        android.graphics.drawable.GradientDrawable hbg = new android.graphics.drawable.GradientDrawable();
        hbg.setCornerRadius(999);
        hbg.setColor(Color.argb(102, 255, 255, 255));
        hint.setBackground(hbg);
        LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hintLp.gravity = Gravity.CENTER;
        hintLp.topMargin = dp(10);
        LinearLayout hintWrap = new LinearLayout(requireContext());
        hintWrap.setGravity(Gravity.CENTER);
        hintWrap.addView(hint);
        addToContent(content, hintWrap, 10);
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.primaryButton(requireContext(), getString(R.string.adaptive_adult_onboarding_continue), true, () -> host.goNext()));
    }
}
