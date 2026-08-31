package com.example.graduationproject.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class YouTubeResponse {
    @SerializedName("items")
    public List<YouTubeItem> items;

    public static class YouTubeItem {
        @SerializedName("id")
        public Object id; // In search.list it's an object, in videos.list it's a string
        
        @SerializedName("snippet")
        public Snippet snippet;
        @SerializedName("status")
        public Status status;

        public String getVideoId() {
            if (id instanceof String) {
                return (String) id;
            } else if (id instanceof com.google.gson.internal.LinkedTreeMap) {
                com.google.gson.internal.LinkedTreeMap map = (com.google.gson.internal.LinkedTreeMap) id;
                return (String) map.get("videoId");
            }
            return null;
        }
    }

    public static class Snippet {
        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("channelTitle")
        public String channelTitle;
        @SerializedName("thumbnails")
        public Thumbnails thumbnails;
    }

    public static class Thumbnails {
        @SerializedName("high")
        public ThumbnailDetails high;
        @SerializedName("medium")
        public ThumbnailDetails medium;
        @SerializedName("default")
        public ThumbnailDetails defaultThumb;
    }

    public static class ThumbnailDetails {
        @SerializedName("url")
        public String url;
    }

    public static class Status {
        @SerializedName("privacyStatus")
        public String privacyStatus;
        @SerializedName("embeddable")
        public boolean embeddable;
        @SerializedName("uploadStatus")
        public String uploadStatus;
    }
}
