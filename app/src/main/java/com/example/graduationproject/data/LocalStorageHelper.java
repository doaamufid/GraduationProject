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
 */
public class LocalStorageHelper {

    private static final String TAG = "LocalStorageHelper";
    private static final String PREFS_NAME = "noor_results_prefs";
    private static final String KEY_RESULTS_JSON = "results_list";

    private static final String JSON_IMAGE_PATH = "imagePath";
    private static final String JSON_FEEDBACK = "feedbackText";
    private static final String JSON_TIMESTAMP = "timestamp";

    /**
     * يحفظ صورة جديدة (نسخة محلية) + نص التحليل كسجل جديد بالقائمة.
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

        List<DrawingResult> results = getAllResults(context);
        results.add(0, new DrawingResult(savedImagePath, feedbackText, timestamp));

        saveResultsList(context, results);
    }

    /** يرجع كل السجلات المحفوظة، الأحدث أولاً */
    public static List<DrawingResult> getAllResults(Context context) {
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
                        obj.getLong(JSON_TIMESTAMP)
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "خطأ بقراءة السجلات المحفوظة: " + e.getMessage(), e);
        }

        Collections.sort(results, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
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