package com.example.graduationproject.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads custom fonts (Tajawal / Cormorant Garamond / Poppins — the same
 * families used in the web version) from assets/fonts/ if the developer has
 * placed the .ttf files there. Falls back gracefully to the closest system
 * typeface so the app still builds and runs perfectly without them.
 *
 * To get a pixel-perfect match, download the free fonts and drop them into:
 *   app/src/main/assets/fonts/tajawal_regular.ttf
 *   app/src/main/assets/fonts/cormorant_italic.ttf
 *   app/src/main/assets/fonts/cormorant_bold.ttf
 */
public class QouteFeatureUtils {

    private static final Map<String, Typeface> CACHE = new HashMap<>();

    public static Typeface getFont(Context context, String assetFileName) {
        if (CACHE.containsKey(assetFileName)) return CACHE.get(assetFileName);

        Typeface result;
        try {
            result = Typeface.createFromAsset(context.getAssets(), "fonts/" + assetFileName);
        } catch (Exception e) {
            if (assetFileName.contains("cormorant")) {
                boolean bold = assetFileName.contains("bold");
                result = Typeface.create(Typeface.SERIF, bold ? Typeface.BOLD : Typeface.ITALIC);
            } else {
                result = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
            }
        }
        CACHE.put(assetFileName, result);
        return result;
    }
}
