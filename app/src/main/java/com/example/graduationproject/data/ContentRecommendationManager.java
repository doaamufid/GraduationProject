package com.example.graduationproject.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.graduationproject.data.ArticleRepository;
import com.example.graduationproject.models.Article;
import com.example.graduationproject.models.ArticleCategory;
import com.example.graduationproject.models.CandidateItem;
import com.example.graduationproject.models.ContentItem;
import com.example.graduationproject.models.ContentRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContentRecommendationManager {

    private static final String PREF_NAME = "ContentRecPrefs";
    private static final String KEY_LAST_RECS = "last_recommendations";
    private static final String KEY_LAST_TIME = "last_rec_time";
    private static final String KEY_LAST_MOOD = "last_rec_mood";

    public static List<CandidateItem> getShortlist(Context context, String moodId) {
        List<CandidateItem> shortlist = new ArrayList<>();
        ContentFeedbackStore feedbackStore = new ContentFeedbackStore(context);
        java.util.Set<String> disliked = feedbackStore.getRecentlyDislikedKeys(30);
        
        // Match articles
        for (Article a : ArticleRepository.getAll()) {
            if (isCategoryMatch(a.category, moodId)) {
                if (!disliked.contains("article:" + a.id)) {
                    shortlist.add(new CandidateItem("article", a.id, a.title, a.category));
                }
            }
        }
        
        // Match videos
        for (ContentItem v : ContentRepository.getAllItems()) {
            if (isCategoryMatch(v.category, moodId)) {
                if (!disliked.contains("video:" + v.id)) {
                    shortlist.add(new CandidateItem("video", v.id, v.title, v.category));
                }
            }
        }
        
        // Limit to 15 items for Gemini context
        if (shortlist.size() > 15) {
            return shortlist.subList(0, 15);
        }
        return shortlist;
    }

    private static boolean isCategoryMatch(String category, String moodId) {
        if (moodId == null) return true;
        
        switch (moodId) {
            case "awful":
            case "sad":
                return category.equals(ArticleCategory.GRATITUDE) || category.equals(ArticleCategory.HOPE) 
                        || category.equals("قلق") || category.equals("صدمة");
            case "low":
                return category.equals(ArticleCategory.PATIENCE) || category.equals("نوم");
            case "neutral":
                return category.equals(ArticleCategory.STRENGTH) || category.equals("علاقات");
            case "calm":
            case "happy":
            case "overjoyed":
                return category.equals(ArticleCategory.STRENGTH) || category.equals(ArticleCategory.GRATITUDE);
            default:
                return true;
        }
    }

    public static boolean shouldRefresh(Context context, String currentMoodId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long lastTime = prefs.getLong(KEY_LAST_TIME, 0);
        String lastMood = prefs.getString(KEY_LAST_MOOD, "");
        
        long now = System.currentTimeMillis();
        // Refresh if > 12 hours OR mood changed
        return (now - lastTime > 12 * 60 * 60 * 1000) || !currentMoodId.equals(lastMood);
    }

    public static void saveRecommendations(Context context, List<RecommendationResponse> recs, String moodId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        try {
            for (RecommendationResponse r : recs) {
                JSONObject obj = new JSONObject();
                obj.put("type", r.type);
                obj.put("id", r.id);
                obj.put("reason", r.reason);
                array.put(obj);
            }
        } catch (Exception ignored) {}
        
        prefs.edit()
                .putString(KEY_LAST_RECS, array.toString())
                .putLong(KEY_LAST_TIME, System.currentTimeMillis())
                .putString(KEY_LAST_MOOD, moodId)
                .apply();
    }

    public static List<RecommendationResponse> getCachedRecommendations(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LAST_RECS, null);
        return getCachedRecommendationsFromText(json);
    }

    public static List<RecommendationResponse> getCachedRecommendationsFromText(String json) {
        if (json == null) return null;
        
        List<RecommendationResponse> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RecommendationResponse r = new RecommendationResponse();
                r.type = obj.getString("type");
                
                // Gemini might return id as string even if we asked for number, 
                // or we used Schema.str()
                String idStr = obj.getString("id");
                r.id = Integer.parseInt(idStr);

                r.reason = obj.getString("reason");
                list.add(r);
            }
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    public static class RecommendationResponse {
        public String type;
        public int id;
        public String reason;
    }
}
