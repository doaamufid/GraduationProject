package com.example.graduationproject.util;

/**
 * Bilingual string tables, ported 1:1 from the `STR` object in the
 * original React component.
 */
public class QouteFeatureLangStrings {

    public static final String[] DAYS_AR = {
            "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"
    };
    public static final String[] DAYS_EN = {
            "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"
    };

    public static final String[] MONTHS_AR = {
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    };
    public static final String[] MONTHS_EN = {
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };

    public static String brand(boolean ar) { return ar ? "سلام" : "Salam"; }

    public static String more(boolean ar) { return ar ? "دخول" : "MORE"; }

    public static String saveMsg(boolean ar) { return ar ? "تم الحفظ في مساحتي" : "Saved to my space"; }

    public static String copiedMsg(boolean ar) { return ar ? "تم نسخ الاقتباس" : "Quote copied"; }

    public static String am(boolean ar) { return ar ? "ص" : "a.m."; }

    public static String pm(boolean ar) { return ar ? "م" : "p.m."; }

    public static String greet(boolean ar, int hour24) {
        if (ar) {
            if (hour24 < 12) return "صباح الخير";
            if (hour24 < 18) return "طاب يومك";
            return "مساء الخير";
        } else {
            if (hour24 < 12) return "Good Morning";
            if (hour24 < 18) return "Good Afternoon";
            return "Good Evening";
        }
    }
}
