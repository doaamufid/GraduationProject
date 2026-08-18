package com.example.graduationproject.models;

import android.content.Context;
import com.example.graduationproject.R;

public class ArticleCategory {

    public static final String ALL = "All";
    public static final String STRENGTH = "Strength";
    public static final String HOPE = "Hope";
    public static final String PATIENCE = "Patience";
    public static final String GRATITUDE = "Gratitude";

    public static final String[] ALL_TABS = {ALL, STRENGTH, HOPE, PATIENCE, GRATITUDE};
    public static final String[] ALL_ICONS = {"🌟", "💪", "🌈", "⏳", "🙏"};

    public static String getLabel(String category) {
        switch (category) {
            case STRENGTH: return "قوة";
            case HOPE: return "أمل";
            case PATIENCE: return "صبر";
            case GRATITUDE: return "شكر";
            default: return "الكل";
        }
    }

    public static String englishLabel(String category) {
        return category;
    }

    public static String getIcon(String category) {
        for (int i = 0; i < ALL_TABS.length; i++) {
            if (ALL_TABS[i].equals(category)) return ALL_ICONS[i];
        }
        return "✨";
    }

    public static int[] gradientColors(Context ctx, String category) {
        // Keeping some color mapping for other UI parts if needed
        switch (category) {
            case STRENGTH: return new int[]{color(ctx, R.color.cat_cbt_1), color(ctx, R.color.cat_cbt_2)};
            case HOPE: return new int[]{color(ctx, R.color.cat_anxiety_1), color(ctx, R.color.cat_anxiety_2)};
            case PATIENCE: return new int[]{color(ctx, R.color.cat_sleep_1), color(ctx, R.color.cat_sleep_2)};
            case GRATITUDE: return new int[]{color(ctx, R.color.cat_sadness_1), color(ctx, R.color.cat_sadness_2)};
            default: return new int[]{color(ctx, R.color.primary), color(ctx, R.color.primaryDark)};
        }
    }

    private static int color(Context ctx, int resId) {
        return ctx.getResources().getColor(resId);
    }
}
