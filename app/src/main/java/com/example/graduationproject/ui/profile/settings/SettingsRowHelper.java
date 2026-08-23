package com.example.graduationproject.ui.profile.settings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationproject.AppLanguageManager;
import com.example.graduationproject.R;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Binds one {@code item_settings_row.xml} include, equivalent to the
 * original &lt;Row/&gt; component. Supports both variants used in the
 * source: a toggle-only row (no icon), and an icon+chevron navigation row.
 */
public final class SettingsRowHelper {

    private SettingsRowHelper() {
    }

    /** Toggle-only row (e.g. "وضع الإخفاء"): no icon box, a SwitchMaterial on the right slot. */
    public static SwitchMaterial bindToggleRow(View rowRoot, String title, String sub) {
        bindTextAndIcon(rowRoot, title, sub, 0, false);

        FrameLayout rightSlot = rowRoot.findViewById(R.id.rightSlot);
        rightSlot.removeAllViews();
        SwitchMaterial switchView = new SwitchMaterial(rowRoot.getContext());
        switchView.setLayoutDirection(AppLanguageManager.getLayoutDirection(rowRoot.getContext()));

        int primaryColor = rowRoot.getResources().getColor(R.color.primary);
        int trackColor = rowRoot.getResources().getColor(R.color.border);

        ColorStateList trackTint = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        primaryColor,
                        trackColor
                }
        );

        switchView.setTrackTintList(trackTint);
        switchView.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
        rightSlot.addView(switchView);

        rowRoot.setClickable(false);
        return switchView;
    }

    /** Navigation row (e.g. "إعدادات الإشعارات"): icon box + chevron, whole row clickable. */
    public static void bindNavRow(View rowRoot, String title, String sub, int iconRes, Runnable onClick) {
        bindTextAndIcon(rowRoot, title, sub, iconRes, true);

        FrameLayout rightSlot = rowRoot.findViewById(R.id.rightSlot);
        rightSlot.removeAllViews();
        ImageView chevron = new ImageView(rowRoot.getContext());
        chevron.setImageResource(R.drawable.ic_chevron_left);
        chevron.setScaleX(AppLanguageManager.isRtl(AppLanguageManager.getSavedLanguage(rowRoot.getContext())) ? -1f : 1f);
        rightSlot.addView(chevron);

        rowRoot.setOnClickListener(v -> onClick.run());
    }

    /** Navigation row without an icon (e.g. "عن سلام"): chevron only, whole row clickable. */
    public static void bindNavRowNoIcon(View rowRoot, String title, String sub, Runnable onClick) {
        bindTextAndIcon(rowRoot, title, sub, 0, false);

        FrameLayout rightSlot = rowRoot.findViewById(R.id.rightSlot);
        rightSlot.removeAllViews();
        ImageView chevron = new ImageView(rowRoot.getContext());
        chevron.setImageResource(R.drawable.ic_chevron_left);
        chevron.setScaleX(AppLanguageManager.isRtl(AppLanguageManager.getSavedLanguage(rowRoot.getContext())) ? -1f : 1f);
        rightSlot.addView(chevron);

        rowRoot.setOnClickListener(v -> onClick.run());
    }

    private static void bindTextAndIcon(View rowRoot, String title, String sub, int iconRes, boolean showIcon) {
        TextView tvTitle = rowRoot.findViewById(R.id.tvRowTitle);
        TextView tvSub = rowRoot.findViewById(R.id.tvRowSub);
        View iconBox = rowRoot.findViewById(R.id.iconBox);
        ImageView ivIcon = rowRoot.findViewById(R.id.ivRowIcon);

        tvTitle.setText(title);
        if (sub != null) {
            tvSub.setText(sub);
            tvSub.setVisibility(View.VISIBLE);
        } else {
            tvSub.setVisibility(View.GONE);
        }

        iconBox.setVisibility(showIcon ? View.VISIBLE : View.GONE);
        if (showIcon) {
            ivIcon.setImageResource(iconRes);
        }
    }

    /** Updates the icon box background tint and switch color based on theme selection. */
    public static void setThemeColor(View rowRoot, int colorInt) {
        View iconBoxBg = rowRoot.findViewById(R.id.iconBoxBg);
        if (iconBoxBg != null) {
            iconBoxBg.getBackground().mutate().setTint(colorInt);
        }

        FrameLayout rightSlot = rowRoot.findViewById(R.id.rightSlot);
        if (rightSlot != null && rightSlot.getChildCount() > 0 && rightSlot.getChildAt(0) instanceof SwitchMaterial) {
            SwitchMaterial switchView = (SwitchMaterial) rightSlot.getChildAt(0);
            int trackColorOff = rowRoot.getResources().getColor(R.color.border);
            ColorStateList trackTint = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            colorInt,
                            trackColorOff
                    }
            );
            switchView.setTrackTintList(trackTint);
        }
    }
}
