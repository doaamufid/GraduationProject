package com.example.graduationproject.models;

import com.example.graduationproject.util.YouTubeUtils;

/** Equivalent of one entry in the ITEMS array constant. */
public class ContentItem {
    public final int id;
    public final String title;
    public final String src;      // author / source label
    public final String type;     // "فيديو" | "بودكاست"
    public final boolean isVideo; // true -> play icon, false -> headphones icon
    public final String duration;
    public final String category;
    public final String videoId;  // This should always be the clean 11-char ID
    public final String thumbnailUrl;
    public final String mediumThumbnailUrl;
    public final String defaultThumbnailUrl;
    public final int gradStart;
    public final int gradEnd;
    public final String reason;

    public ContentItem(int id, String title, String src, String type, boolean isVideo,
                        String duration, String category, String videoIdOrUrl,
                        String thumbnailUrl, String mediumThumbnailUrl, String defaultThumbnailUrl,
                        int gradStart, int gradEnd, String reason) {
        this.id = id;
        this.title = title;
        this.src = src;
        this.type = type;
        this.isVideo = isVideo;
        this.duration = duration;
        this.category = category;
        this.videoId = YouTubeUtils.extractVideoId(videoIdOrUrl);
        this.thumbnailUrl = thumbnailUrl;
        this.mediumThumbnailUrl = mediumThumbnailUrl;
        this.defaultThumbnailUrl = defaultThumbnailUrl;
        this.gradStart = gradStart;
        this.gradEnd = gradEnd;
        this.reason = reason;
    }

    /** Copy constructor for mapping while preserving existing fields. */
    public ContentItem(ContentItem other, String videoId, String thumbnailUrl, String mediumThumbnailUrl, String defaultThumbnailUrl, String reason) {
        this.id = other.id;
        this.title = other.title;
        this.src = other.src;
        this.type = other.type;
        this.isVideo = other.isVideo;
        this.duration = other.duration;
        this.category = other.category;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
        this.mediumThumbnailUrl = mediumThumbnailUrl;
        this.defaultThumbnailUrl = defaultThumbnailUrl;
        this.gradStart = other.gradStart;
        this.gradEnd = other.gradEnd;
        this.reason = reason;
    }
}
