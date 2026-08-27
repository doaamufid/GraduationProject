package com.example.graduationproject.Kids;

import android.graphics.Bitmap;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiService {
    private static final String TAG = "GeminiService";
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

    /**
     * إرسال طلب نصي عام لـ Gemini واستقبال الرد عبر Callback
     */
    private void executeGeminiRequest(Content content, GeminiCallback callback) {
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
                    callback.onError("لم أستطع فهم ذلك يا صديقي!");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "خطأ من Gemini: " + t.getMessage(), t);
                callback.onError("تأكد من الاتصال بالإنترنت يا بطل!");
            }
        }, executor);
    }

    private void executeGeminiRequest(String promptText, GeminiCallback callback) {
        Content content = new Content.Builder().addText(promptText).build();
        executeGeminiRequest(content, callback);
    }

    /**
     * إرسال المزاج المختار لـ Gemini
     */
    public void generateMoodMessage(String mood, GeminiCallback callback) {
        executeGeminiRequest(buildMoodPrompt(mood), callback);
    }

    /**
     * تحليل رسمة الطفل (Bitmap)
     */
    public void analyzeDrawing(Bitmap drawingBitmap, GeminiCallback callback) {
        Content content = new Content.Builder()
                .addImage(drawingBitmap)
                .addText(buildDrawingPrompt())
                .build();
        executeGeminiRequest(content, callback);
    }

    /**
     * تحليل تسجيل صوتي للطفل
     */
    public void analyzeRecording(File audioFile, String phrase, GeminiCallback callback) {
        executor.execute(() -> {
            try {
                byte[] audioBytes = readFileBytes(audioFile);
                Content content = new Content.Builder()
                        .addInlineData(audioBytes, "audio/mp4")
                        .addText(buildRecordingPrompt(phrase))
                        .build();
                executeGeminiRequest(content, callback);
            } catch (IOException e) {
                Log.e(TAG, "خطأ بقراءة ملف الصوت: " + e.getMessage(), e);
                callback.onError("تعذّر قراءة ملف التسجيل");
            }
        });
    }

    /**
     * توليد كلمة/جملة محفزة جديدة
     */
    public void generatePhrase(GeminiCallback callback) {
        String prompt = "أنت \"نور\"، صديقة داعمة نفسياً لطفل صغير. "
                + "اقترحي كلمة أو جملة تحفيزية واحدة فقط، قصيرة جداً (كلمة إلى ثلاث كلمات كحد أقصى)، "
                + "باللغة العربية الفصحى المبسطة المناسبة للأطفال، إيجابية ومحفزة على الثقة بالنفس "
                + "(مثال: أنا قوي، أنا شجاع — لا تكرري نفس الأمثلة). "
                + "أعيدي فقط الكلمة/الجملة نفسها بدون علامات تنصيص وبدون أي شرح أو مقدمة.";
        executeGeminiRequest(prompt, new GeminiCallback() {
            @Override
            public void onSuccess(String message) {
                callback.onSuccess(message.replace("\"", ""));
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * يولّد قصة قصيرة مناسبة للتصنيف المحدد (لعبة / صداقة / نوم / مشاعر...)
     * القصة قصيرة ومناسبة للأطفال، وبتنقرأ لاحقاً بواسطة TextToSpeech.
     */
    public void generateStoryForCategory(String category, GeminiCallback callback) {
        String prompt = buildStoryPrompt(category);
        executeGeminiRequest(prompt, callback);
    }

    /**
     * إرسال رسالة مخصصة منفردة من الطفل
     */
    public void sendCustomPrompt(String userMessage, GeminiCallback callback) {
        String prompt = "أنت صديق لطيف ومرح للأطفال اسمه \"دبدوب نور\". "
                + "رسالة الطفل هي: \"" + userMessage + "\". "
                + "رد عليه برفق وبجملة أو جملتين فقط، بلغة عربية بسيطة ومحبة، وبدون مقدمات إضافية.";
        executeGeminiRequest(prompt, callback);
    }

    /**
     * إرسال سجل المحادثة الكاملة مع الطفل
     */
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

    // --- Helper Functions & Prompts ---

    private String buildStoryPrompt(String category) {
        return "أنت \"نور\"، راوية قصص لطيفة للأطفال الصغار. "
                + "اكتبي قصة قصيرة جداً (٤ إلى ٦ جمل فقط) مناسبة لتصنيف \"" + category + "\"، "
                + "باللغة العربية الفصحى المبسطة المناسبة للأطفال، "
                + "بأسلوب دافئ، مشوّق، وإيجابي، بحيث تُقرأ بصوت عالٍ للطفل. "
                + "لا تكرري نفس القصة، اخترعي قصة جديدة كل مرة، وابتعدي عن أي محتوى مخيف أو حزين. "
                + "أعيدي فقط نص القصة نفسه بدون عنوان وبدون أي مقدمات أو شروحات.";
    }

    private String buildMoodPrompt(String mood) {
        return "أنت دبدوب لطيف اسمه \"دبدوب نور\"، ترافق طفلاً صغيراً وتدعمه نفسياً. "
                + "الطفل الآن يشعر بأنه \"" + mood + "\". "
                + "اكتب رسالة تشجيعية واحدة قصيرة جداً (جملة أو جملتين بحد أقصى) "
                + "باللغة العربية الفصحى المبسطة المناسبة للأطفال، "
                + "دافئة، محبة، وتشعره بالأمان، بدون أي مقدمات أو شرح، فقط الرسالة نفسها.";
    }

    private String buildDrawingPrompt() {
        return "أنت \"نور\"، صديقة لطيفة وداعمة نفسياً لطفل صغير. "
                + "الطفل رسم هذه الرسمة وأرسلها لك. "
                + "انظري إلى الرسمة وتفاعلي معها بشكل شخصي ودافئ: "
                + "اذكري بلطف شيئاً واحداً لاحظتيه فيها (الألوان، الأشكال، أو أي عنصر واضح)، "
                + "وامدحي مجهود الطفل ومشاعره وأنت تشجعينه. "
                + "اكتبي فقرة قصيرة جداً (جملتين إلى ثلاث جمل كحد أقصى) "
                + "باللغة العربية الفصحى المبسطة المناسبة للأطفال، "
                + "بأسلوب دافئ ومحب ومطمئن، بدون أي مقدمات أو شرح تقني، فقط الرسالة نفسها موجهة للطفل مباشرة.";
    }

    private String buildRecordingPrompt(String phrase) {
        return "أنت \"نور\"، صديقة لطيفة وداعمة نفسياً لطفل صغير. "
                + "استمعي لهذا التسجيل الصوتي، حيث يحاول الطفل أن يقرأ بصوته العبارة التالية: \"" + phrase + "\". "
                + "قيّمي المحاولة بلطف: إذا كان النطق واضحاً امدحيه بحرارة، وإذا كان فيه تردد أو صعوبة "
                + "شجّعيه بدون أي انتقاد أو إشارة سلبية مباشرة. "
                + "اكتبي رسالة قصيرة جداً (جملة أو جملتين كحد أقصى) باللغة العربية الفصحى المبسطة المناسبة للأطفال، "
                + "دافئة وموجهة للطفل مباشرة، بدون أي مقدمات أو شرح تقني، فقط الرسالة نفسها.";
    }

    private byte[] readFileBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = fis.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
}