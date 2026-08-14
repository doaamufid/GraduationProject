package com.example.graduationproject.models.profile.settings;

/** Equivalent of one entry in the THEMES constant. */
public class ThemeOption {
    public final String key;
    public final int colorInt;

    public ThemeOption(String key, int colorInt) {
        this.key = key;
        this.colorInt = colorInt;
    }
}
