package com.gentleobserver.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.gentleobserver.app.R;

/**
 * Analytics Activity - shows charts and export options
 */
public class AnalyticsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        
        try {
            Button btnExport = findViewById(R.id.btnExport);
            if (btnExport != null) {
                btnExport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Would show export dialog
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
