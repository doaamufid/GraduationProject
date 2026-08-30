package com.example.graduationproject.Kids;

/**
 * حسابات نظام "النجوم" لميزة الطفل المميز.
 * مركزيّة بمكان واحد حتى تستخدمها أي شاشة (Activity/Adapter) بدون تكرار.
 */
public final class StarUtils {

    private static final int MAX_VISUAL_STARS = 5;

    private StarUtils() {
        // كلاس أدوات فقط - ما بدنا نعمل منه instance
    }

    /**
     * يحسب تقييم الطفل من 5 نجوم نسبةً لأعلى طفل بالقائمة.
     * أعلى طفل دايمًا 5/5. الباقي بالنسبة.
     */
    public static int calculateVisualStars(int childStars, int topStars) {
        if (topStars <= 0) return 0;
        double ratio = (double) childStars / topStars;
        return (int) Math.round(ratio * MAX_VISUAL_STARS);
    }

    /**
     * يحوّل عدد النجوم الملونة لنص إيموجي جاهز للعرض: ⭐⭐⭐☆☆
     */
    public static String starsToEmoji(int filledStars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_VISUAL_STARS; i++) {
            sb.append(i < filledStars ? "⭐" : "☆");
        }
        return sb.toString();
    }
}