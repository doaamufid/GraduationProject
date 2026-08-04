package com.example.graduationproject.models;

/** Equivalent of one entry in the BADGES constant. */
public class Badge {
    public final int id;
    public final String label;
    public final int iconRes;
    public final boolean earned;
    public final int colorInt;
    public final String need;

    public Badge(int id, String label, int iconRes, boolean earned, int colorInt, String need) {
        this.id = id;
        this.label = label;
        this.iconRes = iconRes;
        this.earned = earned;
        this.colorInt = colorInt;
        this.need = need;
    }
}
