package com.gentleobserver.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.gentleobserver.app.R;
import com.gentleobserver.app.database.DatabaseHelper;
import com.gentleobserver.app.models.CheckIn;

import java.util.List;

/**
 * History Activity - shows past check-ins
 */
public class HistoryActivity extends AppCompatActivity {
    
    private RecyclerView rvHistory;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        
        try {
            dbHelper = DatabaseHelper.getInstance(this);
            rvHistory = findViewById(R.id.rvHistory);
            tvEmpty = findViewById(R.id.tvEmpty);
            loadHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadHistory() {
        try {
            if (dbHelper == null || tvEmpty == null || rvHistory == null) {
                return;
            }
            
            List<CheckIn> checkIns = dbHelper.getAllCheckIns();
            
            if (checkIns.isEmpty()) {
                tvEmpty.setVisibility(android.view.View.VISIBLE);
                rvHistory.setVisibility(android.view.View.GONE);
            } else {
                tvEmpty.setVisibility(android.view.View.GONE);
                rvHistory.setVisibility(android.view.View.VISIBLE);
                // Would use adapter here
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (tvEmpty != null) {
                tvEmpty.setVisibility(android.view.View.VISIBLE);
            }
            if (rvHistory != null) {
                rvHistory.setVisibility(android.view.View.GONE);
            }
        }
    }
}
