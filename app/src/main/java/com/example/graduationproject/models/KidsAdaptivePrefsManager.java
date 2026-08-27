package com.example.graduationproject.models;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;

/**
 * Persists KidsAdaptiveOnboardingData + the current screen index to SharedPreferences, exactly the
 * role `savedRef` plays in the React root component when simulating "reopen the app".
 */
public class KidsAdaptivePrefsManager {

    private static final String PREFS_NAME = "salam_kids_prefs";
    private static final String KEY_DATA = "onboarding_data_json";
    private static final String KEY_INDEX = "onboarding_index";
    private static final String KEY_HAS_SAVED = "has_saved_state";
    private static final String KEY_PHASE = "app_phase"; // "onboarding" | "home"

    private final SharedPreferences prefs;

    public KidsAdaptivePrefsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void save(KidsAdaptiveOnboardingData data, int index, String phase) {
        try {
            JSONObject o = new JSONObject();
            o.put("nickname", data.nickname);
            o.put("nicknameProvided", data.nicknameProvided);
            o.put("ageRangeIndex", data.ageRangeIndex == null ? JSONObject.NULL : data.ageRangeIndex);
            o.put("gender", data.gender == null ? JSONObject.NULL : data.gender);
            o.put("overallMood", data.overallMood == null ? JSONObject.NULL : data.overallMood);
            o.put("frequentEmotions", toArray(data.frequentEmotions));
            o.put("safetyFeeling", data.safetyFeeling == null ? JSONObject.NULL : data.safetyFeeling);
            o.put("intenseFearExperience", data.intenseFearExperience == null ? JSONObject.NULL : data.intenseFearExperience);
            o.put("difficultTimes", toArray(data.difficultTimes));
            o.put("morningFeelings", toArray(data.morningFeelings));
            o.put("dayFeelings", toArray(data.dayFeelings));
            o.put("eveningFeelings", toArray(data.eveningFeelings));
            o.put("nightFeelings", toArray(data.nightFeelings));
            o.put("helpfulActivities", toArray(data.helpfulActivities));
            o.put("goals", toArray(data.goals));
            o.put("demoMoodSelected", data.demoMoodSelected == null ? JSONObject.NULL : data.demoMoodSelected);
            o.put("moodCheckinEnabled", data.moodCheckinEnabled);
            o.put("onboardingCompleted", data.onboardingCompleted);

            prefs.edit()
                    .putString(KEY_DATA, o.toString())
                    .putInt(KEY_INDEX, index)
                    .putString(KEY_PHASE, phase)
                    .putBoolean(KEY_HAS_SAVED, true)
                    .apply();
        } catch (JSONException e) {
            // Non-fatal: worst case the resume prompt simply won't have fresh data.
        }
    }

    public boolean hasSavedState() {
        return prefs.getBoolean(KEY_HAS_SAVED, false);
    }

    public String getSavedPhase() {
        return prefs.getString(KEY_PHASE, "onboarding");
    }

    public int getSavedIndex() {
        return prefs.getInt(KEY_INDEX, 0);
    }

    public KidsAdaptiveOnboardingData load() {
        KidsAdaptiveOnboardingData data = new KidsAdaptiveOnboardingData();
        String json = prefs.getString(KEY_DATA, null);
        if (json == null) return data;
        try {
            JSONObject o = new JSONObject(json);
            data.nickname = o.optString("nickname", "");
            data.nicknameProvided = o.optBoolean("nicknameProvided", false);
            data.ageRangeIndex = o.isNull("ageRangeIndex") ? null : o.optInt("ageRangeIndex");
            data.gender = o.isNull("gender") ? null : o.optString("gender");
            data.overallMood = o.isNull("overallMood") ? null : o.optString("overallMood");
            data.frequentEmotions = fromArray(o.optJSONArray("frequentEmotions"));
            data.safetyFeeling = o.isNull("safetyFeeling") ? null : o.optString("safetyFeeling");
            data.intenseFearExperience = o.isNull("intenseFearExperience") ? null : o.optString("intenseFearExperience");
            data.difficultTimes = fromArray(o.optJSONArray("difficultTimes"));
            data.morningFeelings = fromArray(o.optJSONArray("morningFeelings"));
            data.dayFeelings = fromArray(o.optJSONArray("dayFeelings"));
            data.eveningFeelings = fromArray(o.optJSONArray("eveningFeelings"));
            data.nightFeelings = fromArray(o.optJSONArray("nightFeelings"));
            data.helpfulActivities = fromArray(o.optJSONArray("helpfulActivities"));
            data.goals = fromArray(o.optJSONArray("goals"));
            data.demoMoodSelected = o.isNull("demoMoodSelected") ? null : o.optString("demoMoodSelected");
            data.moodCheckinEnabled = o.optBoolean("moodCheckinEnabled", true);
            data.onboardingCompleted = o.optBoolean("onboardingCompleted", false);
        } catch (JSONException e) {
            // return default
        }
        return data;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    private static JSONArray toArray(LinkedHashSet<String> set) {
        JSONArray arr = new JSONArray();
        for (String s : set) arr.put(s);
        return arr;
    }

    private static LinkedHashSet<String> fromArray(JSONArray arr) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (arr == null) return set;
        for (int i = 0; i < arr.length(); i++) {
            set.add(arr.optString(i));
        }
        return set;
    }
}
