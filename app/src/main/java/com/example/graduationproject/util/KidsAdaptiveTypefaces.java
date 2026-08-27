package com.example.graduationproject.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Central place for the two typefaces used across the app:
 *  - HEADING: mirrors 'Baloo Bhaijaan 2' (rounded, playful, used for titles/buttons/labels)
 *  - BODY:    mirrors 'Tajawal' (used for paragraphs / helper text)
 *
 * By default this falls back to the closest system fonts so the project builds and
 * runs immediately with no extra setup. For a pixel-perfect match:
 *   1) Download "Baloo Bhaijaan 2" and "Tajawal" .ttf files from Google Fonts.
 *   2) Place them at res/font/baloo_bhaijaan_2.ttf and res/font/tajawal.ttf
 *      (file names must be lowercase + underscores).
 *   3) Replace the two Typeface.create(...) calls below with:
 *      ResourcesCompat.getFont(context, R.font.baloo_bhaijaan_2) / R.font.tajawal
 */
public final class KidsAdaptiveTypefaces {

    private static Typeface heading;
    private static Typeface headingBold;
    private static Typeface body;

    private KidsAdaptiveTypefaces() {}

    public static Typeface heading(Context context) {
        if (heading == null) {
            heading = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        }
        return heading;
    }

    public static Typeface headingBold(Context context) {
        if (headingBold == null) {
            headingBold = Typeface.create("sans-serif-medium", Typeface.BOLD);
        }
        return headingBold;
    }

    public static Typeface body(Context context) {
        if (body == null) {
            body = Typeface.create("sans-serif", Typeface.NORMAL);
        }
        return body;
    }
}
