package com.example.graduationproject.models;

public class PhotoItem {
    public final long id;
    public final String uri; // content:// or file:// URI as string
    public final String caption;

    public PhotoItem(long id, String uri, String caption) {
        this.id = id;
        this.uri = uri;
        this.caption = caption;
    }
}
