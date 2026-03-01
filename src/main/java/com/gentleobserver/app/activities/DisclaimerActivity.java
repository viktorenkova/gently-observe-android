package com.gentleobserver.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gentleobserver.app.R;

/**
 * Disclaimer Activity - shows medical disclaimer
 */
public class DisclaimerActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
        
        try {
            TextView tvDisclaimer = findViewById(R.id.tvDisclaimer);
            Button btnAccept = findViewById(R.id.btnAccept);
            
            if (tvDisclaimer != null) {
                tvDisclaimer.setText(R.string.disclaimer_text);
            }
            
            if (btnAccept != null) {
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
