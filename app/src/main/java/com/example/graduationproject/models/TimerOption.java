package com.example.graduationproject.models;

/**
 * Equivalent of one entry in the TIMERS constant. `minutes` is -1 to
 * represent the "sleep" (unbounded) option, matching the original's
 * `k: "sleep"` sentinel value.
 */
public class TimerOption {
    public static final int SLEEP = 480; // 8 hours in minutes

    public final int minutes;
    public final String label;

    public TimerOption(int minutes, String label) {
        this.minutes = minutes;
        this.label = label;
    }

    public boolean isSleep() {
        return minutes == SLEEP;
    }
}
