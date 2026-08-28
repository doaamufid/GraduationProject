package com.example.graduationproject.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.models.kidsCalmDurationOption;

/** Mirrors the React <DurChips value onChange /> component. */
public final class kidsCalmDurChipsHelper {

    private kidsCalmDurChipsHelper() {}

    public interface OnPick { void onPick(String key); }

    public static void render(LinearLayout container, Context ctx, String selectedKey, OnPick onPick) {
        container.removeAllViews();
        kidsCalmAppState state = kidsCalmAppState.get();
        for (int i = 0; i < state.durOptions.size(); i++) {
            kidsCalmDurationOption d = state.durOptions.get(i);
            View chip = LayoutInflater.from(ctx).inflate(R.layout.kids_calm_item_dur_chip, container, false);
            TextView emoji = chip.findViewById(R.id.chipEmoji);
            TextView label = chip.findViewById(R.id.chipLabel);
            emoji.setText(d.emoji);
            label.setText(resolveLabel(ctx, d));

            boolean selected = d.key.equals(selectedKey);
            chip.setBackgroundResource(selected ? R.drawable.kids_calm_bg_dur_chip_selected : R.drawable.kids_calm_bg_dur_chip_idle);

            if (i == state.durOptions.size() - 1) {
                ((LinearLayout.LayoutParams) chip.getLayoutParams()).rightMargin = 0;
            }

            chip.setOnClickListener(v -> onPick.onPick(d.key));
            container.addView(chip);
        }
    }

    private static String resolveLabel(Context ctx, kidsCalmDurationOption d) {
        int resId = ctx.getResources().getIdentifier(d.labelResName, "string", ctx.getPackageName());
        return resId != 0 ? ctx.getString(resId) : d.key;
    }
}
