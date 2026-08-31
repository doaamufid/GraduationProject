package com.example.graduationproject.data;

import android.content.Context;
import org.json.JSONArray;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatSafetyRuleScreener {

    public enum ScreenResult { NORMAL, DIAGNOSIS_REQUEST, MEDICATION_REQUEST, CRISIS_SIGNAL }

    private static final String[] DIAGNOSIS_PATTERNS = {
        "عندي اكتئاب", "أنا مكتئب", "هل أنا مكتئب", "عندي قلق نفسي", "أنا مصاب بـ",
        "شخصني", "شخّصيني", "am i depressed", "do i have depression", "do i have anxiety", "do i have bipolar"
    };

    private static final String[] MEDICATION_VERBS = { "وصف", "خذ دوا", "زوّد الجرعة", "بطّل الدوا", "غيّر الجرعة", "increase dose", "stop taking" };
    private static final String[] MEDICATION_NAMES = { "بروزاك", "زولوفت", "ريسبردال", "ابيليفاي", "medication", "antidepressant", "prozac", "zoloft" };

    private final List<String> crisisLexicon;

    public ChatSafetyRuleScreener(Context context) {
        this.crisisLexicon = loadCrisisLexicon(context);
    }

    private List<String> loadCrisisLexicon(Context context) {
        List<String> lexicon = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("crisis_lexicon.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                lexicon.add(array.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lexicon;
    }

    public ScreenResult screen(String message) {
        if (message == null) return ScreenResult.NORMAL;
        String normalized = message.toLowerCase().trim();

        for (String phrase : crisisLexicon) {
            if (!phrase.trim().isEmpty() && normalized.contains(phrase.toLowerCase())) {
                return ScreenResult.CRISIS_SIGNAL;
            }
        }
        for (String p : DIAGNOSIS_PATTERNS) {
            if (normalized.contains(p)) return ScreenResult.DIAGNOSIS_REQUEST;
        }
        for (String verb : MEDICATION_VERBS) {
            for (String name : MEDICATION_NAMES) {
                if (normalized.contains(verb) && normalized.contains(name)) return ScreenResult.MEDICATION_REQUEST;
            }
        }
        return ScreenResult.NORMAL;
    }
}
