package com.example.graduationproject.models;

import android.net.Uri;

/**
 * Represents either an uploaded photo (type=UPLOAD, uri set)
 * or a chosen gradient preset (type=PRESET, preset set).
 * Mirrors the JS `photo` object: { type: "upload" | "preset", ... }
 */
public class CardPhoto {

    public enum Type { UPLOAD, PRESET }

    public final Type type;
    public final Uri uploadUri;   // used when type == UPLOAD
    public final Preset preset;   // used when type == PRESET

    private CardPhoto(Type type, Uri uploadUri, Preset preset) {
        this.type = type;
        this.uploadUri = uploadUri;
        this.preset = preset;
    }

    public static CardPhoto fromUpload(Uri uri) {
        return new CardPhoto(Type.UPLOAD, uri, null);
    }

    public static CardPhoto fromPreset(Preset preset) {
        return new CardPhoto(Type.PRESET, null, preset);
    }
}
