package com.gentleobserver.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gentleobserver.app.notifications.NotificationHelper;

/**
 * Application class for Gentle Observer
 * Handles global app state and preferences
 */
public class GentleObserverApp extends Application {
    
    private static final String PREFS_FIRST_RUN = "first_run";
    private static final String PREFS_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String PREFS_EVENING_TIME = "evening_time";
    private static final String PREFS_EXPRESSIVE_COUNT_TODAY = "expressive_count_today";
    private static final String PREFS_LAST_EXPRESSIVE_DATE = "last_expressive_date";
    
    private static volatile GentleObserverApp instance;
    private SharedPreferences prefs;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Create notification channels for Android 8.0+
        try {
            NotificationHelper.createNotificationChannels(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static GentleObserverApp getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Application not created yet");
        }
        return instance;
    }
    
    // First run
    public boolean isFirstRun() {
        return prefs.getBoolean(PREFS_FIRST_RUN, true);
    }
    
    public void setFirstRunComplete() {
        prefs.edit().putBoolean(PREFS_FIRST_RUN, false).apply();
    }
    
    // Notifications
    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(PREFS_NOTIFICATIONS_ENABLED, true);
    }
    
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(PREFS_NOTIFICATIONS_ENABLED, enabled).apply();
    }
    
    // Evening check-in time (default 20:00 = 8 PM)
    public int getEveningCheckInHour() {
        return prefs.getInt(PREFS_EVENING_TIME + "_hour", 20);
    }
    
    public int getEveningCheckInMinute() {
        return prefs.getInt(PREFS_EVENING_TIME + "_minute", 0);
    }
    
    public void setEveningCheckInTime(int hour, int minute) {
        prefs.edit()
                .putInt(PREFS_EVENING_TIME + "_hour", hour)
                .putInt(PREFS_EVENING_TIME + "_minute", minute)
                .apply();
    }
    
    // Expressive window daily counter
    public int getExpressiveCountToday() {
        long lastDate = prefs.getLong(PREFS_LAST_EXPRESSIVE_DATE, 0);
        long today = System.currentTimeMillis() / (1000 * 60 * 60 * 24);
        
        if (lastDate != today) {
            // Reset for new day
            prefs.edit()
                    .putInt(PREFS_EXPRESSIVE_COUNT_TODAY, 0)
                    .putLong(PREFS_LAST_EXPRESSIVE_DATE, today)
                    .apply();
            return 0;
        }
        
        return prefs.getInt(PREFS_EXPRESSIVE_COUNT_TODAY, 0);
    }
    
    public void incrementExpressiveCount() {
        int count = getExpressiveCountToday();
        prefs.edit().putInt(PREFS_EXPRESSIVE_COUNT_TODAY, count + 1).apply();
    }
    
    public boolean canShowExpressiveWindow() {
        return getExpressiveCountToday() < 2;
    }
}
