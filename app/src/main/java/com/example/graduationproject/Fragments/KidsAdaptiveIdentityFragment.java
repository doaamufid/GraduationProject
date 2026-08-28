package com.example.graduationproject.Fragments;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.widget.KidsAdaptiveGenderCardView;

public class KidsAdaptiveIdentityFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] AGE_BRACKETS = {"أقل من 7", "7–9", "10–12", "13–15", "16–17"};

    private TextView greetingView;
    private TextView ageLabel;

    @Override public int getScreenIndex() { return 2; }

    @Override
    protected String getCompanionMood() {
        return (data().nickname != null && !data().nickname.trim().isEmpty())
                ? KidsAdaptiveTeddyBuddyView.MOOD_WARM : KidsAdaptiveTeddyBuddyView.MOOD_NEUTRAL;
    }

    @Override
    protected void onSkipClick() {
        data().nickname = "";
        data().nicknameProvided = false;
        data().ageRangeIndex = null;
        data().gender = null;
        host.goNext();
    }

    @Override
    protected void buildContent(LinearLayout container, LayoutInflater inflater) {
        TextView title = KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_identity_title), 22);
        LinearLayout.LayoutParams tlp = wrap(); tlp.topMargin = dp(20);
        container.addView(title, tlp);

        TextView subtitle = KidsAdaptiveUiHelpers.subtitle(requireContext(), getString(R.string.kids_adaptive_identity_subtitle));
        LinearLayout.LayoutParams stlp = matchWrap(); stlp.topMargin = dp(10);
        container.addView(subtitle, stlp);

        EditText input = new EditText(requireContext());
        input.setHint(getString(R.string.kids_adaptive_identity_hint));
        input.setText(data().nickname);
        input.setGravity(Gravity.CENTER);
        input.setBackgroundResource(R.drawable.kids_adaptive_bg_text_input);
        input.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()));
        input.setTextSize(14);
        input.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        int padH = dp(16), padV = dp(12);
        input.setPadding(padH, padV, padH, padV);
        input.setSingleLine(true);
        LinearLayout.LayoutParams ilp = matchWrap(); ilp.topMargin = dp(24);
        container.addView(input, ilp);

        greetingView = new TextView(requireContext());
        greetingView.setTextSize(14);
        greetingView.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
        greetingView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        greetingView.setGravity(Gravity.CENTER);
        updateGreeting(data().nickname);
        LinearLayout.LayoutParams glp = matchWrap(); glp.topMargin = dp(14); glp.bottomMargin = dp(24);
        container.addView(greetingView, glp);

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                data().nickname = text;
                data().nicknameProvided = !text.trim().isEmpty();
                updateGreeting(text);
                teddyHeader.setMood(getCompanionMood());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Age question
        TextView ageQ = KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_age_question), 17);
        LinearLayout.LayoutParams aqlp = matchWrap(); aqlp.topMargin = dp(24); aqlp.bottomMargin = dp(16);
        container.addView(ageQ, aqlp);

        ageLabel = new TextView(requireContext());
        ageLabel.setTextSize(16);
        ageLabel.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
        ageLabel.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        ageLabel.setGravity(Gravity.CENTER);
        ageLabel.setBackgroundResource(R.drawable.kids_adaptive_bg_age_label_pill);
        int alPadH = dp(20), alPadV = dp(7);
        ageLabel.setPadding(alPadH, alPadV, alPadH, alPadV);
        LinearLayout centerWrap = new LinearLayout(requireContext());
        centerWrap.setGravity(Gravity.CENTER);
        centerWrap.addView(ageLabel, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams cwlp = matchWrap(); cwlp.bottomMargin = dp(16);
        container.addView(centerWrap, cwlp);

        SeekBar seekBar = new SeekBar(requireContext());
        seekBar.setMax(AGE_BRACKETS.length - 1);
        seekBar.setProgress(data().ageRangeIndex != null ? data().ageRangeIndex : 2);
        seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.kids_adaptive_progress_age_slider));
        seekBar.setThumb(getResources().getDrawable(R.drawable.kids_adaptive_thumb_age_slider));
        seekBar.setLayoutDirection(android.view.View.LAYOUT_DIRECTION_LTR);
        updateAgeLabel(data().ageRangeIndex != null ? data().ageRangeIndex : 2);
        container.addView(seekBar, matchWrap());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                data().ageRangeIndex = progress;
                updateAgeLabel(progress);
                host.pulseTeddy();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Gender question
        TextView genderQ = KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_gender_question), 17);
        LinearLayout.LayoutParams gqlp = matchWrap(); gqlp.topMargin = dp(28); gqlp.bottomMargin = dp(16);
        container.addView(genderQ, gqlp);

        LinearLayout genderRow = new LinearLayout(requireContext());
        genderRow.setOrientation(LinearLayout.HORIZONTAL);
        genderRow.setGravity(Gravity.CENTER);

        KidsAdaptiveGenderCardView female = new KidsAdaptiveGenderCardView(requireContext());
        female.setSymbol("🎀");
        female.setLabel(getString(R.string.kids_adaptive_gender_female));
        female.setSelectedState("female".equals(data().gender));

        KidsAdaptiveGenderCardView male = new KidsAdaptiveGenderCardView(requireContext());
        male.setSymbol("⭐");
        male.setLabel(getString(R.string.kids_adaptive_gender_male));
        male.setSelectedState("male".equals(data().gender));

        KidsAdaptiveGenderCardView other = new KidsAdaptiveGenderCardView(requireContext());
        other.setSymbol("😶");
        other.setLabel(getString(R.string.kids_adaptive_gender_unspecified));
        other.setSelectedState("unspecified".equals(data().gender));

        female.setOnClickListener(v -> {
            data().gender = "female";
            female.setSelectedState(true);
            male.setSelectedState(false);
            other.setSelectedState(false);
            host.pulseTeddy();
        });
        male.setOnClickListener(v -> {
            data().gender = "male";
            male.setSelectedState(true);
            female.setSelectedState(false);
            other.setSelectedState(false);
            host.pulseTeddy();
        });
        other.setOnClickListener(v -> {
            data().gender = "unspecified";
            other.setSelectedState(true);
            female.setSelectedState(false);
            male.setSelectedState(false);
            host.pulseTeddy();
        });

        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        int m = dp(8);
        flp.setMargins(m, 0, m, 0);
        
        genderRow.addView(female, flp);
        genderRow.addView(male, flp);
        genderRow.addView(other, flp);
        container.addView(genderRow, matchWrap());
    }

    private void updateGreeting(String text) {
        boolean has = text != null && !text.trim().isEmpty();
        greetingView.setAlpha(has ? 1f : 0f);
        greetingView.setText(getString(R.string.kids_adaptive_identity_greeting_prefix) + (has ? "، " + text.trim() : "") + " 🌈");
    }

    private void updateAgeLabel(int idx) {
        ageLabel.setText(AGE_BRACKETS[idx] + " 🎈");
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
