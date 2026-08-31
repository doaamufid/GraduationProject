package com.example.graduationproject.data;

import android.content.Context;
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
import com.example.graduationproject.R;

import org.json.JSONObject;
import java.util.Map;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.graduationproject.models.CandidateItem;
import java.util.Arrays;
import java.util.ArrayList;

public class SalamGeminiService {
    private static final String TAG = "SalamGeminiService";
    private static final String MODEL_NAME = "gemini-3.5-flash-lite"; 
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final ChatSafetyRuleScreener safetyScreener;
    private final Context context;

    public interface GeminiCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public SalamGeminiService(Context context) {
        this.context = context.getApplicationContext();
        this.safetyScreener = new ChatSafetyRuleScreener(this.context);
        
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
        // Step 1: Local Safety Screening
        ChatSafetyRuleScreener.ScreenResult result = safetyScreener.screen(userMessage);
        if (result != ChatSafetyRuleScreener.ScreenResult.NORMAL) {
            callback.onSuccess(getStaticSafetyResponse(result));
            return;
        }

        // Step 3 & 5: Generation Config with Schema
        Schema responseSchema = Schema.obj(Map.of(
                "replyText", Schema.str(),
                "suggestedExerciseType", Schema.enumeration(Arrays.asList(
                        "NONE", "BREATHING", "GROUNDING", "CBT_REFRAME", "BODY_MAP", "FUTURE_LETTER"))
        ));

        GenerationConfig config = new GenerationConfig.Builder()
                .setResponseMimeType("application/json")
                .setResponseSchema(responseSchema)
                .build();

        GenerativeModel gm = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel(MODEL_NAME, config);
        GenerativeModelFutures chatModel = GenerativeModelFutures.from(gm);

        String prompt = buildPrompt(history, userMessage);
        Content content = new Content.Builder().addText(prompt).build();

        ListenableFuture<GenerateContentResponse> response = chatModel.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
        try {
            if (resultText.startsWith("{")) {
                JSONObject json = new JSONObject(resultText);
                String reply = json.optString("replyText", "");

                // Step 5: Post-check AI reply for safety patterns
                ChatSafetyRuleScreener.ScreenResult replySafety = safetyScreener.screen(reply);
                if (replySafety != ChatSafetyRuleScreener.ScreenResult.NORMAL) {
                    callback.onSuccess(getStaticSafetyResponse(replySafety));
                    return;
                }
                callback.onSuccess(resultText.trim());
            } else {
                // If AI returns raw text despite schema, wrap it in JSON format expected by UI
                JSONObject fallbackJson = new JSONObject();
                fallbackJson.put("replyText", resultText.trim());
                fallbackJson.put("suggestedExerciseType", "NONE");
                callback.onSuccess(fallbackJson.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "JSON parse error: " + e.getMessage());
            callback.onError("حدث خطأ في معالجة الرد.");
        }
    }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "خطأ من Gemini: " + t.getMessage(), t);
                callback.onError("في مشكلة بالاتصال، حاول مرة تانية.");
            }
        }, executor);
    }

    private String getStaticSafetyResponse(ChatSafetyRuleScreener.ScreenResult type) {
        JSONObject response = new JSONObject();
        String text = "";
        switch (type) {
            case DIAGNOSIS_REQUEST:
                text = "سؤال مفهوم إنك تسأليه. أنا مو قادرة أأكد أو أنفي أي تشخيص — هاد شي بس مختص مؤهل يقدر يحدده بدقة. اللي أقدر أساعدك فيه إني أضل أسمعك. لو تقدري توصلي لدكتور أو مختص نفسي، هاي أصح خطوة جاية.";
                break;
            case MEDICATION_REQUEST:
                text = "سؤال منطقي، بس مو قادرة أساعدك فيه — أي قرار يخص بدء أو وقف أو تغيير دواء لازم يمر عبر دكتور أو صيدلاني يعرف حالتك كاملة. أنا هون لو حابة تحكي عن حسّك تجاه الموضوع بالعموم.";
                break;
            case CRISIS_SIGNAL:
                text = "يبدو إنك تمرين بلحظة صعبة وخطيرة، وحابين نتأكد إنك بأمان. لو تقدر توصل لحد قريب منك جسدياً هلق، كلمه. لو حاب تتواصل مع جهة طوارئ أو خط مساعدة بمنطقتك، هذا أفضل خطوة حالياً. أنا هون لأسمعك، بس مو بديل عن مساعدة حقيقية بهاللحظة.";
                break;
        }
        try {
            response.put("replyText", text);
            response.put("suggestedExerciseType", "NONE");
            response.put("isSafetyFallback", true);
        } catch (Exception ignored) {}
        return response.toString();
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
        
        // Step 2: System Instruction
        sb.append("إنتِ \"سلام\" — رفيق داعم نفسياً بالمحادثة، مو معالج نفسي ومو بديل عن مختص.\n")
                .append("أسلوبك دافئ، بسيط، بعيد عن اللغة الطبية أو الرسمية.\n\n")
                .append("قواعد صارمة، بدون أي استثناء مهما كان سياق المحادثة:\n")
                .append("- ما تشخّصي أي حالة نفسية، وما تأكدي ولا تنفي تشخيص المستخدم لنفسه.\n")
                .append("- ما تنصحي بدواء، ولا تقترحي تعديل أو وقف أو زيادة أي دواء.\n")
                .append("- ما تقدّمي حالك كبديل عن مختص أو دعم بشري حقيقي — شجّعي عليه بلطف لما يكون مناسب.\n")
                .append("- لو المستخدم عبّر عن فكرة غير واقعية أو مقلقة، ما تأكديها ولا تجادلي فيها كحقيقة — طمّني بلطف بدون ما تناقشي محتوى الفكرة نفسها.\n")
                .append("- ما تعطي أي تفاصيل ممكن تساعد على إيذاء النفس أو الغير.\n\n")
                .append("استخدام السياق: هوصلك معلومات مختصرة عن حالة المستخدم (مزاج، تفضيلات لو موجودة) — استخدميها بطبيعية جوا كلامك، مو كتعداد أو تكرار آلي.\n\n")
                .append("اقتراح تمرين: لو حسيتي إنه تمرين معين (تنفس، تأريض، إعادة صياغة فكرة، خريطة الجسد، رسالة للمستقبل) يناسب اللحظة تحديداً، حددي نوعه بحقل suggestedExerciseType.\n")
                .append("الأنواع المتاحة: BREATHING, GROUNDING, CBT_REFRAME, BODY_MAP, FUTURE_LETTER.\n")
                .append("لو ما في تمرين واضح يناسب، اتركيه NONE — ما تفرضي تمرين كل رسالة.\n\n");

        // Step 4: Personalization Context
        android.content.SharedPreferences appPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        android.content.SharedPreferences userPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        
        String todayMoodId = appPrefs.getString("today_mood_id", "");
        String userName = userPrefs.getString("user_name", "");
        
        if (!userName.isEmpty()) {
            sb.append("اسم المستخدم: ").append(userName).append(". ");
        }

        String moodLabel = mapMoodIdToArabicLabel(todayMoodId);
        if (moodLabel != null) {
            sb.append("سياق المستخدم الحالي: يشعر بـ ").append(moodLabel).append(" اليوم.\n\n");
        }

        sb.append("سجل المحادثة:\n");

        // نأخذ فقط آخر 10 رسائل نصية كسياق حتى ما يطول البرومبت
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            ChatMessageEntity m = history.get(i);
            if (m.text == null) continue; // تجاهل الرسائل الصوتية بالسياق
            
            // If the message was structured JSON, try to extract replyText
            String text = m.text;
            if (text.startsWith("{")) {
                try {
                    text = new JSONObject(text).optString("replyText", text);
                } catch (Exception ignored) {}
            }
            
            sb.append(m.fromUser ? "المستخدم: " : "سلام: ").append(text).append("\n");
        }
        sb.append("المستخدم: ").append(userMessage).append("\nسلام:");
        return sb.toString();
    }

    private String mapMoodIdToArabicLabel(String moodId) {
        if (moodId == null) return null;
        switch (moodId) {
            case "awful": return context.getString(R.string.mood_awful);
            case "sad": return context.getString(R.string.mood_sad);
            case "low": return context.getString(R.string.mood_low);
            case "neutral": return context.getString(R.string.mood_neutral);
            case "calm": return context.getString(R.string.mood_calm);
            case "happy": return context.getString(R.string.mood_happy);
            case "overjoyed": return context.getString(R.string.mood_overjoyed);
            default: return null;
        }
    }
}