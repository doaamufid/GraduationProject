package com.example.graduationproject.models;

import android.net.Uri;

/** Mirrors a single item of INITIAL_KID_CARDS / the "cards" state array. */
public class kidsCalmKidCardModel {
    public long id;
    public String phrase;
    public kidsCalmSticker sticker;   // null when a photo is used
    public Uri photoUri;      // null when a sticker is used
    public boolean active;

    public kidsCalmKidCardModel(long id, String phrase, kidsCalmSticker sticker, Uri photoUri, boolean active) {
        this.id = id;
        this.phrase = phrase;
        this.sticker = sticker;
        this.photoUri = photoUri;
        this.active = active;
    }
}
