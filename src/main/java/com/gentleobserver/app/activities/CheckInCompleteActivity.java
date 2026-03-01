package com.gentleobserver.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gentleobserver.app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Check-in complete screen - shows confirmation after check-in
 */
public class CheckInCompleteActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_complete);
        
        try {
            String checkInType = getIntent().getStringExtra("check_in_type");
            
            TextView tvTitle = findViewById(R.id.tvCompleteTitle);
            TextView tvMessage = findViewById(R.id.tvCompleteMessage);
            Button btnBack = findViewById(R.id.btnBackToHome);
            
            if (tvTitle != null) {
                tvTitle.setText(R.string.check_in_complete);
            }
            
            // Calculate next check-in time
            Calendar nextCheckIn = Calendar.getInstance();
            if ("evening".equals(checkInType)) {
                nextCheckIn.add(Calendar.DAY_OF_YEAR, 1);
                nextCheckIn.set(Calendar.HOUR_OF_DAY, 9);
                nextCheckIn.set(Calendar.MINUTE, 0);
            } else {
                nextCheckIn.add(Calendar.HOUR_OF_DAY, 2);
            }
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String nextTime = timeFormat.format(nextCheckIn.getTime());
            if (tvMessage != null) {
                tvMessage.setText(getString(R.string.check_in_thanks, nextTime));
            }
            
            if (btnBack != null) {
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            // Clear the entire back stack and start MainActivity fresh
                            Intent intent = new Intent(CheckInCompleteActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Fallback: just finish this activity
                            finish();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
