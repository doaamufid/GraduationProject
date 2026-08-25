package com.example.graduationproject.models.AdultOnboarding;

/** Mirrors one entry of the JS `STAGES` array: the sky gradient + text color + star opacity. */
public class Stage {
    public final int fromColor;
    public final int toColor;
    public final int textColor;
    public final float starsOpacity;

    public Stage(int fromColor, int toColor, int textColor, float starsOpacity) {
        this.fromColor = fromColor;
        this.toColor = toColor;
        this.textColor = textColor;
        this.starsOpacity = starsOpacity;
    }
}
