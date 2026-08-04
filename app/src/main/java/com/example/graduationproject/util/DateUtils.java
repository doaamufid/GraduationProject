package com.example.graduationproject.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Equivalent of the small date helpers at the top of the original file:
 * toAr (Arabic-Indic digits), MONTHS_AR, formatDate, addDays, daysLeft.
 */
public final class DateUtils {

    private static final String[] AR_DIGITS = {"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩"};
    private static final String[] MONTHS_AR = {
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    };

    private DateUtils() {
    }

    /** Equivalent of `toAr(n)`: replaces 0-9 with Arabic-Indic digits. */
    public static String toAr(long n) {
        StringBuilder sb = new StringBuilder();
        for (char c : String.valueOf(n).toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(AR_DIGITS[c - '0']);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Equivalent of `formatDate(d)`: "١٢ يناير ٢٠٢٦". */
    public static String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        return toAr(day) + " " + MONTHS_AR[month] + " " + toAr(year);
    }

    /** Equivalent of `addDays(d, n)`. */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /** Equivalent of `daysLeft(target)`: whole days between today and target, clamped at 0. */
    public static long daysLeft(Date target) {
        Calendar targetCal = truncateToMidnight(target);
        Calendar todayCal = truncateToMidnight(new Date());
        long diffMs = targetCal.getTimeInMillis() - todayCal.getTimeInMillis();
        long days = Math.round(Math.ceil(diffMs / 86400000.0));
        return Math.max(0, days);
    }

    private static Calendar truncateToMidnight(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
