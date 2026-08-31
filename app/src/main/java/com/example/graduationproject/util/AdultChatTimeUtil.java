package com.example.graduationproject.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdultChatTimeUtil {
    public static String nowTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("hh:mm a", new Locale("ar"));
        return fmt.format(new Date());
    }

    public static String mmss(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", m, s);
    }

    public static int parseDuration(String mmss) {
        String[] parts = mmss.split(":");
        int m = Integer.parseInt(parts[0]);
        int s = Integer.parseInt(parts[1]);
        return m * 60 + s;
    }
}
