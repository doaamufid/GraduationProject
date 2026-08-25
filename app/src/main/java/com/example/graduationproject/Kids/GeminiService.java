package com.example.graduationproject.Kids;

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

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiService {

    private static final String TAG = "GeminiService";

    // استخدام اسم موديل رسمياً مدعوم في Firebase Vertex AI
    private static final String MODEL_NAME = "gemini-3.5-flash-lite";

    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface GeminiCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public GeminiService() {
        GenerativeModel gm = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel(MODEL_NAME);
        this.model = GenerativeModelFutures.from(gm);
    }

    public void generateMoodMessage(String mood, GeminiCallback callback) {
        String prompt = buildPrompt(mood);

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();

                if (resultText != null && !resultText.trim().isEmpty()) {
                    Log.d(TAG, "رد ناجح من Gemini: " + resultText);
                    callback.onSuccess(resultText.trim());
                } else {
                    Log.e(TAG, "رد فاضي من Gemini");
                    callback.onError("تعذّر قراءة رد Gemini");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "خطأ من Gemini: " + t.getMessage(), t);
                callback.onError("خطأ: " + t.getMessage());
            }
        }, executor);
    }

    private String buildPrompt(String mood) {
        return "أنت دبدوب لطيف اسمه \"دبدوب نور\"، ترافق طفلاً صغيراً وتدعمه نفسياً. "
                + "الطفل الآن يشعر بأنه \"" + mood + "\". "
                + "اكتب رسالة تشجيعية واحدة قصيرة جداً (جملة أو جملتين بحد أقصى) "
                + "باللغة العربية الفصحى المبسطة المناسبة للأطفال، "
                + "دافئة، محبة، وتشعره بالأمان، بدون أي مقدمات أو شرح، فقط الرسالة نفسها.";
    }
    public void sendCustomPrompt(String userMessage, GeminiCallback callback) {
        String prompt = "أنت صديق لطيف ومرح للأطفال اسمه \"دبدوب نور\". "
                + "رسالة الطفل هي: \"" + userMessage + "\". "
                + "رد عليه برفق وبجملة أو جملتين فقط، بلغة عربية بسيطة ومحبة، وبدون مقدمات إضافية.";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (result.getText() != null && !result.getText().trim().isEmpty()) {
                    callback.onSuccess(result.getText().trim());
                } else {
                    callback.onError("لم أستطع فهم ذلك يا صديقي!");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError("تأكد من الاتصال بالإنترنت يا بطل!");
            }
        }, executor);
    }
    public void sendChatHistory(List<ChatMessage> chatMessages, GeminiCallback callback) {
        StringBuilder fullPrompt = new StringBuilder();
        fullPrompt.append("أنت صديق لطيف للأطفال اسمك 'دبدوب نور'. تذكر ما قيل في المحادثة ورد بأسلوب محب وقصير (جملة أو جملتين) باللغة العربية المبسطة.\n\n");
        fullPrompt.append("سجل المحادثة:\n");

        for (ChatMessage msg : chatMessages) {
            if (msg.isUser()) {
                fullPrompt.append("الطفل: ").append(msg.getMessage()).append("\n");
            } else {
                fullPrompt.append("دبدوب نور: ").append(msg.getMessage()).append("\n");
            }
        }
        fullPrompt.append("دبدوب نور:");

        executeGeminiRequest(fullPrompt.toString(), callback);
    }
    private void executeGeminiRequest(String promptText, GeminiCallback callback) {
        Content content = new Content.Builder().addText(promptText).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (result.getText() != null && !result.getText().trim().isEmpty()) {
                    callback.onSuccess(result.getText().trim());
                } else {
                    callback.onError("لم أستطع فهم ذلك يا صديقي!");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError("تأكد من الاتصال بالإنترنت يا بطل!");
            }
        }, executor);
    }
}