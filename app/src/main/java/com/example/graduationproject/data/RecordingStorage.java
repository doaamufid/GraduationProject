package com.example.graduationproject.data;

import android.content.Context;
import android.util.Log;

import com.example.graduationproject.models.Recording;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * تخزين بسيط لقائمة التسجيلات المحفوظة (صندوق "كلماتي الحلوة").
 * ما بنستخدم قاعدة بيانات، بس ملف JSON صغير جوا مساحة التطبيق الداخلية.
 *
 * كل التسجيلات (لكل الأطفال) محفوظة بنفس الملف، بس كل تسجيل معه childId
 * يميّزه. getAllRecordings() بترجع بس تسجيلات الطفل النشط حالياً
 * (حسب ActiveChildManager)، مش كل التسجيلات.
 */
public class RecordingStorage {

    private static final String TAG = "RecordingStorage";
    private static final String FILE_NAME = "recordings.json";

    private final Context context;

    public RecordingStorage(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * يضيف تسجيل جديد لأول القائمة ويحفظها.
     * نحدد الـ childId هون تلقائياً من الطفل النشط حالياً، حتى ما تعتمد
     * كل شاشة على تمريره يدوياً وتخطئ.
     */
    public void saveRecording(Recording recording) {
        long activeChildId = ActiveChildManager.getActiveChildId(context);
        recording.setChildId(activeChildId);

        List<Recording> all = readAllFromDisk();
        all.add(0, recording); // الأحدث فوق
        writeAll(all);
    }

    /** يرجع تسجيلات الطفل النشط حالياً بس، الأحدث أولاً */
    public List<Recording> getAllRecordings() {
        long activeChildId = ActiveChildManager.getActiveChildId(context);
        return getRecordingsForChild(activeChildId);
    }

    /** يرجع تسجيلات طفل معيّن بالتحديد، الأحدث أولاً */
    public List<Recording> getRecordingsForChild(long childId) {
        List<Recording> filtered = new ArrayList<>();
        for (Recording r : readAllFromDisk()) {
            if (r.getChildId() == childId) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    /** يقرأ كل التسجيلات المخزنة من الملف بدون أي فلترة (لكل الأطفال مع بعض) */
    private List<Recording> readAllFromDisk() {
        List<Recording> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            // Add dummy data for demonstration if no file exists
            list.add(new Recording("أنا بطل وشجاع! 💪", "", System.currentTimeMillis() - 86400000, 1L));
            list.add(new Recording("يومي كان حلو كتير اليوم ☀️", "", System.currentTimeMillis() - 172800000, 1L));
            list.add(new Recording("أنا بحب حالي وبحب أهلي ❤️", "", System.currentTimeMillis() - 259200000, 1L));
            return list;
        }
        try (InputStream is = context.openFileInput(FILE_NAME)) {
            byte[] buffer = new byte[(int) file.length()];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(jsonStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Recording r = new Recording(
                        obj.optString("phrase"),
                        obj.optString("filePath"),
                        obj.optLong("savedAtMillis"),
                        obj.optLong("childId", -1L)
                );
                list.add(r);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "تعذر قراءة التسجيلات المحفوظة", e);
        }
        return list;
    }

    private void writeAll(List<Recording> recordings) {
        JSONArray array = new JSONArray();
        try {
            for (Recording r : recordings) {
                JSONObject obj = new JSONObject();
                obj.put("phrase", r.getPhrase());
                obj.put("filePath", r.getFilePath());
                obj.put("savedAtMillis", r.getSavedAtMillis());
                obj.put("childId", r.getChildId());
                array.put(obj);
            }
            try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
                fos.write(array.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (JSONException | IOException e) {
            Log.e(TAG, "تعذر حفظ التسجيل", e);
        }
    }
}