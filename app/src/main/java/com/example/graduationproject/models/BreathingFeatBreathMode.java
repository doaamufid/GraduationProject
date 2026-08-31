package com.example.graduationproject.models;

import java.util.LinkedHashMap;
import java.util.Map;

/** Port of the JS RAW_MODES table. */
public class BreathingFeatBreathMode {
    public final String key;
    public final String name;
    public final String desc;
    public final int minutes;
    public final int inSec, hold1Sec, outSec, hold2Sec;
    public final int gradientStart, gradientEnd; // card background gradient
    public final int textColor;
    public final int iconColor;

    public BreathingFeatBreathMode(String key, String name, String desc, int minutes,
                                   int inSec, int hold1Sec, int outSec, int hold2Sec,
                                   int gradientStart, int gradientEnd, int textColor, int iconColor) {
        this.key = key;
        this.name = name;
        this.desc = desc;
        this.minutes = minutes;
        this.inSec = inSec;
        this.hold1Sec = hold1Sec;
        this.outSec = outSec;
        this.hold2Sec = hold2Sec;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.textColor = textColor;
        this.iconColor = iconColor;
    }

    public int patternTotalSeconds() {
        return inSec + hold1Sec + outSec + hold2Sec;
    }

    public String ratioLabel() {
        StringBuilder sb = new StringBuilder();
        int[] vals = {inSec, hold1Sec, outSec, hold2Sec};
        for (int i = 0; i < vals.length; i++) {
            if (i > 0) sb.append("-");
            sb.append(vals[i]);
        }
        return sb.toString();
    }

    /** Ordered, non-zero phases only, mirrors buildSteps(). */
    public static class Phase {
        public final String key;   // in / hold1 / out / hold2
        public final int seconds;
        public Phase(String key, int seconds) { this.key = key; this.seconds = seconds; }
    }

    public java.util.List<Phase> buildSteps() {
        java.util.List<Phase> steps = new java.util.ArrayList<>();
        if (inSec > 0) steps.add(new Phase("in", inSec));
        if (hold1Sec > 0) steps.add(new Phase("hold1", hold1Sec));
        if (outSec > 0) steps.add(new Phase("out", outSec));
        if (hold2Sec > 0) steps.add(new Phase("hold2", hold2Sec));
        return steps;
    }

    /** Fixed order of the four daily routine modes (ROUTINE_KEYS in the JS). */
    public static Map<String, BreathingFeatBreathMode> buildRoutineModes() {
        Map<String, BreathingFeatBreathMode> m = new LinkedHashMap<>();
        m.put("equal", new BreathingFeatBreathMode("equal", "التنفس المتساوي",
                "التنفس المتوازن يساعدك على الاسترخاء والتركيز", 2,
                4, 0, 4, 0,
                0xFFEAF7EC, 0xFFC9ECD2, 0xFF3D7A4E, 0xFF7FC491));
        m.put("box", new BreathingFeatBreathMode("box", "تنفس المربع",
                "تنفس المربع طريقة قوية لتقليل التوتر", 4,
                4, 4, 4, 4,
                0xFFFDF0E2, 0xFFF6D9B8, 0xFFA06A2C, 0xFFE8A860));
        m.put("relax478", new BreathingFeatBreathMode("relax478", "تنفس 478",
                "تنفس 4-7-8 يساعد على تحسين النوم", 5,
                4, 7, 8, 0,
                0xFFFDECF1, 0xFFF6CDDB, 0xFFA34F6E, 0xFFE58AA8));
        m.put("calm711", new BreathingFeatBreathMode("calm711", "تنفس 7-11",
                "تنفس 7-11 يساعد في تقليل القلق وتحسين النوم", 7,
                7, 0, 11, 0,
                0xFFE3F7F0, 0xFFB9E8D6, 0xFF2C7A63, 0xFF6EC3A4));
        return m;
    }
}
