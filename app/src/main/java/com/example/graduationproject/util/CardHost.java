package com.example.graduationproject.util;

import android.widget.FrameLayout;

public interface CardHost {
    FrameLayout getToastOverlay();

    void copyToClipboard(String text);

    void onPinnedCountChanged();
}
