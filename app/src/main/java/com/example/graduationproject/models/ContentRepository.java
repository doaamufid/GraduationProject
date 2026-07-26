package com.example.graduationproject.models;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static data source. Equivalent of the CATEGORIES, ITEMS, and
 * DISLIKE_REASONS constants at the top of the original file.
 */
public final class ContentRepository {

    public static final List<String> CATEGORIES = Arrays.asList("الكل", "قلق", "نوم", "صدمة", "علاقات");

    public static final List<String> DISLIKE_REASONS = Arrays.asList(
            "طويل زيادة", "مو مناسب لمزاجي الآن", "أفضل صوت بدل فيديو", "محتوى مكرر", "غير ذلك"
    );

    // Verified, genuinely appropriate, publicly embeddable video from a real
    // health institution - kept identical to the original DEMO_VIDEO_ID.
    private static final String DEMO_VIDEO_ID = "mAdwqHl7sac"; // UC San Diego Health

    private static final List<ContentItem> ITEMS = new ArrayList<>();

    static {
        ITEMS.add(new ContentItem(1, "كيف تتعامل مع القلق؟", "منهج الصحة النفسية", "فيديو", true,
                "١٢:٣٤", "قلق", DEMO_VIDEO_ID, Color.parseColor("#2E5C86"), Color.parseColor("#1F3A60"),
                "لأنك سجّلت مزاج \"قلق\" ٣ مرات هالأسبوع"));
        ITEMS.add(new ContentItem(2, "نوم أفضل في ٣ خطوات", "بودكاست سلامة", "بودكاست", false,
                "٢٣:٠٠", "نوم", DEMO_VIDEO_ID, Color.parseColor("#5C7C6E"), Color.parseColor("#3F5A4D"),
                "بناءً على استخدامك لأذكار النوم مؤخراً"));
        ITEMS.add(new ContentItem(3, "تقنية التنفس العميق", "د. نورا العلي", "فيديو", true,
                "٨:١٥", "قلق", DEMO_VIDEO_ID, Color.parseColor("#C97B6B"), Color.parseColor("#A85C4E"),
                "مكمّلة لتمارين التنفس اللي تسويها"));
        ITEMS.add(new ContentItem(4, "الصدمة والشفاء", "أصوات النفس", "بودكاست", false,
                "٣١:٤٠", "صدمة", DEMO_VIDEO_ID, Color.parseColor("#4A7B8C"), Color.parseColor("#2E5A6B"),
                "من مكتبتنا المفحوصة لموضوع الصدمة"));
        ITEMS.add(new ContentItem(5, "العلاقات الصحية", "مركز الأمل", "فيديو", true,
                "١٤:٠٢", "علاقات", DEMO_VIDEO_ID, Color.parseColor("#3A5FA0"), Color.parseColor("#1F3A60"),
                "مقترحة ضمن تصنيف العلاقات"));
    }

    private ContentRepository() {
    }

    public static List<ContentItem> getAllItems() {
        return ITEMS;
    }

    public static ContentItem findById(int id) {
        for (ContentItem item : ITEMS) {
            if (item.id == id) return item;
        }
        return null;
    }

    /** Equivalent of `cat === "الكل" ? ITEMS : ITEMS.filter(i => i.cat === cat)`. */
    public static List<ContentItem> filterByCategory(String category) {
        if ("الكل".equals(category)) return ITEMS;
        List<ContentItem> result = new ArrayList<>();
        for (ContentItem item : ITEMS) {
            if (item.category.equals(category)) result.add(item);
        }
        return result;
    }

    /** Equivalent of `ITEMS.filter(i => i.cat === item.cat && i.id !== item.id).slice(0, 2)`. */
    public static List<ContentItem> relatedTo(ContentItem item) {
        List<ContentItem> result = new ArrayList<>();
        for (ContentItem candidate : ITEMS) {
            if (candidate.category.equals(item.category) && candidate.id != item.id) {
                result.add(candidate);
                if (result.size() == 2) break;
            }
        }
        return result;
    }
}
