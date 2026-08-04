package com.example.graduationproject.models;

/** Equivalent of one entry in the ITEMS array constant. */
public class ContentItem {
    public final int id;
    public final String title;
    public final String src;      // author / source label
    public final String type;     // "فيديو" | "بودكاست"
    public final boolean isVideo; // true -> play icon, false -> headphones icon
    public final String duration;
    public final String category;
    public final String videoId;
    public final int gradStart;
    public final int gradEnd;
    public final String reason;

    public ContentItem(int id, String title, String src, String type, boolean isVideo,
                        String duration, String category, String videoId,
                        int gradStart, int gradEnd, String reason) {
        this.id = id;
        this.title = title;
        this.src = src;
        this.type = type;
        this.isVideo = isVideo;
        this.duration = duration;
        this.category = category;
        this.videoId = videoId;
        this.gradStart = gradStart;
        this.gradEnd = gradEnd;
        this.reason = reason;
    }
}
