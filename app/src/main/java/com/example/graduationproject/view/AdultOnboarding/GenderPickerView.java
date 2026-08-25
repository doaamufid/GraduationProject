package com.example.graduationproject.view.AdultOnboarding;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.AdultOnboardingUiUtils;
import com.example.graduationproject.models.AdultOnboarding.Option;

/** Mirrors <GenderPicker/>: two toggle cards + a "prefer not to say" text link. */
public class GenderPickerView extends LinearLayout {

    public interface OnGenderChange { void onChange(String genderId); }

    private String value;
    private OnGenderChange listener;
    private LinearLayout row;
    private TextView preferNot;

    public GenderPickerView(Context context) { super(context); init(); }
    public GenderPickerView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        row = new LinearLayout(getContext());
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        addView(row, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        preferNot = Widgets.textLink(getContext(), getContext().getString(com.example.graduationproject.R.string.adaptive_adult_onboarding_gender_prefer_not), Color.WHITE, () -> setValue("unspecified"));
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        lp.topMargin = AdultOnboardingUiUtils.dp(getContext(), 8);
        addView(preferNot, lp);

        render();
    }

    public void setOnGenderChange(OnGenderChange l) { this.listener = l; }

    public void setValue(String value) {
        this.value = value;
        render();
        if (listener != null) listener.onChange(value);
    }

    public void setValueSilently(String value) {
        this.value = value;
        render();
    }

    private void render() {
        row.removeAllViews();
        for (Option g : AdultOnboardingAppData.GENDER_OPTIONS) {
            boolean selected = g.id.equals(value);
            LinearLayout col = new LinearLayout(getContext());
            col.setOrientation(VERTICAL);
            col.setGravity(Gravity.CENTER);
            int w = AdultOnboardingUiUtils.dp(getContext(), 96);
            LayoutParams lp = new LayoutParams(w, LayoutParams.WRAP_CONTENT);
            int m = AdultOnboardingUiUtils.dp(getContext(), 6);
            lp.setMargins(m, m, m, m);
            col.setLayoutParams(lp);
            col.setPadding(AdultOnboardingUiUtils.dp(getContext(), 8), AdultOnboardingUiUtils.dp(getContext(), 16), AdultOnboardingUiUtils.dp(getContext(), 8), AdultOnboardingUiUtils.dp(getContext(), 16));

            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(AdultOnboardingUiUtils.dp(getContext(), 20));
            gd.setColor(selected ? Color.argb(41, 255, 227, 176) : Color.argb(15, 255, 255, 255));
            gd.setStroke(AdultOnboardingUiUtils.dp(getContext(), 1), selected ? AdultOnboardingAppData.GLOW : Color.argb(41, 255, 255, 255));
            col.setBackground(gd);

            TextView symbol = new TextView(getContext());
            symbol.setText(g.emoji);
            symbol.setTextSize(20);
            symbol.setTextColor(Color.WHITE);
            symbol.setGravity(Gravity.CENTER);
            int circle = AdultOnboardingUiUtils.dp(getContext(), 40);
            LayoutParams symLp = new LayoutParams(circle, circle);
            symLp.bottomMargin = AdultOnboardingUiUtils.dp(getContext(), 6);
            GradientDrawable symBg = new GradientDrawable();
            symBg.setShape(GradientDrawable.OVAL);
            symBg.setColor(selected ? AdultOnboardingAppData.GLOW : Color.argb(26, 255, 255, 255));
            symbol.setBackground(symBg);
            symbol.setLayoutParams(symLp);
            col.addView(symbol);

            TextView label = new TextView(getContext());
            label.setText(g.labelRes);
            label.setTextColor(Color.WHITE);
            label.setTextSize(13);
            label.setTypeface(AdultOnboardingUiUtils.tajawal(selected));
            col.addView(label);

            col.setOnClickListener(v -> setValue(g.id));
            col.animate().translationY(selected ? -AdultOnboardingUiUtils.dp(getContext(), 3) : 0).scaleX(selected ? 1.03f : 1f).scaleY(selected ? 1.03f : 1f).setDuration(240).start();
            row.addView(col);
        }
        String prefText = getContext().getString(com.example.graduationproject.R.string.adaptive_adult_onboarding_gender_prefer_not);
        preferNot.setText("unspecified".equals(value) ? ("Ã¢Å“â€œ " + prefText) : prefText);
    }
}
