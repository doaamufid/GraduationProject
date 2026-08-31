package com.example.graduationproject.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeUtils {

    /**
     * Extracts the video ID from various YouTube URL formats.
     * @param url The YouTube URL or video ID.
     * @return The extracted video ID or the original string if it looks like an ID.
     */
    public static String extractVideoId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }

        url = url.trim();

        // If it's already a clean ID (11 chars, alpha-numeric + some symbols)
        if (url.length() == 11 && url.matches("[a-zA-Z0-9_-]{11}")) {
            return url;
        }

        // Improved regex to handle shorts and more formats
        Pattern pattern = Pattern.compile(
                "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?|shorts)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return url;
    }

    /**
     * Generates a high-quality thumbnail URL for a video ID.
     * @param videoId The clean YouTube video ID.
     * @return The thumbnail URL.
     */
    public static String getThumbnailUrl(String videoId) {
        if (videoId == null || videoId.trim().isEmpty()) {
            return null;
        }
        // i.ytimg.com is the canonical host for YouTube thumbnails.
        // hqdefault is 480x360 and usually available for all videos.
        return "https://i.ytimg.com/vi/" + videoId.trim() + "/hqdefault.jpg";
    }

    /**
     * Generates a fallback thumbnail URL.
     * Uses mqdefault (320x180) which is almost always available even if HQ is not.
     */
    public static String getFallbackThumbnailUrl(String videoId) {
        if (videoId == null || videoId.trim().isEmpty()) {
            return null;
        }
        // mqdefault is a much safer fallback than sddefault, as sddefault 404s for non-HD videos.
        return "https://i.ytimg.com/vi/" + videoId.trim() + "/mqdefault.jpg";
    }
}
