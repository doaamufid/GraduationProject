package com.example.graduationproject.Fragments;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.widget.KidsAdaptiveDemoFaceView;

public class KidsAdaptiveMoodDemoFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] IDS = {"sad", "low", "neutral", "good", "great"};
    private static final String[] EMOJIS = {"😔", "😕", "😐", "🙂", "😄"};

    private TextView hintView;

    @Override public int getScreenIndex() { return 10; }
    @Override protected boolean showSkip() { return false; }

    @Override
    protected String getCompanionMood() {
        return data().demoMoodSelected != null ? KidsAdaptiveTeddyBuddyView.MOOD_WARM : KidsAdaptiveTeddyBuddyView.MOOD_NEUTRAL;
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        String[] labels = {
                getString(R.string.kids_adaptive_demo_face_sad), getString(R.string.kids_adaptive_demo_face_low), getString(R.string.kids_adaptive_demo_face_neutral),
                getString(R.string.kids_adaptive_demo_face_good), getString(R.string.kids_adaptive_demo_face_great)
        };

        LinearLayout.LayoutParams introLp = matchWrap(); introLp.topMargin = dp(4);
        container.addView(KidsAdaptiveUiHelpers.body(requireContext(), getString(R.string.kids_adaptive_demo_intro)), introLp);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.kids_adaptive_bg_card_white);
        int padH = dp(14), padV = dp(18);
        card.setPadding(padH, padV, padH, padV);
        LinearLayout.LayoutParams cardLp = matchWrap(); cardLp.topMargin = dp(4);
        container.addView(card, cardLp);

        TextView question = new TextView(requireContext());
        question.setText(getString(R.string.kids_adaptive_demo_question));
        question.setTextSize(17);
        question.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
        question.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        question.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams qlp = matchWrap(); qlp.bottomMargin = dp(14);
        card.addView(question, qlp);

        LinearLayout facesRow = new LinearLayout(requireContext());
        facesRow.setOrientation(LinearLayout.HORIZONTAL);
        facesRow.setWeightSum(5);
        card.addView(facesRow, matchWrap());

        KidsAdaptiveDemoFaceView[] faces = new KidsAdaptiveDemoFaceView[5];
        for (int i = 0; i < 5; i++) {
            KidsAdaptiveDemoFaceView face = new KidsAdaptiveDemoFaceView(requireContext());
            face.setEmoji(EMOJIS[i]);
            face.setLabel(labels[i]);
            face.setSelectedState(IDS[i].equals(data().demoMoodSelected));
            faces[i] = face;
            LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            flp.setMargins(dp(2), 0, dp(2), 0);
            final int idx = i;
            face.setOnClickListener(v -> {
                data().demoMoodSelected = IDS[idx];
                for (int j = 0; j < faces.length; j++) faces[j].setSelectedState(j == idx);
                teddyHeader.setMood(getCompanionMood());
                host.pulseTeddy();
                updateHint();
            });
            facesRow.addView(face, flp);
        }

        hintView = new TextView(requireContext());
        hintView.setTextSize(13.5f);
        hintView.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
        hintView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        hintView.setAlpha(0.8f);
        hintView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hlp = matchWrap(); hlp.topMargin = dp(4);
        updateHint();
        container.addView(hintView, hlp);
    }

    private void updateHint() {
        boolean has = data().demoMoodSelected != null;
        hintView.setText(has ? getString(R.string.kids_adaptive_demo_hint_after) : getString(R.string.kids_adaptive_demo_hint_before));
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
