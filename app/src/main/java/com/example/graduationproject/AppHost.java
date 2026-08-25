package com.example.graduationproject;

/**
 * Small callback interface fragments use to talk back to MainActivity —
 * e.g. showing the bottom toast pill (React's `toast` state) or switching
 * tabs programmatically (React's `setTab("gallery")` calls from deep links
 * inside SimulateTab / DhikrTab).
 */
public interface AppHost {
    void showToast(String message);
    void switchTab(int index); // 0 = gallery, 1 = dhikr, 2 = simulate
    void refreshHeader();
}
