package com.example.graduationproject.util;

import com.example.graduationproject.models.ContentItem;
import android.graphics.Color;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class YouTubeParser {

    private static final String TAG = "YouTubeDebug";

    /**
     * Parses a search.list response from YouTube Data API v3.
     * @param jsonResponse The raw JSON response string.
     * @return A list of ContentItem objects.
     */
    public static List<ContentItem> parseSearchList(String jsonResponse) {
        List<ContentItem> results = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray items = root.optJSONArray("items");
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    
                    // Correctly parse id.videoId
                    JSONObject idObj = item.optJSONObject("id");
                    if (idObj == null) continue;
                    
                    String videoId = idObj.optString("videoId", "");
                    if (videoId.isEmpty()) continue;

                    JSONObject snippet = item.optJSONObject("snippet");
                    if (snippet == null) continue;

                    String title = snippet.optString("title", "Untitled");
                    String description = snippet.optString("description", "");
                    String channelTitle = snippet.optString("channelTitle", "YouTube");

                    // Thumbnails
                    JSONObject thumbnails = snippet.optJSONObject("thumbnails");
                    String highUrl = null;
                    String mediumUrl = null;
                    String defaultUrl = null;

                    if (thumbnails != null) {
                        JSONObject high = thumbnails.optJSONObject("high");
                        if (high != null) highUrl = high.optString("url", null);

                        JSONObject medium = thumbnails.optJSONObject("medium");
                        if (medium != null) mediumUrl = medium.optString("url", null);

                        JSONObject def = thumbnails.optJSONObject("default");
                        if (def != null) defaultUrl = def.optString("url", null);
                    }

                    Log.d(TAG, "Parsed Search Result - videoId: " + videoId + ", highUrl: " + highUrl);

                    // Map to ContentItem model
                    results.add(new ContentItem(
                            1000 + i, 
                            title, 
                            channelTitle, 
                            "فيديو", 
                            true, 
                            "YouTube", 
                            "عام", 
                            videoId, 
                            highUrl,
                            mediumUrl,
                            defaultUrl,
                            Color.parseColor("#2E5C86"), 
                            Color.parseColor("#1F3A60"), 
                            description
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
