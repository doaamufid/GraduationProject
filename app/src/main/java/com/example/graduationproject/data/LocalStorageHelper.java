package com.example.graduationproject.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.graduationproject.models.DrawingResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * يخزن كل رسمة + تحليلها كسجل منفصل محليًا (صورة كملف + بيانات JSON بالـ SharedPreferences)
 * حتى تنعرض لاحقًا بشاشة المعرض/History.
 *
 * كل السجلات (لكل الأطفال) محفوظة بنفس مفتاح الـ JSON، بس كل سجل معه childId
 * يميّزه. getAllResults() بترجع بس رسومات الطفل النشط حالياً
 * (حسب ActiveChildManager)، مش كل الرسومات.
 */
public class LocalStorageHelper {

    private static final String TAG = "LocalStorageHelper";
    private static final String PREFS_NAME = "noor_results_prefs";
    private static final String KEY_RESULTS_JSON = "results_list";

    private static final String JSON_IMAGE_PATH = "imagePath";
    private static final String JSON_FEEDBACK = "feedbackText";
    private static final String JSON_TIMESTAMP = "timestamp";
    private static final String JSON_CHILD_ID = "childId";

    /**
     * يحفظ صورة جديدة (نسخة محلية) + نص التحليل كسجل جديد بالقائمة.
     * نحدد الـ childId هون تلقائياً من الطفل النشط حالياً.
     */
    public static void saveResult(Context context, Uri sourceUri, String feedbackText) {
        long timestamp = System.currentTimeMillis();
        String savedImagePath = sourceUri != null
                ? saveImageLocally(context, sourceUri, timestamp)
                : null;

        if (savedImagePath == null) {
            Log.e(TAG, "لم يتم حفظ الصورة، سيتم تجاهل هذا السجل");
            return;
        }

        long activeChildId = ActiveChildManager.getActiveChildId(context);

        List<DrawingResult> allResults = readAllFromDisk(context);
        allResults.add(0, new DrawingResult(savedImagePath, feedbackText, timestamp, activeChildId));

        saveResultsList(context, allResults);
    }

    /** يرجع رسومات الطفل النشط حالياً بس، الأحدث أولاً */
    public static List<DrawingResult> getAllResults(Context context) {
        long activeChildId = ActiveChildManager.getActiveChildId(context);
        return getResultsForChild(context, activeChildId);
    }

    /** يرجع رسومات طفل معيّن بالتحديد، الأحدث أولاً */
    public static List<DrawingResult> getResultsForChild(Context context, long childId) {
        List<DrawingResult> filtered = new ArrayList<>();
        for (DrawingResult r : readAllFromDisk(context)) {
            if (r.getChildId() == childId) {
                filtered.add(r);
            }
        }
        Collections.sort(filtered, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return filtered;
    }

    /** يقرأ كل السجلات المخزنة بدون أي فلترة (لكل الأطفال مع بعض) */
    private static List<DrawingResult> readAllFromDisk(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RESULTS_JSON, null);

        List<DrawingResult> results = new ArrayList<>();
        if (json == null) return results;

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                results.add(new DrawingResult(
                        obj.getString(JSON_IMAGE_PATH),
                        obj.getString(JSON_FEEDBACK),
                        obj.getLong(JSON_TIMESTAMP),
                        obj.optLong(JSON_CHILD_ID, -1L)
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "خطأ بقراءة السجلات المحفوظة: " + e.getMessage(), e);
        }

        return results;
    }

    private static void saveResultsList(Context context, List<DrawingResult> results) {
        JSONArray array = new JSONArray();
        try {
            for (DrawingResult r : results) {
                JSONObject obj = new JSONObject();
                obj.put(JSON_IMAGE_PATH, r.getImagePath());
                obj.put(JSON_FEEDBACK, r.getFeedbackText());
                obj.put(JSON_TIMESTAMP, r.getTimestamp());
                obj.put(JSON_CHILD_ID, r.getChildId());
                array.put(obj);
            }
        } catch (JSONException e) {
            Log.e(TAG, "خطأ بحفظ السجلات: " + e.getMessage(), e);
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_RESULTS_JSON, array.toString()).apply();
    }

    private static String saveImageLocally(Context context, Uri sourceUri, long timestamp) {
        try (InputStream in = context.getContentResolver().openInputStream(sourceUri)) {
            if (in == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null) return null;

            File outFile = new File(context.getFilesDir(), "drawing_" + timestamp + ".jpg");
            try (FileOutputStream out = new FileOutputStream(outFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }

            return outFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "تعذّر حفظ الصورة محليًا: " + e.getMessage(), e);
            return null;
        }
    }
}