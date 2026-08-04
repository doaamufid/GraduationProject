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
 * لو حابب مستقبلاً تربطها بـ Room أو Firebase، بس بدّل هالكلاس وخلي الواجهة
 * (الدوال) زي ما هي عشان باقي الشاشات ما تتغير.
 */
public class RecordingStorage {

    private static final String TAG = "RecordingStorage";
    private static final String FILE_NAME = "recordings.json";

    private final Context context;

    public RecordingStorage(Context context) {
        this.context = context.getApplicationContext();
    }

    /** يضيف تسجيل جديد لأول القائمة ويحفظها */
    public void saveRecording(Recording recording) {
        List<Recording> current = getAllRecordings();
        current.add(0, recording); // الأحدث فوق
        writeAll(current);
    }

    /** يرجع كل التسجيلات المحفوظة، الأحدث أولاً */
    public List<Recording> getAllRecordings() {
        List<Recording> list = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
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
                        obj.optLong("savedAtMillis")
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
