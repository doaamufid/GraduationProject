package com.example.graduationproject.data;

import android.util.Log;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerationConfig;
import com.google.firebase.ai.type.Schema;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.graduationproject.data.ChatMessageEntity;

import java.util.Map;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.graduationproject.models.CandidateItem;
import java.util.Arrays;
import java.util.ArrayList;

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

    public void getSuggestedContent(List<CandidateItem> shortlist, String moodId, GeminiCallback callback) {
        Schema itemSchema = Schema.obj(Map.of(
                "type", Schema.enumeration(Arrays.asList("article", "video")),
                "id", Schema.str(),
                "reason", Schema.str()
        ));
        Schema responseSchema = Schema.array(itemSchema);

        GenerationConfig config = new GenerationConfig.Builder()
                .setResponseMimeType("application/json")
                .setResponseSchema(responseSchema)
                .build();

        GenerativeModel gm = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel(MODEL_NAME, config);
        GenerativeModelFutures suggestionsModel = GenerativeModelFutures.from(gm);

        StringBuilder candidatesText = new StringBuilder();
        for (CandidateItem item : shortlist) {
            candidatesText.append(item.type).append(" id=").append(item.id)
                    .append(" category=").append(item.category)
                    .append(" title=").append(item.title).append("\n");
        }

        String prompt = "بناءً على مزاج المستخدم (" + moodId + ")، اختاري ٢ مقالة و٢ فيديو بالضبط "
                + "من القائمة التالية فقط (ممنوع ذكر أي id مو موجود بالقائمة):\n"
                + candidatesText
                + "\nلكل عنصر مختار، اكتبي سبب قصير بالعربي ليش يناسب حالته الآن.";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = suggestionsModel.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (resultText != null && !resultText.trim().isEmpty()) {
                    callback.onSuccess(resultText.trim());
                } else {
                    callback.onError("Empty response");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error generating suggestions: " + t.getMessage(), t);
                callback.onError(t.getMessage());
            }
        }, executor);
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

    public void generateStructuredQuote(String prompt, GeminiCallback callback) {
        Schema quoteSchema = Schema.obj(Map.of(
                "ar", Schema.str(),
                "en", Schema.str()
        ));

        GenerationConfig config = new GenerationConfig.Builder()
                .setResponseMimeType("application/json")
                .setResponseSchema(quoteSchema)
                .build();

        GenerativeModel gm = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel(MODEL_NAME, config);
        GenerativeModelFutures quoteModel = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = quoteModel.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                if (resultText != null && !resultText.trim().isEmpty()) {
                    callback.onSuccess(resultText.trim());
                } else {
                    callback.onError("Empty response");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error generating quote: " + t.getMessage(), t);
                callback.onError(t.getMessage());
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
}