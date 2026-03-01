package com.gentleobserver.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gentleobserver.app.GentleObserverApp;
import com.gentleobserver.app.R;
import com.gentleobserver.app.database.DatabaseHelper;
import com.gentleobserver.app.models.CheckIn;
import com.gentleobserver.app.notifications.NotificationScheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Main Activity - Home screen of the application
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private TextView tvNextCheckIn;
    private Button btnStartCheckIn;
    private Button btnHistory;
    private Button btnSettings;
    private View cardTodaySummary;
    private TextView tvTodayCount;

    private DatabaseHelper dbHelper;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if first run - show onboarding
        try {
            if (GentleObserverApp.getInstance().isFirstRun()) {
                startActivity(new Intent(this, OnboardingActivity.class));
                finish();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Continue to main activity if there's an error
        }
        
        setContentView(R.layout.activity_main);
        
        try {
            dbHelper = DatabaseHelper.getInstance(this);
            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initViews();
        setupListeners();
        scheduleNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void initViews() {
        try {
            tvGreeting = findViewById(R.id.tvGreeting);
            tvNextCheckIn = findViewById(R.id.tvNextCheckIn);
            btnStartCheckIn = findViewById(R.id.btnStartCheckIn);
            btnHistory = findViewById(R.id.btnHistory);
            btnSettings = findViewById(R.id.btnSettings);
            cardTodaySummary = findViewById(R.id.cardTodaySummary);
            tvTodayCount = findViewById(R.id.tvTodayCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        if (btnStartCheckIn != null) {
            btnStartCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCheckIn();
                }
            });
        }

        if (btnHistory != null) {
            btnHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                }
            });
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });
        }
    }

    private void updateUI() {
        try {
            // Calculate next check-in time
            Calendar nextCheckIn = calculateNextCheckInTime();
            String nextTime = timeFormat.format(nextCheckIn.getTime());
            if (tvNextCheckIn != null) {
                tvNextCheckIn.setText(getString(R.string.next_check_in, nextTime));
            }

            // Update today's summary
            updateTodaySummary();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTodaySummary() {
        try {
            if (cardTodaySummary == null || tvTodayCount == null || dbHelper == null) {
                return;
            }

            Calendar startOfDay = Calendar.getInstance();
            startOfDay.set(Calendar.HOUR_OF_DAY, 0);
            startOfDay.set(Calendar.MINUTE, 0);
            startOfDay.set(Calendar.SECOND, 0);

            Calendar endOfDay = Calendar.getInstance();
            endOfDay.set(Calendar.HOUR_OF_DAY, 23);
            endOfDay.set(Calendar.MINUTE, 59);
            endOfDay.set(Calendar.SECOND, 59);

            List<CheckIn> todaysCheckIns = dbHelper.getTodaysCheckIns(
                    startOfDay.getTimeInMillis(), 
                    endOfDay.getTimeInMillis());

            if (todaysCheckIns.isEmpty()) {
                cardTodaySummary.setVisibility(View.GONE);
            } else {
                cardTodaySummary.setVisibility(View.VISIBLE);
                int count = todaysCheckIns.size();
                tvTodayCount.setText(count + " " + getPluralForm(count));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (cardTodaySummary != null) {
                cardTodaySummary.setVisibility(View.GONE);
            }
        }
    }

    private String getPluralForm(int count) {
        if (count == 1) {
            return "чек-ин";
        } else if (count >= 2 && count <= 4) {
            return "чек-ина";
        } else {
            return "чек-инов";
        }
    }

    private Calendar calculateNextCheckInTime() {
        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();
        
        // Simple algorithm: next check-in in 2 hours, but not after 21:00
        next.add(Calendar.HOUR_OF_DAY, 2);
        
        // If it's past evening time, set for tomorrow morning
        int eveningHour = GentleObserverApp.getInstance().getEveningCheckInHour();
        if (next.get(Calendar.HOUR_OF_DAY) >= eveningHour) {
            next.set(Calendar.HOUR_OF_DAY, 9);
            next.set(Calendar.MINUTE, 0);
            next.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        return next;
    }

    private void startCheckIn() {
        try {
            Intent intent = new Intent(this, CheckInActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotifications() {
        try {
            if (GentleObserverApp.getInstance().areNotificationsEnabled()) {
                NotificationScheduler.scheduleNotifications(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
