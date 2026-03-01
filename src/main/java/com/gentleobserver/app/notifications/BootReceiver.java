package com.gentleobserver.app.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receives boot completed broadcast to reschedule notifications
 */
public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reschedule notifications after device reboot
            NotificationScheduler.scheduleNotifications(context);
        }
    }
}
