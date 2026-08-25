package com.example.graduationproject.data;

import com.example.graduationproject.R;
import com.example.graduationproject.models.CategoryMeta;
import com.example.graduationproject.models.Preset;

import java.util.LinkedHashMap;
import java.util.Map;

/** Static config mirroring the constants at the top of the JS file. */
public class Constants {

    public static final Preset[] PRESETS = new Preset[]{
            new Preset("sunset", "🌅", "غروب هادئ", R.drawable.gradient_sunset),
            new Preset("sea", "🌊", "بحر ساكن", R.drawable.gradient_sea),
            new Preset("forest", "🌳", "بين الشجر", R.drawable.gradient_forest),
            new Preset("peace", "🕊️", "سلام داخلي", R.drawable.gradient_peace),
            new Preset("family", "🤍", "أحبابي", R.drawable.gradient_family),
            new Preset("night", "🌙", "ليل هادئ", R.drawable.gradient_night),
    };

    public static final String[] PHRASE_SUGGESTIONS = new String[]{
            "هذا الشعور مؤقت، وبيمر",
            "أنا نجوت من قبل، وبنجو هلأ",
            "خذي نفس... أنتِ بأمان الآن",
            "أنا أقوى من اللي أفكر فيه",
            "تذكري: نجوتِ من كل يوم صعب مر عليك",
            "صوتك يستاهل يُسمع",
            "بخير حتى لو مو تمام الآن",
            "هالّلحظة رح تعدي، متل كل مرة",
    };

    /** duration options in minutes — never below 1 minute */
    public static final int[] DURATION_OPTIONS = new int[]{1, 2, 3, 5};

    public static final Map<String, CategoryMeta> CATEGORY_META = new LinkedHashMap<>();
    static {
        CATEGORY_META.put("قلق", new CategoryMeta("🕊️", "طمأنينة"));
        CATEGORY_META.put("حزن", new CategoryMeta("🌾", "صبر"));
        CATEGORY_META.put("خوف", new CategoryMeta("🛡️", "حفظ"));
        CATEGORY_META.put("عام", new CategoryMeta("📿", "عام"));
        CATEGORY_META.put("امتنان", new CategoryMeta("🌟", "شكر"));
    }
    public static final String[] CATEGORY_KEYS = CATEGORY_META.keySet().toArray(new String[0]);

    /** 4-7-8 breathing phase definition (durationMs, target scale, seconds shown) */
    public static class BreathPhase {
        public final String key;
        public final String label;
        public final int durMs;
        public final float scale;
        public final int secs;
        public final int cycle;

        public BreathPhase(String key, String label, int durMs, float scale, int secs, int cycle) {
            this.key = key;
            this.label = label;
            this.durMs = durMs;
            this.scale = scale;
            this.secs = secs;
            this.cycle = cycle;
        }
    }

    private static final Object[][] BASE_PHASES = {
            {"inhale", "شهيقي ببطء من الأنف", 4000, 1.18f, 4},
            {"hold", "احبسي نفسك بهدوء", 7000, 1.18f, 7},
            {"exhale", "زفّري ببطء من الفم", 8000, 0.82f, 8},
    };
    public static final int BREATH_CYCLES = 2;

    public static BreathPhase[] buildBreathSequence() {
        BreathPhase[] seq = new BreathPhase[BASE_PHASES.length * BREATH_CYCLES];
        int idx = 0;
        for (int c = 0; c < BREATH_CYCLES; c++) {
            for (Object[] p : BASE_PHASES) {
                seq[idx++] = new BreathPhase((String) p[0], (String) p[1], (int) p[2], (float) p[3], (int) p[4], c + 1);
            }
        }
        return seq;
    }
}
