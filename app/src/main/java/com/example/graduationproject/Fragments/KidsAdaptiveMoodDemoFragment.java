package com.example.graduationproject.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;

public class KidsAdaptiveMoodDemoFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {"sad", "low", "neutral", "good", "great"};
    private static final String[] EMOJIS = {"😔", "😕", "😐", "🙂", "😄"};

    private TextView hintView;
    private LinearLayout facesRow;

    @Override public int getScreenIndex() { return 10; }
    @Override protected boolean showSkip() { return false; }

    @Override
    protected String getCompanionMood() {
        // 🌟 استخدام الدالة الجديدة من كلاس البيانات
        return data().getAvatarMoodFromSelection();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        LinearLayout.LayoutParams introLp = matchWrap(); introLp.topMargin = dp(16); introLp.bottomMargin = dp(12);
        container.addView(KidsAdaptiveUiHelpers.body(requireContext(), getString(R.string.kids_adaptive_demo_intro)), introLp);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(20));
        gd.setColor(Color.argb(160, 255, 255, 255));
        gd.setStroke(dp(1), Color.argb(40, 0, 0, 0));
        card.setBackground(gd);
        int padH = dp(14), padV = dp(20);
        card.setPadding(padH, padV, padH, padV);
        LinearLayout.LayoutParams cardLp = matchWrap(); cardLp.topMargin = dp(8);
        container.addView(card, cardLp);

        TextView question = new TextView(requireContext());
        question.setText(getString(R.string.kids_adaptive_demo_question));
        question.setTextSize(17);
        question.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
        question.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        question.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qlp.bottomMargin = dp(14);
        card.addView(question, qlp);

        facesRow = new LinearLayout(requireContext());
        facesRow.setOrientation(LinearLayout.HORIZONTAL);
        facesRow.setWeightSum(5);
        card.addView(facesRow, matchWrap());
        renderFaces();

        hintView = new TextView(requireContext());
        hintView.setTextSize(13.5f);
        hintView.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
        hintView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        hintView.setAlpha(0.8f);
        hintView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hlp = matchWrap(); hlp.topMargin = dp(10);
        updateHint();
        container.addView(hintView, hlp);
    }

    private void renderFaces() {
        String[] labels = {
                getString(R.string.kids_adaptive_demo_face_sad), getString(R.string.kids_adaptive_demo_face_low), getString(R.string.kids_adaptive_demo_face_neutral),
                getString(R.string.kids_adaptive_demo_face_good), getString(R.string.kids_adaptive_demo_face_great)
        };
        facesRow.removeAllViews();
        for (int i = 0; i < IDS.length; i++) {
            final int idx = i;
            boolean isSel = IDS[idx].equals(data().demoMoodSelected);

            LinearLayout col = new LinearLayout(requireContext());
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.CENTER);
            col.setPadding(dp(4), dp(10), dp(4), dp(10));

            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(16));
            if (isSel) {
                bg.setColor(Color.argb(50, 201, 138, 138));
            } else {
                bg.setColor(Color.TRANSPARENT);
            }
            col.setBackground(bg);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(dp(2), 0, dp(2), 0);
            col.setLayoutParams(lp);

            TextView emoji = new TextView(requireContext());
            emoji.setText(EMOJIS[idx]);
            emoji.setTextSize(30);
            emoji.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams icLp = new LinearLayout.LayoutParams(dp(44), dp(44));
            icLp.bottomMargin = dp(4);
            col.addView(emoji, icLp);

            TextView label = new TextView(requireContext());
            label.setText(labels[idx]);
            label.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
            label.setAlpha(0.9f);
            label.setTextSize(11f);
            label.setGravity(Gravity.CENTER);
            col.addView(label);

            col.setOnClickListener(v -> {
                data().demoMoodSelected = IDS[idx];

                // 🌟 تحديث مظهر الدب العلوي فوراً
                if (teddyHeader != null) {
                    teddyHeader.setMood(data().getAvatarMoodFromSelection());
                }

                if (host != null) {
                    host.pulseTeddy();
                }

                // 🌟 حفظ الاختيار فوراً في SharedPreferences
                String chosenAvatar = data().demoMoodSelected;
                if (chosenAvatar != null && !chosenAvatar.trim().isEmpty()) {
                    SharedPreferences prefs = requireContext().getSharedPreferences("KidsApp", Context.MODE_PRIVATE);
                    prefs.edit().putString("current_child_avatar", chosenAvatar).apply();
                }

                renderFaces();
                updateHint();
            });
            facesRow.addView(col);
        }
    }

    private void updateHint() {
        boolean has = data().demoMoodSelected != null;
        hintView.setText(has ? getString(R.string.kids_adaptive_demo_hint_after) : getString(R.string.kids_adaptive_demo_hint_before));
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}