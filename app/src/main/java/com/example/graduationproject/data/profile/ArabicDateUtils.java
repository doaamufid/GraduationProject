package com.example.graduationproject.data.profile;

import android.content.Context;
import com.example.graduationproject.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ArabicDateUtils {

    public static String toAr(int n) {
        // If we want to support both, we should check locale.
        // But the user said "default lang is Arabic".
        // For English, we should use standard digits.
        if (Locale.getDefault().getLanguage().equals("ar")) {
            String[] AR_DIGITS = { "٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩" };
            StringBuilder sb = new StringBuilder();
            for (char c : String.valueOf(n).toCharArray()) {
                if (Character.isDigit(c)) sb.append(AR_DIGITS[c - '0']);
                else sb.append(c);
            }
            return sb.toString();
        }
        return String.valueOf(n);
    }

    public static String formatDate(Context ctx, Date d) {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().equals("ar")) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            String[] months = ctx.getResources().getStringArray(R.array.months_ar);
            return toAr(day) + " " + months[month] + " " + toAr(year);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", locale);
            return sdf.format(d);
        }
    }

    public static Date addDays(Date d, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, n);
        return cal.getTime();
    }

    /** Mirrors daysLeftFn: whole days between today (midnight) and target (midnight), min 0. */
    public static int daysLeft(Date target) {
        Calendar today = midnight(new Date());
        Calendar targetCal = midnight(target);
        long diffMs = targetCal.getTimeInMillis() - today.getTimeInMillis();
        long days = Math.round(diffMs / 86400000.0);
        return (int) Math.max(0, days);
    }

    private static Calendar midnight(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
