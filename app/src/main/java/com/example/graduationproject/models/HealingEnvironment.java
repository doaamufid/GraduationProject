package com.example.graduationproject.models;

import java.util.List;

/** Equivalent of one entry in the ENVIRONMENTS constant. */
public class HealingEnvironment {
    public final String key;
    public final String label;
    public final String tag;
    public final int iconRes;
    public final int gradientBackgroundRes;
    public final int gifRes;
    public final List<SoundLayer> layers;

    public HealingEnvironment(String key, String label, String tag, int iconRes,
                              int gradientBackgroundRes, int gifRes, List<SoundLayer> layers) {
        this.key = key;
        this.label = label;
        this.tag = tag;
        this.iconRes = iconRes;
        this.gradientBackgroundRes = gradientBackgroundRes;
        this.gifRes = gifRes;
        this.layers = layers;
    }
}
