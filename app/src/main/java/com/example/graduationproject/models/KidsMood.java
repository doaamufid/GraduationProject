package com.example.graduationproject.models;

/** Plain data holder for one mood option (mirrors KID_MOODS in the React source). */
public class KidsMood {
    public final String id;
    public final String label;
    public final String bearType;
    public final int bgColor;
    public final int accentColor;

    public KidsMood(String id, String label, String bearType, int bgColor, int accentColor) {
        this.id = id;
        this.label = label;
        this.bearType = bearType;
        this.bgColor = bgColor;
        this.accentColor = accentColor;
    }
}
