package com.example.graduationproject.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;

import java.util.ArrayList;
import java.util.List;

public class KidsAdaptivePreviewFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static class Card {
        String emoji, text; Integer jump;
        Card(String e, String t, Integer j) { emoji = e; text = t; jump = j; }
    }

    @Override public int getScreenIndex() { return 11; }
    @Override protected boolean showSkip() { return false; }

    // 🌟 استرجاع مظهر الأفاتار/الدب المختار ديناميكياً بدلاً من MOOD_CALM
    @Override
    protected String getCompanionMood() {
        return data().getAvatarMoodFromSelection();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        // 🌟 التأكد من المزامنة والحفظ النهائي للأفاتار المختار في SharedPreferences
        saveAvatarToPrefs();

        LinearLayout.LayoutParams tlp = matchWrap(); tlp.topMargin = dp(20); tlp.bottomMargin = dp(16);
        container.addView(KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_preview_title), 20), tlp);

        List<Card> cards = buildPreview();
        for (Card c : cards) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.kids_adaptive_bg_card_white);
            int padH = dp(16), padV = dp(18);
            row.setPadding(padH, padV, padH, padV);
            LinearLayout.LayoutParams rlp = matchWrap(); rlp.topMargin = dp(12);
            container.addView(row, rlp);

            TextView emojiCircle = new TextView(requireContext());
            emojiCircle.setText(c.emoji);
            emojiCircle.setTextSize(20);
            emojiCircle.setGravity(Gravity.CENTER);
            emojiCircle.setBackgroundResource(R.drawable.kids_adaptive_bg_bubble_selected);
            LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(dp(44), dp(44));
            ep.setMarginEnd(dp(14));
            row.addView(emojiCircle, ep);

            TextView text = new TextView(requireContext());
            text.setText(c.text);
            text.setTextSize(14f);
            text.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
            text.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
            text.setLineSpacing(0f, 1.55f);
            LinearLayout.LayoutParams txp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            row.addView(text, txp);

            if (c.jump != null) {
                TextView edit = new TextView(requireContext());
                edit.setText(getString(R.string.kids_adaptive_preview_edit));
                edit.setTextSize(12f);
                edit.setPaintFlags(edit.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                edit.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
                edit.setAlpha(0.6f);
                edit.setPadding(dp(8), dp(4), 0, dp(4));
                final int jump = c.jump;
                edit.setOnClickListener(v -> {
                    if (host != null) {
                        host.pulseTeddy();
                        host.goTo(jump);
                    }
                });
                row.addView(edit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            // 🌟 إضافة تفاعل عند النقر على الكرت نفسه
            row.setOnClickListener(v -> {
                if (host != null) {
                    host.pulseTeddy();
                }
            });
        }

        TextView footnote = new TextView(requireContext());
        footnote.setText(getString(R.string.kids_adaptive_preview_footnote));
        footnote.setTextSize(12f);
        footnote.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
        footnote.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        footnote.setAlpha(0.65f);
        footnote.setBackgroundResource(R.drawable.kids_adaptive_bg_footnote_pill);
        int fpH = dp(18), fpV = dp(8);
        footnote.setPadding(fpH, fpV, fpH, fpV);
        LinearLayout centerWrap = new LinearLayout(requireContext());
        centerWrap.setGravity(Gravity.CENTER);
        centerWrap.addView(footnote, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams fwlp = matchWrap(); fwlp.topMargin = dp(20);
        container.addView(centerWrap, fwlp);
    }

    private void saveAvatarToPrefs() {
        String chosenAvatar = data().demoMoodSelected;
        if (chosenAvatar != null && !chosenAvatar.trim().isEmpty()) {
            SharedPreferences prefs = requireContext().getSharedPreferences("KidsApp", Context.MODE_PRIVATE);
            prefs.edit().putString("current_child_avatar", chosenAvatar).apply();
        }
    }

    private List<Card> buildPreview() {
        List<Card> cards = new ArrayList<>();
        if (data().difficultTimes.contains("night")) {
            cards.add(new Card("🌙", getString(R.string.kids_adaptive_preview_night), 7));
        }
        if (data().frequentEmotions.contains("anxiety") || data().frequentEmotions.contains("tension")) {
            cards.add(new Card("🫁", getString(R.string.kids_adaptive_preview_breathing), 4));
        }
        if (data().helpfulActivities.contains("spiritual")) {
            cards.add(new Card("🕌", getString(R.string.kids_adaptive_preview_spiritual), 8));
        }
        if (data().helpfulActivities.contains("writing")) {
            cards.add(new Card("✍️", getString(R.string.kids_adaptive_preview_writing), 8));
        }
        String watchOnly = getString(R.string.kids_adaptive_goal_watch);
        if (!data().goals.isEmpty() && !(data().goals.size() == 1 && data().goals.contains(watchOnly))) {
            List<String> goalsMinusWatch = data().goalsMinusWatchOnly(watchOnly);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(2, goalsMinusWatch.size()); i++) {
                if (i > 0) sb.append("، ");
                sb.append(goalsMinusWatch.get(i));
            }
            if (sb.length() > 0) {
                cards.add(new Card("🌱", getString(R.string.kids_adaptive_preview_goals_prefix, sb.toString()), 9));
            }
        }
        if (cards.isEmpty()) {
            cards.add(new Card("🤍", getString(R.string.kids_adaptive_preview_fallback), null));
        }
        return cards;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}