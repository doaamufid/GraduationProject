package com.example.graduationproject.util;

import java.util.concurrent.atomic.AtomicLong;

public class AdultChatIdGen {
    private static final AtomicLong COUNTER = new AtomicLong(0);

    public static void setStart(long val) {
        COUNTER.set(val);
    }

    public static long next() {
        return COUNTER.incrementAndGet();
    }
}
