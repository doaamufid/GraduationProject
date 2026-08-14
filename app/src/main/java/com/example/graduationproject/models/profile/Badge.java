package com.example.graduationproject.models.profile;

public class Badge {
    public final int labelRes;
    public final int iconRes;
    public final boolean earned;
    public final int colorRes;

    public Badge(int labelRes, int iconRes, boolean earned, int colorRes) {
        this.labelRes = labelRes;
        this.iconRes = iconRes;
        this.earned = earned;
        this.colorRes = colorRes;
    }
}
