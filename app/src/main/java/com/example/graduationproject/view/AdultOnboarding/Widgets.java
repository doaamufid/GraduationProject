package com.example.graduationproject.view.AdultOnboarding;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.AdultOnboardingAppData;
import com.example.graduationproject.AdultOnboardingUiUtils;

/**
 * Small reusable UI builders that translate the React file's stateless
 * presentational components (ChoiceCard, EmotionBubble, PrimaryButton,
 * TextLink, chips, time-period cards) into plain Android views, built
 * programmatically so selected/unselected styling can be recomputed on
 * every tap exactly like the JS inline-style approach.
 */
public final class Widgets {
    private Widgets() {}

    public static int dp(Context ctx, float v) { return AdultOnboardingUiUtils.dp(ctx, v); }

    // ---------------- ChoiceCard ----------------
    public static View choiceCard(Context ctx, String emoji, String label, String sub, boolean selected, Runnable onClick) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(ctx, 16), padV = dp(ctx, 13);
        row.setPadding(padH, padV, padH, padV);
        row.setBackground(cardBackground(ctx, selected));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(ctx, 10);
        row.setLayoutParams(lp);

        if (emoji != null) {
            TextView em = new TextView(ctx);
            em.setText(emoji);
            em.setTextSize(20);
            em.setPadding(0, 0, dp(ctx, 10), 0);
            row.addView(em);
        }
        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(Color.WHITE);
        title.setTextSize(15.5f);
        title.setTypeface(AdultOnboardingUiUtils.tajawal(selected));
        title.setTypeface(title.getTypeface(), selected ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        textCol.addView(title);
        if (sub != null) {
            TextView subTv = new TextView(ctx);
            subTv.setText(sub);
            subTv.setTextColor(Color.WHITE);
            subTv.setAlpha(0.65f);
            subTv.setTextSize(12.5f);
            textCol.addView(subTv);
        }
        row.addView(textCol);
        row.setOnClickListener(v -> onClick.run());
        row.setClickable(true);
        row.setFocusable(true);
        row.animate().translationY(selected ? -dp(ctx, 1) : 0).setDuration(220).start();
        return row;
    }

    private static GradientDrawable cardBackground(Context ctx, boolean selected) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(ctx, 16));
        gd.setColor(selected ? Color.argb(41, 255, 227, 176) : Color.argb(15, 255, 255, 255));
        gd.setStroke(Math.max(1, dp(ctx, 1.5f)), selected ? AdultOnboardingAppData.GLOW : Color.argb(41, 255, 255, 255));
        return gd;
    }

    // ---------------- Light theme chip (Goals screen Ã¢â‚¬â€ cream sky background) ----------------
    public static View lightChip(Context ctx, String emoji, String label, boolean selected, Runnable onClick) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(ctx, 18), padV = dp(ctx, 11);
        row.setPadding(padH, padV, padH, padV);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(ctx, 999));
        if (selected) {
            gd.setOrientation(GradientDrawable.Orientation.TL_BR);
            gd.setColors(new int[]{AdultOnboardingAppData.GLOW, AdultOnboardingAppData.DAWN_2});
        } else {
            gd.setColor(Color.argb(140, 255, 255, 255));
            gd.setStroke(dp(ctx, 1), Color.argb(20, 0, 0, 0));
        }
        row.setBackground(gd);
        row.setElevation(selected ? dp(ctx, 3) : dp(ctx, 1));

        TextView em = new TextView(ctx);
        em.setText(emoji);
        em.setTextSize(16);
        em.setPadding(0, 0, dp(ctx, 8), 0);
        row.addView(em);

        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(AdultOnboardingAppData.INK);
        title.setTextSize(13.5f);
        title.setTypeface(AdultOnboardingUiUtils.tajawal(selected));
        row.addView(title);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = dp(ctx, 5);
        lp.setMargins(m, m, m, m);
        row.setLayoutParams(lp);

        row.setOnClickListener(v -> onClick.run());
        row.animate().translationY(selected ? -dp(ctx, 2) : 0).scaleX(selected ? 1.03f : 1f).scaleY(selected ? 1.03f : 1f).setDuration(240).start();
        return row;
    }

    // ---------------- Dark theme small pill (timeline followups) ----------------
    public static View darkPill(Context ctx, String label, boolean selected, Runnable onClick) {
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(13);
        int padH = dp(ctx, 14), padV = dp(ctx, 8);
        tv.setPadding(padH, padV, padH, padV);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(ctx, 999));
        gd.setColor(selected ? Color.argb(51, 255, 227, 176) : Color.TRANSPARENT);
        gd.setStroke(dp(ctx, 1), selected ? AdultOnboardingAppData.GLOW : Color.argb(46, 255, 255, 255));
        tv.setBackground(gd);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = dp(ctx, 4);
        lp.setMargins(m, m, m, m);
        tv.setLayoutParams(lp);
        tv.setOnClickListener(v -> onClick.run());
        return tv;
    }

    // ---------------- Emotion bubble (circular) ----------------
    public static View emotionBubble(Context ctx, String emoji, String label, boolean selected, Runnable onClick) {
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        int size = dp(ctx, 92);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        int m = dp(ctx, 4);
        lp.setMargins(m, m, m, m);
        col.setLayoutParams(lp);

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        if (selected) {
            gd.setColor(Color.argb(64, 255, 227, 176));
            gd.setStroke(dp(ctx, 1), AdultOnboardingAppData.GLOW);
        } else {
            gd.setColor(Color.argb(13, 255, 255, 255));
            gd.setStroke(dp(ctx, 1), Color.argb(36, 255, 255, 255));
        }
        col.setBackground(gd);

        TextView em = new TextView(ctx);
        em.setText(emoji);
        em.setTextSize(26);
        em.setGravity(Gravity.CENTER);
        col.addView(em);

        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(Color.WHITE);
        title.setTextSize(11.5f);
        title.setTypeface(AdultOnboardingUiUtils.tajawal(true));
        title.setGravity(Gravity.CENTER);
        col.addView(title);

        col.setOnClickListener(v -> onClick.run());
        col.animate().translationY(selected ? -dp(ctx, 6) : 0).scaleX(selected ? 1.06f : 1f).scaleY(selected ? 1.06f : 1f).setDuration(260).start();
        return col;
    }

    // ---------------- Time period card ----------------
    public static View timePeriodCard(Context ctx, int sceneRes, String label, boolean selected, Runnable onClick) {
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        int padV = dp(ctx, 22), padH = dp(ctx, 8);
        col.setPadding(padH, padV, padH, padV);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(ctx, 22));
        gd.setColor(selected ? Color.argb(41, 255, 227, 176) : Color.argb(15, 255, 255, 255));
        gd.setStroke(dp(ctx, 1), selected ? AdultOnboardingAppData.GLOW : Color.argb(36, 255, 255, 255));
        col.setBackground(gd);

        android.widget.ImageView iv = new android.widget.ImageView(ctx);
        iv.setImageResource(sceneRes);
        int circle = dp(ctx, 64);
        LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(circle, circle);
        ivLp.bottomMargin = dp(ctx, 10);
        iv.setLayoutParams(ivLp);
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(selected ? Color.argb(46, 255, 255, 255) : Color.argb(20, 255, 255, 255));
        iv.setBackground(circleBg);
        iv.setPadding(dp(ctx, 12), dp(ctx, 12), dp(ctx, 12), dp(ctx, 12));
        col.addView(iv);

        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(Color.WHITE);
        title.setTextSize(14);
        title.setTypeface(AdultOnboardingUiUtils.tajawal(selected));
        col.addView(title);

        col.setOnClickListener(v -> onClick.run());
        col.animate().translationY(selected ? -dp(ctx, 3) : 0).scaleX(selected ? 1.02f : 1f).scaleY(selected ? 1.02f : 1f).setDuration(260).start();
        return col;
    }

    // ---------------- Primary button ----------------
    public static android.widget.Button primaryButton(Context ctx, String text, boolean darkGlow, Runnable onClick) {
        android.widget.Button btn = new android.widget.Button(ctx);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextColor(AdultOnboardingAppData.INK);
        btn.setTextSize(16);
        btn.setTypeface(AdultOnboardingUiUtils.cairo(true));
        btn.setBackgroundResource(darkGlow ? com.example.graduationproject.R.drawable.bg_button_glow : com.example.graduationproject.R.drawable.bg_button_light);
        btn.setElevation(dp(ctx, 6));
        btn.setStateListAnimator(null);
        btn.setPadding(dp(ctx, 18), dp(ctx, 15), dp(ctx, 18), dp(ctx, 15));
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 56));
        btn.setLayoutParams(lp);
        btn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(120).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    break;
            }
            return false;
        });
        btn.setOnClickListener(v -> onClick.run());
        return btn;
    }

    // ---------------- Typography helpers ----------------
    public static TextView heading(Context ctx, String text, int colorRes) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(colorRes);
        tv.setTextSize(20);
        tv.setTypeface(AdultOnboardingUiUtils.cairo(true));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    public static TextView subtext(Context ctx, String text, int colorRes) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(colorRes);
        tv.setAlpha(0.78f);
        tv.setTextSize(13.5f);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(AdultOnboardingUiUtils.tajawal(false));
        return tv;
    }

    public static TextView paragraph(Context ctx, String text, int colorRes) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(colorRes);
        tv.setAlpha(0.9f);
        tv.setTextSize(14.5f);
        tv.setLineSpacing(0, 1.35f);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(AdultOnboardingUiUtils.tajawal(false));
        return tv;
    }

    // ---------------- Text link ----------------
    public static TextView textLink(Context ctx, String text, int color, Runnable onClick) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setAlpha(0.75f);
        tv.setTextSize(14);
        tv.setPaintFlags(tv.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        int pad = dp(ctx, 6);
        tv.setPadding(pad, pad, pad, pad);
        tv.setOnClickListener(v -> onClick.run());
        return tv;
    }
}
