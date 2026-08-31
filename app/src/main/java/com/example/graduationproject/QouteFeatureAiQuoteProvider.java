package com.example.graduationproject;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.graduationproject.models.QouteFeatureQuoteEntry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.json.JSONObject;

public class QouteFeatureAiQuoteProvider {

    private static final String PREFS_NAME = "salam_prefs";
    private static final String KEY_DATE = "salam_ai_quote_date";
    private static final String KEY_AR = "salam_ai_quote_ar";
    private static final String KEY_EN = "salam_ai_quote_en";

    private final SharedPreferences prefs;
    private final com.example.graduationproject.data.SalamGeminiService geminiService;

    public QouteFeatureAiQuoteProvider(Context context, com.example.graduationproject.data.SalamGeminiService geminiService) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.geminiService = geminiService;
    }

    /** فحص فوري، صفر شبكة — يرجّع null لو ما فيه اقتباس صالح لليوم. */
    public QouteFeatureQuoteEntry getTodaysCachedQuote(List<String> existingImageUrls) {
        String todayKey = todayDateString();
        if (!todayKey.equals(prefs.getString(KEY_DATE, null))) return null;

        String ar = prefs.getString(KEY_AR, null);
        String en = prefs.getString(KEY_EN, null);
        if (ar == null || en == null || ar.trim().isEmpty() || en.trim().isEmpty()) return null;

        String img = existingImageUrls.isEmpty() ? "" : existingImageUrls.get(new Random().nextInt(existingImageUrls.size()));
        return new QouteFeatureQuoteEntry("ai_" + todayKey, img, ar, en);
    }

    /** يستدعى بالخلفية بس — ما يوقف أي UI. النتيجة تُخزّن للمرة الجاية فقط، مو لهالفتحة. */
    public void generateAndCacheForNextTime() {
        String prompt = "اكتبي اقتباس تأملي قصير (جملة أو جملتين بالعربي، ومكافئه بالإنجليزي) — دافئ، إيجابي،\n" +
                "غير ديني وغير طبي، بنفس أسلوب هالأمثلة بالضبط:\n" +
                "- \"الألم اللي جوّاك مش نهاية القصة، هو بس فصل بتقدر تطويه.\"\n" +
                "- \"مش لازم تكون بخير كل يوم عشان تستاهل الحب والراحة.\"\n" +
                "- \"كل خطوة صغيرة بتعملها اليوم هي انتصار بيستحق التقدير.\"\n" +
                "\n" +
                "أرجعي فقط JSON بالصيغة: {\"ar\": \"...\", \"en\": \"...\"}\n" +
                "ممنوع أي إشارة لتشخيص أو دواء أو حالة نفسية محددة — نص عام داعم بس.";

        geminiService.generateStructuredQuote(prompt, new com.example.graduationproject.data.SalamGeminiService.GeminiCallback() {
            @Override
            public void onSuccess(String message) {
                try {
                    String cleaned = message;
                    if (message.contains("```json")) {
                        cleaned = message.substring(message.indexOf("```json") + 7);
                        if (cleaned.contains("```")) cleaned = cleaned.substring(0, cleaned.indexOf("```"));
                    }
                    
                    JSONObject json = new JSONObject(cleaned.trim());
                    String ar = json.getString("ar").trim();
                    String en = json.getString("en").trim();
                    
                    // الخطوة ٤: شبكة أمان خفيفة
                    if (!ar.isEmpty() && !en.isEmpty() && ar.length() < 300 && en.length() < 300) {
                        prefs.edit()
                                .putString(KEY_DATE, todayDateString())
                                .putString(KEY_AR, ar)
                                .putString(KEY_EN, en)
                                .apply();
                    }
                } catch (Exception ignored) {}
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private String todayDateString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }
}
