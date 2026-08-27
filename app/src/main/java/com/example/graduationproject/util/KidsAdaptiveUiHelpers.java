package com.example.graduationproject.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;

public final class KidsAdaptiveUiHelpers {

    private KidsAdaptiveUiHelpers() {}

    public static TextView title(Context ctx, String text, float sizeSp) {
        TextView t = new TextView(ctx);
        t.setText(text);
        t.setTextSize(sizeSp);
        t.setTypeface(KidsAdaptiveTypefaces.headingBold(ctx));
        t.setTextColor(ctx.getResources().getColor(R.color.kids_adaptive_ink));
        t.setGravity(Gravity.CENTER);
        return t;
    }

    public static TextView subtitle(Context ctx, String text) {
        TextView t = new TextView(ctx);
        t.setText(text);
        t.setTextSize(13.5f);
        t.setTypeface(KidsAdaptiveTypefaces.body(ctx));
        t.setTextColor(ctx.getResources().getColor(R.color.kids_adaptive_ink));
        t.setAlpha(0.75f);
        t.setGravity(Gravity.CENTER);
        t.setLineSpacing(0f, 1.6f);
        return t;
    }

    public static TextView body(Context ctx, String text) {
        TextView t = new TextView(ctx);
        t.setText(text);
        t.setTextSize(15f);
        t.setTypeface(KidsAdaptiveTypefaces.body(ctx));
        t.setTextColor(ctx.getResources().getColor(R.color.kids_adaptive_ink));
        t.setAlpha(0.85f);
        t.setGravity(Gravity.CENTER);
        t.setLineSpacing(0f, 1.7f);
        return t;
    }

    public static View spacer(Context ctx, int heightDp) {
        View v = new View(ctx);
        float d = ctx.getResources().getDisplayMetrics().density;
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (heightDp * d)));
        return v;
    }

    public static int dp(Context ctx, int v) {
        return (int) (v * ctx.getResources().getDisplayMetrics().density);
    }
}
