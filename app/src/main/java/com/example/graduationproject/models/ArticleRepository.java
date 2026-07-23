package com.example.graduationproject.models;

import com.example.graduationproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Static data source. Equivalent of CATEGORIES, CAT_STYLE, ARTICLES, and
 * DISLIKE_REASONS constants at the top of the original React file.
 */
public final class ArticleRepository {

    public static final String CAT_ALL = "الكل";
    public static final String CAT_GRIEF = "الحزن";
    public static final String CAT_ANXIETY = "القلق";
    public static final String CAT_CBT = "CBT";
    public static final String CAT_BREATHING = "التنفس";
    public static final String CAT_SLEEP = "نوم";

    public static final List<String> CATEGORIES = Arrays.asList(
            CAT_ALL, CAT_GRIEF, CAT_ANXIETY, CAT_CBT, CAT_BREATHING, CAT_SLEEP);

    public static final Map<String, CategoryStyle> CAT_STYLE = new LinkedHashMap<>();

    static {
        CAT_STYLE.put(CAT_GRIEF, new CategoryStyle(
                R.drawable.bg_art_grief, R.drawable.ic_heart, R.color.cat_grief_end, R.color.cat_grief_badge_bg));
        CAT_STYLE.put(CAT_ANXIETY, new CategoryStyle(
                R.drawable.bg_art_anxiety, R.drawable.ic_zap, R.color.cat_anxiety_end, R.color.cat_anxiety_badge_bg));
        CAT_STYLE.put(CAT_CBT, new CategoryStyle(
                R.drawable.bg_art_cbt, R.drawable.ic_brain, R.color.cat_cbt_end, R.color.cat_cbt_badge_bg));
        CAT_STYLE.put(CAT_BREATHING, new CategoryStyle(
                R.drawable.bg_art_breathing, R.drawable.ic_wind, R.color.cat_breathing_end, R.color.cat_breathing_badge_bg));
        CAT_STYLE.put(CAT_SLEEP, new CategoryStyle(
                R.drawable.bg_art_sleep, R.drawable.ic_moon, R.color.cat_sleep_end, R.color.cat_sleep_badge_bg));
    }

    /** Falls back to CBT style, matching `CAT_STYLE[a.cat] || CAT_STYLE["CBT"]` in the original. */
    public static CategoryStyle styleFor(String category) {
        CategoryStyle style = CAT_STYLE.get(category);
        return style != null ? style : CAT_STYLE.get(CAT_CBT);
    }

    public static final List<String> DISLIKE_REASONS = Arrays.asList(
            "طويلة زيادة", "مو مناسبة لمزاجي الآن", "معلومات أعرفها أصلاً", "لغة صعبة", "غير ذلك");

    private static final List<Article> ARTICLES = new ArrayList<>();

    static {
        ARTICLES.add(new Article(
                1, "كيف تتعاملين مع الحزن بدون قمعه؟", CAT_GRIEF, "٥ دقائق", "CBT", "د. نورا العلي",
                true, "لأن مزاجك مال للحزن مرتين هالأسبوع",
                new RelatedExercise("ذاكرة الامتنان", R.drawable.ic_sparkles_primary),
                new String[]{
                        "الحزن مشاعر إنسانية طبيعية تماماً، لكن كثير منا يتعلم من الصغر إنه يخفيه بدل ما يتعامل معه. قمع الحزن ما يخليه يختفي، بس يأجّله لوقت ثاني وبشكل أثقل.",
                        "أول خطوة صحية هي الاعتراف بالشعور بدون حكم عليه — بس تقولين لنفسك \"أنا حزينة الآن، وهذا مقبول\". هذا الاعتراف البسيط يقلل من حدة الشعور بشكل ملحوظ عند أغلب الناس.",
                        "بعدها، جربي تعطين نفسك وقت محدد للحزن (١٠-١٥ دقيقة مثلاً) بدل ما يمتد بلا حدود — هذا يعطيك إحساس بالسيطرة بدل ما تحسين إن الشعور يسيطر عليك.",
                        "وأخيراً، الحزن يخف غالباً لما تشاركينه مع شخص تثقين فيه، أو حتى تكتبينه لنفسك. التعبير — بأي شكل — أهم خطوة نحو المرور بالشعور مو تجاوزه بسرعة."
                }
        ));

        ARTICLES.add(new Article(
                2, "٧ تقنيات للتنفس في الأزمات", CAT_BREATHING, "٣ دقائق", "تمارين", "فريق سلام",
                false, "بناءً على استخدامك المتكرر لتمارين التنفس",
                new RelatedExercise("تمارين التنفس", R.drawable.ic_wind_primary),
                new String[]{"محتوى تجريبي لمقالة ثانية...", "فقرة ثانية للمقالة التجريبية."}
        ));

        ARTICLES.add(new Article(
                3, "ما هو CBT وكيف يساعدك؟", CAT_CBT, "٦ دقائق", "مبادئ", "د. سلمى فهد",
                false, "مرتبطة بتمرين إعادة الصياغة اللي جربتيه",
                new RelatedExercise("إعادة الصياغة", R.drawable.ic_sparkles_primary),
                new String[]{"محتوى تجريبي لمقالة ثانية...", "فقرة ثانية للمقالة التجريبية."}
        ));

        ARTICLES.add(new Article(
                4, "متى يتحول القلق لاضطراب؟", CAT_ANXIETY, "٤ دقائق", "توعية", "د. نورا العلي",
                false, "مرتبطة بتصنيفات مزاجك الأخيرة",
                new RelatedExercise("تمارين التنفس", R.drawable.ic_wind_primary),
                new String[]{"محتوى تجريبي...", "فقرة ثانية."}
        ));

        ARTICLES.add(new Article(
                5, "روتين مسائي لنوم أعمق", CAT_SLEEP, "٤ دقائق", "نوم", "فريق سلام",
                false, "بناءً على أوقات استخدامك المسائية",
                new RelatedExercise("أذكار المساء", R.drawable.ic_moon_primary),
                new String[]{"محتوى تجريبي...", "فقرة ثانية."}
        ));
    }

    private ArticleRepository() {
    }

    public static List<Article> getAll() {
        return ARTICLES;
    }

    public static Article findById(int id) {
        for (Article a : ARTICLES) if (a.id == id) return a;
        return null;
    }

    public static Article getFeatured() {
        for (Article a : ARTICLES) if (a.featured) return a;
        return null;
    }

    /**
     * Equivalent of:
     *   (cat === "الكل" ? ARTICLES : ARTICLES.filter(a => a.cat === cat))
     *     .filter(a => a.id !== featured?.id || cat !== "الكل")
     * i.e. the grid excludes the featured article ONLY while viewing "الكل"
     * (since it's already shown in the hero card there).
     */
    public static List<Article> getGridArticles(String category) {
        Article featured = getFeatured();
        List<Article> result = new ArrayList<>();
        for (Article a : ARTICLES) {
            boolean matchesCategory = category.equals(CAT_ALL) || a.category.equals(category);
            if (!matchesCategory) continue;
            boolean isFeaturedOnAllTab = featured != null && a.id == featured.id && category.equals(CAT_ALL);
            if (isFeaturedOnAllTab) continue;
            result.add(a);
        }
        return result;
    }

    /** Equivalent of `ARTICLES.filter(a => a.cat === article.cat && a.id !== article.id).slice(0, 2)`. */
    public static List<Article> getSuggestions(Article article) {
        List<Article> result = new ArrayList<>();
        for (Article a : ARTICLES) {
            if (a.category.equals(article.category) && a.id != article.id) {
                result.add(a);
                if (result.size() == 2) break;
            }
        }
        return result;
    }
}
