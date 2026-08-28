package com.example.graduationproject.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.graduationproject.R;

/**
 * Small shared helper that builds pill-shaped chip TextViews, used for both
 * the duration selector (1/2/3/5 minutes) and the dhikr category selector —
 * equivalent to <DurationChips/> and the category-chip row in the JS sheet.
 */
public class ChipRowHelper {

    public interface OnChipClick { void onClick(int index); }

    public static void buildDurationChips(Context ctx, LinearLayout container, int[] options,
                                           int selectedValue, OnChipClick listener) {
        container.removeAllViews();
        container.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < options.length; i++) {
            int value = options[i];
            TextView chip = new TextView(ctx);
            chip.setText(value + " " + (value == 1
                    ? ctx.getString(R.string.minute_1)
                    : ctx.getString(R.string.minutes_n)));
            styleChip(ctx, chip, value == selectedValue);
            final int idx = i;
            chip.setOnClickListener(v -> listener.onClick(idx));
            container.addView(chip);
        }
    }

    public static void buildCategoryChips(Context ctx, LinearLayout container, String[] keys,
                                           String selectedKey, java.util.Map<String, com.example.graduationproject.models.CategoryMeta> meta,
                                           OnChipClick listener) {
        container.removeAllViews();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            TextView chip = new TextView(ctx);
            chip.setText(meta.get(key).emoji + " " + meta.get(key).label);
            styleChip(ctx, chip, key.equals(selectedKey));
            final int idx = i;
            chip.setOnClickListener(v -> listener.onClick(idx));
            container.addView(chip);
        }
    }

    private static void styleChip(Context ctx, TextView chip, boolean selected) {
        float density = ctx.getResources().getDisplayMetrics().density;
        chip.setPadding((int) (13 * density), (int) (7 * density), (int) (13 * density), (int) (7 * density));
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f);
        chip.setTypeface(chip.getTypeface(), android.graphics.Typeface.BOLD);
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_plain);
        chip.setTextColor(ctx.getResources().getColor(selected ? R.color.text_main : R.color.text_soft));
        chip.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd((int) (7 * density));
        lp.bottomMargin = (int) (7 * density);
        chip.setLayoutParams(lp);
    }
}
