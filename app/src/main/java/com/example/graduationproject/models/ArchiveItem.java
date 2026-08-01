package com.example.graduationproject.models;

/** Equivalent of one entry in the archive quick-links array. */
public class ArchiveItem {
    public final String label;
    public final String sub;
    public final int iconRes;
    public final int colorInt;

    public ArchiveItem(String label, String sub, int iconRes, int colorInt) {
        this.label = label;
        this.sub = sub;
        this.iconRes = iconRes;
        this.colorInt = colorInt;
    }
}
