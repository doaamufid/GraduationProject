package com.example.graduationproject.ui;

/**
 * Mirrors { theme, fontSize, weight, brightness } from Reader's useState in the React source.
 * Held statically per-process so it also matches the React behaviour of resetting whenever
 * a fresh Reader mounts (React re-initializes useState on mount too, since Reader is
 * recreated per navigation) -- callers should construct a new instance per screen.
 */
public class ReaderSettings {
    public static final String THEME_CLASSIC = "classic";
    public static final String THEME_TRADITIONAL = "traditional";
    public static final String THEME_NIGHT = "night";
    public static final String THEME_TYPEWRITER = "typewriter";

    public static final String WEIGHT_NORMAL = "normal";
    public static final String WEIGHT_BOLD = "bold";

    public String theme = THEME_CLASSIC;
    public int fontSize = 16; // sp, one of {14,16,18,21}
    public String weight = WEIGHT_NORMAL;
    public int brightness = 100; // 20..100, mirrors CSS filter:brightness(%)
}
