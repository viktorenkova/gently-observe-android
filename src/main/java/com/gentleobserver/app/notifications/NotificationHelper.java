package com.gentleobserver.app.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.gentleobserver.app.R;

/**
 * Helper class for managing notification channels
 * Required for Android 8.0+ (API 26+)
 */
public class NotificationHelper {

    public static final String CHANNEL_ID_CHECKINS = "checkin_reminders";
    public static final String CHANNEL_ID_EVENING = "evening_checkin";

    /**
     * Create notification channels for Android 8.0+
     * Should be called when app starts
     */
    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) return;

            // Channel for daytime check-in reminders
            NotificationChannel checkinChannel = new NotificationChannel(
                    CHANNEL_ID_CHECKINS,
                    context.getString(R.string.channel_checkins_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            checkinChannel.setDescription(context.getString(R.string.channel_checkins_description));

            // Channel for evening check-in
            NotificationChannel eveningChannel = new NotificationChannel(
                    CHANNEL_ID_EVENING,
                    context.getString(R.string.channel_evening_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            eveningChannel.setDescription(context.getString(R.string.channel_evening_description));

            notificationManager.createNotificationChannel(checkinChannel);
            notificationManager.createNotificationChannel(eveningChannel);
        }
    }
}
