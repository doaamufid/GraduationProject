package com.example.graduationproject.util;

import java.util.Calendar;

/**
 * Port of the JS useDpcTimeOfDay / useApcTimeOfDay hooks.
 * Computes the sun/moon orb position and a 0..1 "darkness" value purely
 * from the wall-clock hour, independent from any habit progress.
 */
public class TimeOfDayUtils {

    public static class Snapshot {
        public float hour;        // 0..24 (fractional)
        public float darkness;    // 0 = bright noon, 1 = midnight
        public boolean isDaytime; // 6:00 - 19:00
        public float orbProgress; // 0..1 across its half of the sky
        public float orbTopPercent;   // % from top of the sky strip
        public float orbRightPercent; // % from the right
    }

    public static Snapshot compute() {
        Calendar c = Calendar.getInstance();
        float hour = c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) / 60f;

        Snapshot s = new Snapshot();
        s.hour = hour;
        // darkness: 0 at noon, 1 at midnight (symmetric cosine curve)
        s.darkness = (float) ((1 - Math.cos(((hour - 12) / 12.0) * Math.PI)) / 2.0);
        s.isDaytime = hour >= 6 && hour < 19;

        float orbProgress;
        if (s.isDaytime) {
            orbProgress = ColorUtils.clamp01((hour - 6) / 13f);
        } else {
            float nightHour = hour < 6 ? hour + 24 - 19 : hour - 19;
            orbProgress = ColorUtils.clamp01(nightHour / 11f);
        }
        s.orbProgress = orbProgress;
        s.orbTopPercent = (float) (50 - Math.sin(orbProgress * Math.PI) * 38);
        s.orbRightPercent = 8 + orbProgress * 78;
        return s;
    }
}
