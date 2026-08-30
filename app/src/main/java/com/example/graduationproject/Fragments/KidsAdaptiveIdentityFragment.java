package com.example.graduationproject.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.util.KidsAdaptiveTypefaces;
import com.example.graduationproject.util.KidsAdaptiveUiHelpers;
import com.example.graduationproject.view.KidsAdaptiveTeddyBuddyView;
import com.example.graduationproject.widget.KidsAdaptiveGenderCardView;

import java.util.ArrayList;
import java.util.List;

public class KidsAdaptiveIdentityFragment extends KidsAdaptiveBaseOnboardingFragment {

    private static final String[] AGE_BRACKETS = {"أقل من 7", "7–9", "10–12", "13–15", "16–17"};
    private static final String[] BOY_AVATARS = {"🦁", "🦊", "🐻", "🐼", "🐵", "🐯", "🐨"};
    private static final String[] GIRL_AVATARS = {"🦄", "🐰", "🐱", "🐥", "🦋", "🌸", "👑"};
    private static final String[] GENERAL_AVATARS = {"🌟", "🎈", "🎨", "🚀", "🌈", "🧸", "⚽"};

    private TextView greetingView;
    private TextView ageLabel;
    private TextView avatarTitle;
    private HorizontalScrollView avatarsScrollView;
    private LinearLayout avatarsContainer;
    private final List<TextView> avatarViews = new ArrayList<>();

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
        saveCurrentChildData();
        host.goNext();
    }

    @Override
    protected void buildContent(LinearLayout mainContainer, LayoutInflater inflater) {
        // ScrollView لمنع اقتطاع العناصر السفلية أو كروت الجنس
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setClipToPadding(false);

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 0, 0, dp(24));

        // Title & Subtitle
        TextView title = KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_identity_title), 22);
        LinearLayout.LayoutParams tlp = wrap(); tlp.topMargin = dp(10);
        container.addView(title, tlp);

        TextView subtitle = KidsAdaptiveUiHelpers.subtitle(requireContext(), getString(R.string.kids_adaptive_identity_subtitle));
        LinearLayout.LayoutParams stlp = matchWrap(); stlp.topMargin = dp(6);
        container.addView(subtitle, stlp);

        // Name Input
        EditText input = new EditText(requireContext());
        input.setHint(getString(R.string.kids_adaptive_identity_hint));
        input.setText(data().nickname);
        input.setGravity(Gravity.CENTER);
        input.setBackgroundResource(R.drawable.kids_adaptive_bg_text_input);
        input.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()));
        input.setTextSize(14);
        input.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        int padH = dp(16), padV = dp(10);
        input.setPadding(padH, padV, padH, padV);
        input.setSingleLine(true);
        LinearLayout.LayoutParams ilp = matchWrap(); ilp.topMargin = dp(16);
        container.addView(input, ilp);

        greetingView = new TextView(requireContext());
        greetingView.setTextSize(13);
        greetingView.setTypeface(KidsAdaptiveTypefaces.body(requireContext()));
        greetingView.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        greetingView.setGravity(Gravity.CENTER);
        updateGreeting(data().nickname);
        LinearLayout.LayoutParams glp = matchWrap(); glp.topMargin = dp(6); glp.bottomMargin = dp(10);
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

        // Age Question
        TextView ageQ = KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_age_question), 16);
        LinearLayout.LayoutParams aqlp = matchWrap(); aqlp.topMargin = dp(10); aqlp.bottomMargin = dp(8);
        container.addView(ageQ, aqlp);

        ageLabel = new TextView(requireContext());
        ageLabel.setTextSize(15);
        ageLabel.setTypeface(KidsAdaptiveTypefaces.heading(requireContext()), Typeface.BOLD);
        ageLabel.setTextColor(getResources().getColor(R.color.kids_adaptive_ink));
        ageLabel.setGravity(Gravity.CENTER);
        ageLabel.setBackgroundResource(R.drawable.kids_adaptive_bg_age_label_pill);
        int alPadH = dp(18), alPadV = dp(6);
        ageLabel.setPadding(alPadH, alPadV, alPadH, alPadV);
        LinearLayout centerWrap = new LinearLayout(requireContext());
        centerWrap.setGravity(Gravity.CENTER);
        centerWrap.addView(ageLabel, wrap());
        LinearLayout.LayoutParams cwlp = matchWrap(); cwlp.bottomMargin = dp(8);
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

        // Gender Question
        TextView genderQ = KidsAdaptiveUiHelpers.title(requireContext(), getString(R.string.kids_adaptive_gender_question), 16);
        LinearLayout.LayoutParams gqlp = matchWrap(); gqlp.topMargin = dp(16); gqlp.bottomMargin = dp(10);
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
            showAvatarsForGender(GIRL_AVATARS);
            host.pulseTeddy();
        });
        male.setOnClickListener(v -> {
            data().gender = "male";
            male.setSelectedState(true);
            female.setSelectedState(false);
            other.setSelectedState(false);
            showAvatarsForGender(BOY_AVATARS);
            host.pulseTeddy();
        });
        other.setOnClickListener(v -> {
            data().gender = "unspecified";
            other.setSelectedState(true);
            female.setSelectedState(false);
            male.setSelectedState(false);
            showAvatarsForGender(GENERAL_AVATARS);
            host.pulseTeddy();
        });

        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        int m = dp(4);
        flp.setMargins(m, 0, m, 0);

        genderRow.addView(female, flp);
        genderRow.addView(male, flp);
        genderRow.addView(other, flp);
        container.addView(genderRow, matchWrap());

        // --- Avatar Selection Section ---
        avatarTitle = KidsAdaptiveUiHelpers.title(requireContext(), "اختر صديقك المفضل ✨", 16);
        LinearLayout.LayoutParams avtlp = matchWrap(); avtlp.topMargin = dp(16); avtlp.bottomMargin = dp(8);
        avatarTitle.setVisibility(View.GONE);
        container.addView(avatarTitle, avtlp);

        avatarsScrollView = new HorizontalScrollView(requireContext());
        avatarsScrollView.setHorizontalScrollBarEnabled(false);
        avatarsScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        avatarsScrollView.setVisibility(View.GONE);

        avatarsContainer = new LinearLayout(requireContext());
        avatarsContainer.setOrientation(LinearLayout.HORIZONTAL);
        avatarsContainer.setGravity(Gravity.CENTER);
        avatarsScrollView.addView(avatarsContainer, wrap());

        LinearLayout.LayoutParams avslp = matchWrap(); avslp.bottomMargin = dp(10);
        container.addView(avatarsScrollView, avslp);

        if (data().gender != null) {
            if ("female".equals(data().gender)) showAvatarsForGender(GIRL_AVATARS);
            else if ("male".equals(data().gender)) showAvatarsForGender(BOY_AVATARS);
            else showAvatarsForGender(GENERAL_AVATARS);
        }

        scrollView.addView(container, matchWrap());
        mainContainer.addView(scrollView, matchWrap());
    }

    private void showAvatarsForGender(String[] avatars) {
        avatarTitle.setVisibility(View.VISIBLE);
        avatarsScrollView.setVisibility(View.VISIBLE);
        avatarsContainer.removeAllViews();
        avatarViews.clear();

        for (String avatar : avatars) {
            TextView tv = new TextView(requireContext());
            tv.setText(avatar);
            tv.setTextSize(28);
            tv.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(54), dp(54));
            lp.setMargins(dp(6), dp(4), dp(6), dp(4));

            tv.setOnClickListener(v -> {
                data().demoMoodSelected = avatar;
                highlightSelectedAvatar(tv);

                // 🌟 1. تحديث الدب في أعلى الشاشة فوراً
                if (teddyHeader != null) {
                    teddyHeader.setMood(data().getAvatarMoodFromSelection());
                }

                // 🌟 2. تشغيل تأثير النبض التفاعلي
                if (host != null) {
                    host.pulseTeddy();
                }

                // 🌟 3. حفظ الإيموجي المختار في SharedPreferences
                saveCurrentChildData();
            });

            if (avatar.equals(data().demoMoodSelected)) {
                highlightSelectedAvatar(tv);
            } else {
                unhighlightAvatar(tv);
            }

            avatarViews.add(tv);
            avatarsContainer.addView(tv, lp);
        }
    }

    private void highlightSelectedAvatar(TextView selectedTv) {
        for (TextView tv : avatarViews) {
            unhighlightAvatar(tv);
        }
        GradientDrawable selectedBg = new GradientDrawable();
        selectedBg.setShape(GradientDrawable.RECTANGLE);
        selectedBg.setCornerRadius(dp(16));
        selectedBg.setColor(Color.parseColor("#FFF0E6"));
        selectedBg.setStroke(dp(2), Color.parseColor("#F47C2B"));

        selectedTv.setBackground(selectedBg);
        selectedTv.setScaleX(1.1f);
        selectedTv.setScaleY(1.1f);
    }

    private void unhighlightAvatar(TextView tv) {
        GradientDrawable normalBg = new GradientDrawable();
        normalBg.setShape(GradientDrawable.RECTANGLE);
        normalBg.setCornerRadius(dp(16));
        normalBg.setColor(Color.parseColor("#FFFFFF"));
        normalBg.setStroke(dp(1), Color.parseColor("#E5E7EB"));

        tv.setBackground(normalBg);
        tv.setScaleX(1.0f);
        tv.setScaleY(1.0f);
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
    private void saveCurrentChildData() {
        String name = data().nickname != null ? data().nickname.trim() : "";
        String avatar = data().demoMoodSelected != null ? data().demoMoodSelected : "🐻";

        // 🌟 حفظ البيانات في SharedPreferences
        requireContext().getSharedPreferences("KidsApp", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("current_child_name", name)
                .putString("current_child_avatar", avatar)
                .apply();
    }
}