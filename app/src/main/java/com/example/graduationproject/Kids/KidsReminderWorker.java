package com.example.graduationproject.Kids;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.graduationproject.R;

/**
 * بيعرض إشعار "كيف تشعر الآن" لقسم الأطفال. بينفّذ مرة وحدة كل ما WorkManager يستدعيه
 * (بعد 24 ساعة من آخر فتح، حسب جدولة KidsReminderScheduler).
 */
public class KidsReminderWorker extends Worker {

    private static final String CHANNEL_ID = "kids_daily_reminder_channel";
    private static final int NOTIFICATION_ID = 2001;

    public KidsReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotification();
        return Result.success();
    }

    private void showNotification() {
        Context context = getApplicationContext();
        createChannelIfNeeded(context);

        // الضغط على الإشعار بيوديها مباشرة لشاشة تسجيل المزاج
        Intent intent = new Intent(context, MoodCheckInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // ⚠️ تأكدي إنه موجود، شوفي الملاحظة تحت
                .setContentTitle(context.getString(R.string.kids_reminder_title))
                .setContentText(context.getString(R.string.kids_reminder_body))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.kids_reminder_body)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // بأندرويد 13+ لازم صلاحية POST_NOTIFICATIONS قبل ما نعرض
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void createChannelIfNeeded(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.kids_reminder_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(context.getString(R.string.kids_reminder_channel_description));

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}