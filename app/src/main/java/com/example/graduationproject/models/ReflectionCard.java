package com.example.graduationproject.models;

/**
 * Mirrors one entry of the CARDS array in the JSX (minus the Scene component
 * reference, which becomes a sceneType int resolved by SceneView).
 */
public class ReflectionCard {

    public final int sceneType;
    public final int titleRes;
    public final int tagRes;
    public final int chipRes;
    public final int dateRes;
    public final int noteRes;
    public final int iconRes;

    public ReflectionCard(int sceneType, int titleRes, int tagRes, int chipRes, int dateRes, int noteRes, int iconRes) {
        this.sceneType = sceneType;
        this.titleRes = titleRes;
        this.tagRes = tagRes;
        this.chipRes = chipRes;
        this.dateRes = dateRes;
        this.noteRes = noteRes;
        this.iconRes = iconRes;
    }
}
