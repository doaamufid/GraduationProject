package com.example.graduationproject;

import android.util.Log;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.graduationproject.data.ChatMessageEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SalamGeminiService {
    private static final String TAG = "SalamGeminiService";
    private static final String MODEL_NAME = "gemini-3.5-flash-lite"; // نفس الموديل يلي مستخدمه بمشروعك
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface GeminiCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public SalamGeminiService() {
        GenerativeModel gm = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel(MODEL_NAME);
        this.model = GenerativeModelFutures.from(gm);
    }

    /**
     * بيبعت رسالة المستخدم الجديدة مع آخر جزء من سجل المحادثة (للسياق).
     */
    public void sendMessage(List<ChatMessageEntity> history, String userMessage, GeminiCallback callback) {
        String prompt = buildPrompt(history, userMessage);
        Content content = new Content.Builder().addText(prompt).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (resultText != null && !resultText.trim().isEmpty()) {
                    callback.onSuccess(resultText.trim());
                } else {
                    callback.onError("لم أستطع فهم رسالتك، ممكن تعيد صياغتها؟");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "خطأ من Gemini: " + t.getMessage(), t);
                callback.onError("في مشكلة بالاتصال، حاول مرة تانية.");
            }
        }, executor);
    }

    private String buildPrompt(List<ChatMessageEntity> history, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("أنت \"سلام\"، مساعد نفسي داعم ولطيف. ")
                .append("رد بتعاطف وإيجاز (جملتين إلى ثلاث جمل كحد أقصى)، باللغة العربية المبسطة، ")
                .append("وبدون تشخيص طبي أو نصائح دوائية.\n\n")
                .append("سجل المحادثة:\n");

        // نأخذ فقط آخر 10 رسائل نصية كسياق حتى ما يطول البرومبت
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            ChatMessageEntity m = history.get(i);
            if (m.text == null) continue; // تجاهل الرسائل الصوتية بالسياق
            sb.append(m.fromUser ? "المستخدم: " : "سلام: ").append(m.text).append("\n");
        }
        sb.append("المستخدم: ").append(userMessage).append("\nسلام:");
        return sb.toString();
    }
    public interface ChildAnalysisCallback {
        void onSuccess(String recommendationsJson);
        void onError(String errorMessage);
    }

    public void generateChildReport(String childName, int childAge, int completedExercises, ChildAnalysisCallback callback) {
        String prompt = "أنت أخصائي نفسي وتربوي للأطفال. قم بتحليل حالة الطفل التالية وإعطاء توصيات مختصرة:\n" +
                "اسم الطفل: " + childName + "\n" +
                "العمر: " + childAge + " سنوات\n" +
                "عدد التمارين المنجزة مؤخراً: " + completedExercises + "\n\n" +
                "المطلوب: اذكر 2 إلى 3 توصيات تربوية/نفسية قصيرة جداً ومباشرة للأهل للتعامل مع الطفل.\n" +
                "اكتب كل توصية في سطر منفصل ابدأ بكلمة '-' بدون مقدمات أو خاتمة.";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (resultText != null && !resultText.trim().isEmpty()) {
                    callback.onSuccess(resultText.trim());
                } else {
                    callback.onError("تعذر تحليل البيانات حالياً.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "خطأ أثناء تحليل بيانات الطفل: " + t.getMessage(), t);
                callback.onError("فشل الاتصال أثناء جلب التوصيات.");
            }
        }, executor);
    }
    public interface ChildMoodAnalysisCallback {
        void onSuccess(float[] dayScores, float[] weekScores, float[] monthScores);
        void onError(String errorMessage);
    }

    public void generateChildMoodData(String childName, int childAge, ChildMoodAnalysisCallback callback) {
        String prompt = "أنت أخصائي نفسي للأطفال. قم بتوليد تقييم تقريبي لمزاج الطفل " + childName + " (العمر: " + childAge + " سنوات) " +
                "على شكل درجات من 1.0 إلى 5.0 لثلاث فترات زمنية.\n\n" +
                "المطلوب إرجاع النص بصيغة JSON فقط وبدون أي كلام إضافي بالشكل التالي:\n" +
                "{\n" +
                "  \"day\": [3.5, 4.0, 3.2, 4.5, 4.8, 4.0, 4.2, 3.9],\n" +
                "  \"week\": [3.2, 4.5, 2.8, 4.0, 4.6, 3.9, 4.4],\n" +
                "  \"month\": [3.6, 4.1, 3.9, 4.4, 4.0]\n" +
                "}";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                try {
                    String text = result.getText();
                    if (text != null) {
                        // تنظيف النص للحصول على الـ JSON فقط
                        int start = text.indexOf("{");
                        int end = text.lastIndexOf("}") + 1;
                        if (start >= 0 && end > start) {
                            text = text.substring(start, end);
                        }
                        org.json.JSONObject json = new org.json.JSONObject(text);

                        float[] day = parseJsonArray(json.getJSONArray("day"));
                        float[] week = parseJsonArray(json.getJSONArray("week"));
                        float[] month = parseJsonArray(json.getJSONArray("month"));

                        callback.onSuccess(day, week, month);
                    } else {
                        callback.onError("فشل في استخراج بيانات المزاج");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "خطأ في تحليل JSON المزاج: " + e.getMessage(), e);
                    callback.onError("خطأ في معالجة البيانات");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t.getMessage());
            }
        }, executor);
    }

    private float[] parseJsonArray(org.json.JSONArray array) throws org.json.JSONException {
        float[] result = new float[array.length()];
        for (int i = 0; i < array.length(); i++) {
            result[i] = (float) array.getDouble(i);
        }
        return result;
    }
}