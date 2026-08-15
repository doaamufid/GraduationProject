package com.example.graduationproject.widget;

import android.view.View;

/** Equivalent of: style={{ transform: expanded ? "rotate(180deg)" : "rotate(0)", transition: "transform .25s" }} */
public final class ChevronRotator {

    private ChevronRotator() {
    }

    public static void setExpanded(View chevron, boolean expanded, boolean animate) {
        float target = expanded ? 180f : 0f;
        if (animate) {
            chevron.animate().rotation(target).setDuration(250).start();
        } else {
            chevron.setRotation(target);
        }
    }
}
