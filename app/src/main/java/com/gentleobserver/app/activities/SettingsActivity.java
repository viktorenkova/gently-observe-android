package com.gentleobserver.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.gentleobserver.app.GentleObserverApp;
import com.gentleobserver.app.R;
import com.gentleobserver.app.database.DatabaseHelper;
import com.gentleobserver.app.models.CheckIn;
import com.gentleobserver.app.notifications.NotificationScheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Settings Activity - app settings and data management
 */
public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NOTIFICATIONS = 1001;
    private static final int REQUEST_CODE_STORAGE = 1002;

    private Switch switchNotifications;
    private Button btnExport;
    private Button btnDelete;
    private Button btnDisclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchNotifications = findViewById(R.id.switchNotifications);
        btnExport = findViewById(R.id.btnExport);
        btnDelete = findViewById(R.id.btnDelete);
        btnDisclaimer = findViewById(R.id.btnDisclaimer);

        // Load settings - don't set listener yet to avoid triggering on initial load
        switchNotifications.setChecked(GentleObserverApp.getInstance().areNotificationsEnabled());
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // For Android 13+, request runtime permission first
                if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                            Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // Permission not granted, request it
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                REQUEST_CODE_NOTIFICATIONS);
                        // Revert the switch until permission is granted
                        switchNotifications.setChecked(false);
                        return;
                    }
                }
                updateNotificationState(isChecked);
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportData();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmDialog();
            }
        });

        btnDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, DisclaimerActivity.class));
            }
        });
    }

    private void updateNotificationState(boolean isChecked) {
        GentleObserverApp.getInstance().setNotificationsEnabled(isChecked);
        if (isChecked) {
            NotificationScheduler.scheduleNotifications(SettingsActivity.this);
        } else {
            NotificationScheduler.cancelNotifications(SettingsActivity.this);
        }
    }

    private void exportData() {
        // Check storage permission for older Android versions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE);
                return;
            }
        }
        performExport();
    }

    private void performExport() {
        try {
            List<CheckIn> checkIns = DatabaseHelper.getInstance(this).getAllCheckIns();
            if (checkIns.isEmpty()) {
                Toast.makeText(this, R.string.export_no_data, Toast.LENGTH_SHORT).show();
                return;
            }

            // Create CSV file in app's private directory
            File exportDir = new File(getExternalFilesDir(null), "exports");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(exportDir, "gentle_observer_export_" + timestamp + ".csv");

            FileWriter writer = new FileWriter(file);
            // Write CSV header
            writer.append("ID,Type,Date,Hunger,Fatigue,Tension,Hours Since Meal,Ate Since Last,Food Enjoyment,Ate Mindfully,Tasted Food,Mood,Urge to Eat\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            for (CheckIn checkIn : checkIns) {
                writer.append(String.format(Locale.getDefault(),
                    "%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    checkIn.getId(),
                    checkIn.getType(),
                    dateFormat.format(new Date(checkIn.getTimestamp())),
                    checkIn.getHunger(),
                    checkIn.getFatigue(),
                    checkIn.getTension(),
                    checkIn.getHoursSinceLastMeal(),
                    checkIn.getAteSinceLastCheckIn(),
                    checkIn.getFoodEnjoyment(),
                    checkIn.getAteMindfully(),
                    checkIn.getTastedFood(),
                    checkIn.getMood(),
                    checkIn.getUrgeToEat()
                ));
            }
            writer.flush();
            writer.close();

            // Share the file
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.export_body));

            // Use FileProvider for secure file sharing
            android.net.Uri fileUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, getString(R.string.export_title)));

        } catch (IOException e) {
            Toast.makeText(this, R.string.export_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable notifications
                switchNotifications.setChecked(true);
                updateNotificationState(true);
            } else {
                // Permission denied
                Toast.makeText(this, R.string.permission_notifications_denied, Toast.LENGTH_LONG).show();
                switchNotifications.setChecked(false);
            }
        } else if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performExport();
            } else {
                Toast.makeText(this, R.string.permission_storage_denied, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.settings_delete_all)
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.delete_yes, new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    DatabaseHelper.getInstance(SettingsActivity.this).deleteAllCheckIns();
                }
            })
            .setNegativeButton(R.string.delete_no, null)
            .show();
    }
}
