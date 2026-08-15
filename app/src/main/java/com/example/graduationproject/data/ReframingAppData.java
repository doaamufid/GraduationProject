package com.example.graduationproject.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static content mirrors the constants declared at the top of the original
 * React file: PATTERNS, EXAMINE_QUESTIONS, REFRAMES, EXERCISES, EMOTIONS.
 */
public final class ReframingAppData {

    private ReframingAppData() {}

    /** A single cognitive-distortion pattern shown in the identify step's 2x2 grid. */
    public static class Pattern {
        public final String id;
        public final String label;
        public final String explain;

        public Pattern(String id, String label, String explain) {
            this.id = id;
            this.label = label;
            this.explain = explain;
        }
    }

    public static final Pattern[] PATTERNS = new Pattern[]{
            new Pattern(
                    "overgen",
                    "التعميم المفرط",
                    "كلمة \"دائماً\" تُعمّم حالة واحدة على كل حياتك — هذا نمط يُضخّم الألم."
            ),
            new Pattern(
                    "allnothing",
                    "الكل أو لا شيء",
                    "التفكير بطرفين فقط (نجاح كامل أو فشل كامل) يتجاهل كل المساحة بينهما."
            ),
            new Pattern(
                    "catastro",
                    "التنبؤ بالفشل",
                    "توقّع أسوأ نتيجة ممكنة وكأنها مؤكدة، قبل ما تصير فعلاً."
            ),
            new Pattern(
                    "neglect",
                    "إهمال الإيجابيات",
                    "التركيز فقط على السلبي وتجاهل أي شيء إيجابي حصل اليوم."
            ),
    };

    public static Pattern findPattern(String id) {
        for (Pattern p : PATTERNS) {
            if (p.id.equals(id)) return p;
        }
        return PATTERNS[0];
    }

    public static final Map<String, String> EXAMINE_QUESTIONS = new LinkedHashMap<>();
    static {
        EXAMINE_QUESTIONS.put("overgen", "هل تتذكر لحظة واحدة خالفت هذه الفكرة؟");
        EXAMINE_QUESTIONS.put("allnothing", "هل فيه احتمال ثالث غير النجاح الكامل أو الفشل الكامل؟");
        EXAMINE_QUESTIONS.put("catastro", "لو صار أسوأ سيناريو فعلاً، هل تقدر تتعامل معه؟");
        EXAMINE_QUESTIONS.put("neglect", "شنو الشي الإيجابي الوحيد اللي صار اليوم رغم كل شي؟");
    }

    public static final Map<String, String[]> REFRAMES = new LinkedHashMap<>();
    static {
        REFRAMES.put("overgen", new String[]{
                "بعض الأشياء ما مشت زي ما توقعت، بس هذا ما يعني إن كل محاولاتك بلا فايدة.",
                "فشلت بموقف واحد، وهذا مختلف تماماً عن إنك فاشل دائماً.",
                "أنت تتعلم من كل تجربة، حتى لو كانت صعبة."
        });
        REFRAMES.put("allnothing", new String[]{
                "فيه مساحة كبيرة بين النجاح الكامل والفشل الكامل، وأنت غالباً فيها.",
                "التقدم الجزئي لسا تقدم."
        });
        REFRAMES.put("catastro", new String[]{
                "أسوأ سيناريو نادراً ما يصير بالضبط زي ما نتخيله.",
                "حتى لو صار الأسوأ، تعاملت مع صعوبات قبل كذا ونجحت."
        });
        REFRAMES.put("neglect", new String[]{
                "فيه إيجابيات صارت اليوم، بس عقلك مركز بس على السلبي الآن.",
                "التوازن يحتاج تلاحظ الاثنين، مو بس وحد."
        });
    }

    public static final Map<String, String> EXERCISES = new LinkedHashMap<>();
    static {
        EXERCISES.put("overgen", "اكتب موقفين نجحت فيهم هذا الأسبوع رغم الصعوبة");
        EXERCISES.put("allnothing", "اكتب تقدماً صغيراً حققته اليوم، حتى لو ما كان كاملاً");
        EXERCISES.put("catastro", "اكتب خطة بسيطة لو صار أسوأ سيناريو فعلاً — بتلاحظ إنك قادر تتعامل معه");
        EXERCISES.put("neglect", "اكتب ٣ أشياء إيجابية صارت اليوم، مهما كانت صغيرة");
    }

    public static final String[] EMOTIONS = {
            "محبط", "خايف", "متعب", "قلقان", "حزين", "غاضب"
    };
}
