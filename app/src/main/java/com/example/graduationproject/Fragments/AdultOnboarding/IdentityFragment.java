package com.example.graduationproject.Fragments.AdultOnboarding;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.R;
import com.example.graduationproject.view.AdultOnboarding.AgeRangeSliderView;
import com.example.graduationproject.view.AdultOnboarding.CompanionView;
import com.example.graduationproject.view.AdultOnboarding.GenderPickerView;
import com.example.graduationproject.view.AdultOnboarding.Widgets;

public class IdentityFragment extends BaseScreenFragment {

    private EditText nicknameInput;
    private TextView greeting;

    @Override protected int getScreenIndex() { return 2; }
    @Override protected String getSkipLabel() { return getString(R.string.adaptive_adult_onboarding_skip); }

    @Override
    protected String getCompanionMood() {
        return (data.nickname != null && !data.nickname.trim().isEmpty()) ? CompanionView.MOOD_WARM : CompanionView.MOOD_NEUTRAL;
    }

    @Override
    protected void onSkip() {
        data.nickname = "";
        data.nicknameProvided = false;
        data.ageRangeIndex = null;
        data.gender = null;
        host.goNext();
    }

    @Override
    protected void populateContent(LayoutInflater inflater, LinearLayout content) {
        addToContent(content, Widgets.heading(requireContext(), getString(R.string.adaptive_adult_onboarding_identity_title), Color.WHITE), 4);
        addToContent(content, Widgets.subtext(requireContext(), getString(R.string.adaptive_adult_onboarding_identity_subtext), Color.WHITE), 2);

        nicknameInput = new EditText(requireContext());
        nicknameInput.setHint(R.string.adaptive_adult_onboarding_identity_hint);
        nicknameInput.setHintTextColor(Color.argb(120, 33, 27, 51)); // Dark hint (Ink alpha)
        nicknameInput.setTextColor(com.example.graduationproject.AdultOnboardingAppData.INK); // Dark text
        nicknameInput.setGravity(Gravity.CENTER);
        nicknameInput.setTextSize(16); // Thinner/Smaller text
        nicknameInput.setBackgroundResource(com.example.graduationproject.R.drawable.bg_input_field);
        nicknameInput.setPadding(dp(16), dp(12), dp(16), dp(12)); // Reduced vertical padding
        nicknameInput.setSingleLine(true);
        nicknameInput.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.tajawal(true));
        if (data.nickname != null) nicknameInput.setText(data.nickname);
        addToContent(content, nicknameInput, 10); // Reduced margin top

        greeting = new TextView(requireContext());
        greeting.setTextColor(AdultOnboardingAppData.CREAM);
        greeting.setTextSize(14);
        greeting.setGravity(Gravity.CENTER);
        updateGreeting();
        addToContent(content, greeting, 8);

        nicknameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                data.nickname = s.toString();
                updateGreeting();
                companionView.setMood(getCompanionMood());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        TextView ageHeading = new TextView(requireContext());
        ageHeading.setText(R.string.adaptive_adult_onboarding_identity_age_title);
        ageHeading.setTextColor(AdultOnboardingAppData.CREAM);
        ageHeading.setTextSize(15.5f);
        ageHeading.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.cairo(true));
        ageHeading.setGravity(Gravity.CENTER);
        addToContent(content, ageHeading, 20);

        AgeRangeSliderView ageSlider = new AgeRangeSliderView(requireContext());
        ageSlider.setIndex(data.ageRangeIndex != null ? data.ageRangeIndex : AdultOnboardingAppData.AGE_BRACKETS.length / 2);
        ageSlider.setOnAgeChange(idx -> {
            data.ageRangeIndex = idx;
        });
        addToContent(content, ageSlider, 8);

        TextView genderHeading = new TextView(requireContext());
        genderHeading.setText(R.string.adaptive_adult_onboarding_identity_gender_title);
        genderHeading.setTextColor(AdultOnboardingAppData.CREAM);
        genderHeading.setTextSize(15.5f);
        genderHeading.setTypeface(com.example.graduationproject.AdultOnboardingUiUtils.cairo(true));
        genderHeading.setGravity(Gravity.CENTER);
        addToContent(content, genderHeading, 12);

        GenderPickerView genderPicker = new GenderPickerView(requireContext());
        genderPicker.setValueSilently(data.gender);
        genderPicker.setOnGenderChange(g -> {
            data.gender = g;
        });
        addToContent(content, genderPicker, 8);
    }

    private void updateGreeting() {
        String name = data.nickname == null ? "" : data.nickname.trim();
        if (name.isEmpty()) {
            greeting.setVisibility(View.INVISIBLE);
        } else {
            greeting.setVisibility(View.VISIBLE);
            greeting.setText(getString(R.string.adaptive_adult_onboarding_identity_greeting, name));
        }
    }

    @Override
    protected void populateFooter(LayoutInflater inflater, ViewGroup footer) {
        footer.addView(Widgets.footerButtons(requireContext(), 
                getString(R.string.adaptive_adult_onboarding_continue), 
                () -> {
                    data.nickname = nicknameInput.getText().toString();
                    data.nicknameProvided = !data.nickname.trim().isEmpty();
                    host.goNext();
                }, 
                () -> host.goBack()));
    }
}
