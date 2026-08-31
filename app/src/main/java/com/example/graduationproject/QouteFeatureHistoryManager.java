package com.example.graduationproject;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.graduationproject.models.QouteFeatureQuoteEntry;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Persists the ids of the last N quotes shown so the same quote does not
 * repeat until the others have been seen. This is the Android equivalent
 * of the window.storage "salam_seen_history" key used in the web version.
 */
public class QouteFeatureHistoryManager {

    private static final String PREFS_NAME = "salam_prefs";
    private static final String HISTORY_KEY = "salam_seen_history";
    private static final int HISTORY_LIMIT = 4;

    private final SharedPreferences prefs;
    private final Random random = new Random();

    public QouteFeatureHistoryManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<String> loadHistory() {
        List<String> result = new ArrayList<>();
        String raw = prefs.getString(HISTORY_KEY, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }
        } catch (JSONException ignored) {
        }
        return result;
    }

    public void saveHistory(List<String> history) {
        JSONArray arr = new JSONArray();
        for (String id : history) arr.put(id);
        prefs.edit().putString(HISTORY_KEY, arr.toString()).apply();
    }

    public static class Pick {
        public final QouteFeatureQuoteEntry entry;
        public final List<String> nextHistory;

        Pick(QouteFeatureQuoteEntry entry, List<String> nextHistory) {
            this.entry = entry;
            this.nextHistory = nextHistory;
        }
    }

    /** Picks the next quote avoiding the recently-seen pool, mirrors pickNextEntry() in JS. */
    public Pick pickNextEntry(List<QouteFeatureQuoteEntry> all, List<String> history) {
        List<QouteFeatureQuoteEntry> candidates = new ArrayList<>();
        for (QouteFeatureQuoteEntry e : all) {
            if (!history.contains(e.id)) candidates.add(e);
        }
        if (candidates.isEmpty()) candidates = all; // seen them all -> reset pool

        QouteFeatureQuoteEntry pick = candidates.get(random.nextInt(candidates.size()));

        List<String> next = new ArrayList<>();
        next.add(pick.id);
        for (String id : history) {
            if (!id.equals(pick.id)) next.add(id);
        }
        while (next.size() > HISTORY_LIMIT) {
            next.remove(next.size() - 1);
        }
        return new Pick(pick, next);
    }
}
