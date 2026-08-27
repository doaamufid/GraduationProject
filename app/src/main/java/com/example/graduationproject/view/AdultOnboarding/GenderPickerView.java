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
            int m = AdultOnboardingUiUtils.dp(getContext(), 10);
            lp.setMargins(m, m, m, m);
            col.setLayoutParams(lp);
            col.setPadding(AdultOnboardingUiUtils.dp(getContext(), 8), AdultOnboardingUiUtils.dp(getContext(), 16), AdultOnboardingUiUtils.dp(getContext(), 8), AdultOnboardingUiUtils.dp(getContext(), 16));

            GradientDrawable gd = new GradientDrawable();
            // Blob radius
            gd.setCornerRadii(new float[]{
                AdultOnboardingUiUtils.dp(getContext(), 30), AdultOnboardingUiUtils.dp(getContext(), 30),
                AdultOnboardingUiUtils.dp(getContext(), 20), AdultOnboardingUiUtils.dp(getContext(), 20),
                AdultOnboardingUiUtils.dp(getContext(), 40), AdultOnboardingUiUtils.dp(getContext(), 40),
                AdultOnboardingUiUtils.dp(getContext(), 25), AdultOnboardingUiUtils.dp(getContext(), 25)
            });

            if (selected) {
                gd.setColor(Color.WHITE);
                gd.setStroke(AdultOnboardingUiUtils.dp(getContext(), 2.5f), AdultOnboardingAppData.INK);
            } else {
                gd.setColor(Color.argb(160, 255, 255, 255));
                gd.setStroke(AdultOnboardingUiUtils.dp(getContext(), 1), Color.argb(30, 0, 0, 0));
            }
            col.setBackground(gd);

            TextView symbol = new TextView(getContext());
            symbol.setText(g.emoji);
            symbol.setTextSize(32); // Bigger icon
            symbol.setTextColor(AdultOnboardingAppData.INK); // Changed to ink
            symbol.setGravity(Gravity.CENTER);
            int circle = AdultOnboardingUiUtils.dp(getContext(), 48);
            LayoutParams symLp = new LayoutParams(circle, circle);
            symLp.bottomMargin = AdultOnboardingUiUtils.dp(getContext(), 6);
            symbol.setLayoutParams(symLp);
            col.addView(symbol);

            TextView label = new TextView(getContext());
            label.setText(g.labelRes);
            label.setTextColor(AdultOnboardingAppData.INK); // Changed to ink
            label.setTextSize(14.5f);
            label.setTypeface(AdultOnboardingUiUtils.tajawal(selected));
            col.addView(label);

            col.setOnClickListener(v -> setValue(g.id));
            row.addView(col);
        }
        String prefText = getContext().getString(com.example.graduationproject.R.string.adaptive_adult_onboarding_gender_prefer_not);
        preferNot.setText("unspecified".equals(value) ? ("Ã¢Å“â€œ " + prefText) : prefText);
    }
}
