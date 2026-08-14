package com.example.graduationproject.models;

/**
 * Equivalent of one entry in the CAT_STYLE constant: the gradient art
 * background, the white category icon, the accent color (grad[1], used
 * for text/icon tinting), and the translucent badge background (grad[0] + "22").
 */
public class CategoryStyle {
    public final int artBackgroundRes;
    public final int iconRes;
    public final int accentColorRes;
    public final int badgeBgColorRes;

    public CategoryStyle(int artBackgroundRes, int iconRes, int accentColorRes, int badgeBgColorRes) {
        this.artBackgroundRes = artBackgroundRes;
        this.iconRes = iconRes;
        this.accentColorRes = accentColorRes;
        this.badgeBgColorRes = badgeBgColorRes;
    }
}
