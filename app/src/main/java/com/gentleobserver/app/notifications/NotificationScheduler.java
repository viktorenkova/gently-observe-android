package com.gentleobserver.app.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.gentleobserver.app.GentleObserverApp;

import java.util.Calendar;

/**
 * Schedules check-in notifications
 * Compatible with Android 4.0+ (API 14+)
 */
public class NotificationScheduler {
    
    private static final int REQUEST_CODE_DAYTIME = 100;
    private static final int REQUEST_CODE_EVENING = 200;
    
    private static final long INTERVAL_DAYTIME = 2 * 60 * 60 * 1000; // 2 hours
    
    public static void scheduleNotifications(Context context) {
        if (!GentleObserverApp.getInstance().areNotificationsEnabled()) {
            return;
        }
        
        scheduleDaytimeNotifications(context);
        scheduleEveningNotification(context);
    }
    
    private static void scheduleDaytimeNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, CheckInReminderReceiver.class);
        intent.putExtra("type", "daytime");

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAYTIME,
            intent,
            flags
        );
        
        // Start from 9:00 AM
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 9);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        
        if (startTime.getTimeInMillis() < System.currentTimeMillis()) {
            startTime.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        // Schedule repeating alarm every 2 hours
        if (alarmManager != null) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                startTime.getTimeInMillis(),
                INTERVAL_DAYTIME,
                pendingIntent
            );
        }
    }
    
    private static void scheduleEveningNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, CheckInReminderReceiver.class);
        intent.putExtra("type", "evening");

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_EVENING,
            intent,
            flags
        );
        
        // Get user preferred time (default 20:00)
        int hour = GentleObserverApp.getInstance().getEveningCheckInHour();
        int minute = GentleObserverApp.getInstance().getEveningCheckInMinute();
        
        Calendar eveningTime = Calendar.getInstance();
        eveningTime.set(Calendar.HOUR_OF_DAY, hour);
        eveningTime.set(Calendar.MINUTE, minute);
        eveningTime.set(Calendar.SECOND, 0);
        
        if (eveningTime.getTimeInMillis() < System.currentTimeMillis()) {
            eveningTime.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        // Schedule daily repeating alarm
        if (alarmManager != null) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                eveningTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            );
        }
    }
    
    public static void cancelNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        // Cancel daytime
        Intent daytimeIntent = new Intent(context, CheckInReminderReceiver.class);
        PendingIntent daytimePending = PendingIntent.getBroadcast(
            context, REQUEST_CODE_DAYTIME, daytimeIntent, flags);
        if (alarmManager != null) {
            alarmManager.cancel(daytimePending);
        }
        daytimePending.cancel();

        // Cancel evening
        Intent eveningIntent = new Intent(context, CheckInReminderReceiver.class);
        PendingIntent eveningPending = PendingIntent.getBroadcast(
            context, REQUEST_CODE_EVENING, eveningIntent, flags);
        if (alarmManager != null) {
            alarmManager.cancel(eveningPending);
        }
        eveningPending.cancel();
    }
}
