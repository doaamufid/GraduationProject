package com.example.graduationproject.util;

import java.util.Calendar;
import java.util.Date;

public final class MyFutureMsgDateUtils {
    private static final String[] myFutureMsgArDigits = {"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩"};
    private static final String[] myFutureMsgMonthsAr = {
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    };

    private MyFutureMsgDateUtils() {}

    public static String myFutureMsgToAr(long myFutureMsgValue) {
        StringBuilder myFutureMsgSb = new StringBuilder(String.valueOf(myFutureMsgValue));
        for (int myFutureMsgI = 0; myFutureMsgI < myFutureMsgSb.length(); myFutureMsgI++) {
            char myFutureMsgC = myFutureMsgSb.charAt(myFutureMsgI);
            if (myFutureMsgC >= '0' && myFutureMsgC <= '9') {
                myFutureMsgSb.setCharAt(myFutureMsgI, myFutureMsgArDigits[myFutureMsgC - '0'].charAt(0));
            }
        }
        return myFutureMsgSb.toString();
    }

    public static String myFutureMsgFormatDate(Date myFutureMsgDate) {
        Calendar myFutureMsgCal = Calendar.getInstance();
        myFutureMsgCal.setTime(myFutureMsgDate);
        int myFutureMsgDay = myFutureMsgCal.get(Calendar.DAY_OF_MONTH);
        int myFutureMsgMonth = myFutureMsgCal.get(Calendar.MONTH);
        int myFutureMsgYear = myFutureMsgCal.get(Calendar.YEAR);
        return myFutureMsgToAr(myFutureMsgDay) + " " + myFutureMsgMonthsAr[myFutureMsgMonth] + " " + myFutureMsgToAr(myFutureMsgYear);
    }

    public static Date myFutureMsgAddDays(Date myFutureMsgDate, int myFutureMsgDays) {
        Calendar myFutureMsgCal = Calendar.getInstance();
        myFutureMsgCal.setTime(myFutureMsgDate);
        myFutureMsgCal.add(Calendar.DAY_OF_MONTH, myFutureMsgDays);
        return myFutureMsgCal.getTime();
    }

    private static Date myFutureMsgAtMidnight(Date myFutureMsgDate) {
        Calendar myFutureMsgCal = Calendar.getInstance();
        myFutureMsgCal.setTime(myFutureMsgDate);
        myFutureMsgCal.set(Calendar.HOUR_OF_DAY, 0);
        myFutureMsgCal.set(Calendar.MINUTE, 0);
        myFutureMsgCal.set(Calendar.SECOND, 0);
        myFutureMsgCal.set(Calendar.MILLISECOND, 0);
        return myFutureMsgCal.getTime();
    }

    public static long myFutureMsgDaysLeft(Date myFutureMsgDate) {
        long myFutureMsgTarget = myFutureMsgAtMidnight(myFutureMsgDate).getTime();
        long myFutureMsgNow = myFutureMsgAtMidnight(new Date()).getTime();
        long myFutureMsgDiff = myFutureMsgTarget - myFutureMsgNow;
        long myFutureMsgDaysCount = (long) Math.ceil(myFutureMsgDiff / 86400000.0);
        return Math.max(0, myFutureMsgDaysCount);
    }

    public static long myFutureMsgDaysSince(Date myFutureMsgDate) {
        long myFutureMsgTarget = myFutureMsgAtMidnight(myFutureMsgDate).getTime();
        long myFutureMsgNow = myFutureMsgAtMidnight(new Date()).getTime();
        long myFutureMsgDiff = myFutureMsgNow - myFutureMsgTarget;
        long myFutureMsgDaysCount = (long) Math.floor(myFutureMsgDiff / 86400000.0);
        return Math.max(0, myFutureMsgDaysCount);
    }

    public static int myFutureMsgWordCount(String myFutureMsgText) {
        if (myFutureMsgText == null) return 0;
        String myFutureMsgTrimmed = myFutureMsgText.trim();
        if (myFutureMsgTrimmed.isEmpty()) return 0;
        return myFutureMsgTrimmed.split("\\s+").length;
    }
}
