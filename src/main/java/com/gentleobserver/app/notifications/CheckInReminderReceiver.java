package com.gentleobserver.app.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.gentleobserver.app.R;
import com.gentleobserver.app.activities.CheckInActivity;
import com.gentleobserver.app.activities.EveningCheckInActivity;

/**
 * Receives alarm broadcasts and shows notifications
 * Compatible with Android 4.0+ using NotificationCompat
 */
public class CheckInReminderReceiver extends BroadcastReceiver {
    
    private static final int NOTIFICATION_ID_DAYTIME = 1;
    private static final int NOTIFICATION_ID_EVENING = 2;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        
        if ("evening".equals(type)) {
            showEveningNotification(context);
        } else {
            showDaytimeNotification(context);
        }
    }
    
    private void showDaytimeNotification(Context context) {
        Intent intent = new Intent(context, CheckInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            flags
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_CHECKINS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_check_in_title))
            .setContentText(context.getString(R.string.notification_check_in_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(Notification.DEFAULT_VIBRATE);
        
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID_DAYTIME, builder.build());
        }
    }
    
    private void showEveningNotification(Context context) {
        Intent intent = new Intent(context, EveningCheckInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            flags
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_EVENING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_evening_title))
            .setContentText(context.getString(R.string.notification_evening_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(Notification.DEFAULT_VIBRATE);
        
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID_EVENING, builder.build());
        }
    }
}
