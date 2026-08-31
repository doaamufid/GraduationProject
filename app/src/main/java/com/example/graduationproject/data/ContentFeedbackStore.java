package com.example.graduationproject.data;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContentFeedbackStore {

    private static final String PREFS_NAME = "salam_prefs";
    private static final String KEY_FEEDBACK_MAP = "content_feedback_map";

    public enum Reason { TOO_LONG, GOOD_TIMING, ALREADY_KNEW, HARD_LANGUAGE, OTHER }

    private final SharedPreferences prefs;

    public ContentFeedbackStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveFeedback(String contentType, long contentId, boolean liked, String reason) {
        try {
            JSONObject map = new JSONObject(prefs.getString(KEY_FEEDBACK_MAP, "{}"));
            JSONObject entry = new JSONObject();
            entry.put("liked", liked);
            entry.put("reason", reason != null ? reason : JSONObject.NULL);
            entry.put("timestamp", System.currentTimeMillis());
            map.put(contentType + ":" + contentId, entry);
            prefs.edit().putString(KEY_FEEDBACK_MAP, map.toString()).apply();
        } catch (JSONException ignored) { }
    }

    /** Returns keys (type:id) that the user disliked within the last N days. */
    public Set<String> getRecentlyDislikedKeys(int withinDays) {
        Set<String> disliked = new HashSet<>();
        long cutoff = System.currentTimeMillis() - (withinDays * 24L * 60 * 60 * 1000);
        try {
            JSONObject map = new JSONObject(prefs.getString(KEY_FEEDBACK_MAP, "{}"));
            Iterator<String> keys = map.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject entry = map.getJSONObject(key);
                if (!entry.optBoolean("liked", true) && entry.optLong("timestamp", 0) >= cutoff) {
                    disliked.add(key);
                }
            }
        } catch (JSONException ignored) { }
        return disliked;
    }
}
