package com.example.graduationproject.Kids;

import android.content.Context;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * بيجدول تذكير للطفل بعد 24 ساعة من آخر مرة فتح فيها قسم الأطفال.
 * كل استدعاء لـ scheduleReminder() بيلغي أي تذكير قديم مجدول ويعيد الجدولة من الصفر
 * (هيك العداد بيتجدد تلقائيًا كل ما الطفل يفتح التطبيق).
 */
public final class KidsReminderScheduler {

    private static final String WORK_NAME = "kids_daily_reminder_work";

    private KidsReminderScheduler() {
    }

    public static void scheduleReminder(Context context) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(KidsReminderWorker.class)
                .setInitialDelay(24, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request);
    }

    public static void cancelReminder(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
    }
}