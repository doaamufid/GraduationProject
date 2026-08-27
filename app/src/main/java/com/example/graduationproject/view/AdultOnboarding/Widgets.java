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
    public static View choiceCard(Context ctx, String emoji, String label, String sub, int baseColor, boolean selected, Runnable onClick) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(ctx, 18), padV = dp(ctx, 14);
        row.setPadding(padH, padV, padH, padV);

        GradientDrawable gd = new GradientDrawable();
        // Dynamic blob-like radius
        gd.setCornerRadii(new float[]{
                dp(ctx, 40), dp(ctx, 40),
                dp(ctx, 25), dp(ctx, 25),
                dp(ctx, 35), dp(ctx, 35),
                dp(ctx, 20), dp(ctx, 20)
        });

        if (selected) {
            gd.setColor(baseColor != 0 ? baseColor : Color.WHITE);
            gd.setStroke(dp(ctx, 2f), AdultOnboardingAppData.INK);
        } else {
            gd.setColor(baseColor != 0 ? Color.argb(180, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor)) : Color.argb(15, 255, 255, 255));
            gd.setStroke(dp(ctx, 1), Color.argb(30, 0, 0, 0));
        }
        row.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(ctx, 12);
        row.setLayoutParams(lp);

        if (emoji != null) {
            TextView em = new TextView(ctx);
            em.setText(emoji);
            em.setTextSize(26); // Smaller icon
            em.setPadding(0, 0, dp(ctx, 12), 0);
            row.addView(em);
        }
        LinearLayout textCol = new LinearLayout(ctx);
        textCol.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(AdultOnboardingAppData.INK);
        title.setTextSize(15.5f);
        title.setTypeface(AdultOnboardingUiUtils.tajawal(true));
        textCol.addView(title);
        if (sub != null) {
            TextView subTv = new TextView(ctx);
            subTv.setText(sub);
            subTv.setTextColor(AdultOnboardingAppData.INK);
            subTv.setAlpha(0.7f);
            subTv.setTextSize(12.5f);
            textCol.addView(subTv);
        }
        row.addView(textCol, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        if (selected) {
            TextView check = new TextView(ctx);
            check.setText("✓");
            check.setTextColor(AdultOnboardingAppData.INK);
            check.setTextSize(14);
            row.addView(check);
        }

        row.setOnClickListener(v -> onClick.run());
        row.setClickable(true);
        row.setFocusable(true);
        return row;
    }

    private static GradientDrawable cardBackground(Context ctx, boolean selected) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(ctx, 16));
        gd.setColor(selected ? Color.argb(41, 255, 227, 176) : Color.argb(15, 255, 255, 255));
        gd.setStroke(Math.max(1, dp(ctx, 1.5f)), selected ? AdultOnboardingAppData.GLOW : Color.argb(41, 255, 255, 255));
        return gd;
    }

    // ---------------- Light theme chip (Goals screen — cream sky background) ----------------
    public static View lightChip(Context ctx, String emoji, String label, int baseColor, boolean selected, Runnable onClick) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int padH = dp(ctx, 16), padV = dp(ctx, 12);
        row.setPadding(padH, padV, padH, padV);

        GradientDrawable gd = new GradientDrawable();
        // Dynamic blob-like radius
        gd.setCornerRadii(new float[]{
                dp(ctx, 35), dp(ctx, 35),
                dp(ctx, 18), dp(ctx, 18),
                dp(ctx, 45), dp(ctx, 45),
                dp(ctx, 25), dp(ctx, 25)
        });

        if (selected) {
            gd.setColor(baseColor != 0 ? baseColor : Color.WHITE);
            gd.setStroke(dp(ctx, 2f), AdultOnboardingAppData.INK);
        } else {
            gd.setColor(baseColor != 0 ? Color.argb(200, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor)) : Color.argb(140, 255, 255, 255));
            gd.setStroke(dp(ctx, 1), Color.argb(30, 0, 0, 0));
        }
        row.setBackground(gd);

        if (emoji != null) {
            TextView em = new TextView(ctx);
            em.setText(emoji);
            em.setTextSize(22); // Smaller icon
            em.setPadding(0, 0, dp(ctx, 10), 0);
            row.addView(em);
        }

        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(AdultOnboardingAppData.INK);
        title.setTextSize(14.5f); // Smaller text
        title.setTypeface(AdultOnboardingUiUtils.tajawal(true));
        row.addView(title);

        if (selected) {
            TextView check = new TextView(ctx);
            check.setText("✓");
            check.setTextColor(AdultOnboardingAppData.INK);
            check.setTextSize(13);
            check.setPadding(dp(ctx, 6), 0, 0, 0);
            row.addView(check);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = dp(ctx, 8);
        lp.setMargins(m, m, m, m);
        row.setLayoutParams(lp);

        row.setOnClickListener(v -> onClick.run());
        return row;
    }

    // ---------------- Dark theme small pill (timeline followups) ----------------
    public static View darkPill(Context ctx, String label, boolean selected, Runnable onClick) {
        TextView tv = new TextView(ctx);
        tv.setText(label);
        tv.setTextColor(AdultOnboardingAppData.INK); // Changed to ink
        tv.setTextSize(14);
        int padH = dp(ctx, 16), padV = dp(ctx, 10);
        tv.setPadding(padH, padV, padH, padV);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(ctx, 999));

        if (selected) {
            gd.setColor(Color.WHITE);
            gd.setStroke(dp(ctx, 2f), AdultOnboardingAppData.INK);
        } else {
            gd.setColor(Color.argb(40, 33, 27, 51));
            gd.setStroke(dp(ctx, 1), Color.argb(40, 33, 27, 51));
        }
        tv.setBackground(gd);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = dp(ctx, 6);
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
        int size = dp(ctx, 100); // Smaller size
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        int m = dp(ctx, 8);
        lp.setMargins(m, m, m, m);
        col.setLayoutParams(lp);

        GradientDrawable gd = new GradientDrawable();
        // Dynamic blob-like radius
        gd.setCornerRadii(new float[]{
                dp(ctx, 45), dp(ctx, 45),
                dp(ctx, 35), dp(ctx, 35),
                dp(ctx, 55), dp(ctx, 55),
                dp(ctx, 30), dp(ctx, 30)
        });

        if (selected) {
            gd.setColor(Color.WHITE);
            gd.setStroke(dp(ctx, 2f), AdultOnboardingAppData.INK);
        } else {
            gd.setColor(Color.argb(160, 255, 255, 255));
            gd.setStroke(dp(ctx, 1), Color.argb(30, 0, 0, 0));
        }
        col.setBackground(gd);

        TextView em = new TextView(ctx);
        em.setText(emoji);
        em.setTextSize(36); // Smaller icon
        em.setGravity(Gravity.CENTER);
        col.addView(em);

        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(AdultOnboardingAppData.INK);
        title.setTextSize(12.5f);
        title.setTypeface(AdultOnboardingUiUtils.tajawal(true));
        title.setGravity(Gravity.CENTER);
        col.addView(title);

        col.setOnClickListener(v -> onClick.run());
        return col;
    }

    // ---------------- Time period card ----------------
    public static View timePeriodCard(Context ctx, int sceneRes, String label, boolean selected, Runnable onClick) {
        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        int padV = dp(ctx, 20), padH = dp(ctx, 10);
        col.setPadding(padH, padV, padH, padV);

        GradientDrawable gd = new GradientDrawable();
        // Blob radius
        gd.setCornerRadii(new float[]{
                dp(ctx, 40), dp(ctx, 40),
                dp(ctx, 20), dp(ctx, 20),
                dp(ctx, 50), dp(ctx, 50),
                dp(ctx, 25), dp(ctx, 25)
        });

        if (selected) {
            gd.setColor(Color.WHITE);
            gd.setStroke(dp(ctx, 2f), AdultOnboardingAppData.INK);
        } else {
            gd.setColor(Color.argb(160, 255, 255, 255));
            gd.setStroke(dp(ctx, 1), Color.argb(30, 0, 0, 0));
        }
        col.setBackground(gd);

        android.widget.ImageView iv = new android.widget.ImageView(ctx);
        iv.setImageResource(sceneRes);
        int size = dp(ctx, 54); // Smaller size
        LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(size, size);
        ivLp.bottomMargin = dp(ctx, 10);
        iv.setLayoutParams(ivLp);
        col.addView(iv);

        TextView title = new TextView(ctx);
        title.setText(label);
        title.setTextColor(AdultOnboardingAppData.INK); // Changed to ink
        title.setTextSize(14);
        title.setTypeface(AdultOnboardingUiUtils.tajawal(true));
        col.addView(title);

        col.setOnClickListener(v -> onClick.run());
        return col;
    }

    // ---------------- Primary button ----------------
    public static android.widget.Button primaryButton(Context ctx, String text, boolean darkGlow, Runnable onClick) {
        com.google.android.material.button.MaterialButton btn = new com.google.android.material.button.MaterialButton(ctx);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextColor(AdultOnboardingAppData.INK);
        btn.setTextSize(16);
        btn.setTypeface(AdultOnboardingUiUtils.cairo(true));

        // Use custom background and ensure MaterialButton doesn't overwrite it with tint
        btn.setBackgroundTintList(null);
        btn.setBackgroundResource(darkGlow ? com.example.graduationproject.R.drawable.bg_button_glow : com.example.graduationproject.R.drawable.bg_button_light);

        btn.setElevation(dp(ctx, 8));
        btn.setTranslationZ(dp(ctx, 2));
        btn.setPadding(dp(ctx, 18), dp(ctx, 15), dp(ctx, 18), dp(ctx, 15));

        android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(ctx, 56));
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
        tv.setTextSize(22);
        tv.setTypeface(AdultOnboardingUiUtils.cairo(true));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    public static TextView subtext(Context ctx, String text, int colorRes) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(colorRes);
        tv.setAlpha(0.85f);
        tv.setTextSize(14.5f);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(AdultOnboardingUiUtils.tajawal(false));
        return tv;
    }

    public static TextView paragraph(Context ctx, String text, int colorRes) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(colorRes);
        tv.setAlpha(0.9f);
        tv.setTextSize(15.5f);
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
        tv.setAlpha(0.8f);
        tv.setTextSize(14);
        tv.setPaintFlags(tv.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        int pad = dp(ctx, 6);
        tv.setPadding(pad, pad, pad, pad);
        tv.setOnClickListener(v -> onClick.run());
        return tv;
    }

    public static void startPulse(View v) {
        android.animation.ObjectAnimator scaleX = android.animation.ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.15f, 1f);
        android.animation.ObjectAnimator scaleY = android.animation.ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.15f, 1f);
        scaleX.setDuration(2500);
        scaleY.setDuration(2500);
        scaleX.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        scaleY.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        scaleX.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        android.animation.AnimatorSet set = new android.animation.AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.start();
    }
}
