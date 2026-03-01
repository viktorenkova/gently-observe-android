package com.gentleobserver.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gentleobserver.app.GentleObserverApp;
import com.gentleobserver.app.R;
import com.gentleobserver.app.database.DatabaseHelper;
import com.gentleobserver.app.models.CheckIn;

import java.util.Random;

/**
 * Evening CheckIn Activity - handles evening check-in flow
 */
public class EveningCheckInActivity extends AppCompatActivity {
    
    private CheckIn checkIn;
    private DatabaseHelper dbHelper;
    private int currentQuestion = 0;
    
    // Views
    private ProgressBar progressBar;
    private TextView tvQuestion;
    private LinearLayout answersContainer;
    private Button btnSkip;
    
    private static final int QUESTION_OVEREATING = 0;
    private static final int QUESTION_LONG_GAPS = 1;
    private static final int QUESTION_DAY_OVERALL = 2;
    private static final int QUESTION_CURRENT_HUNGER = 3;
    private static final int QUESTION_COMPLETE = 4;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        
        dbHelper = DatabaseHelper.getInstance(this);
        checkIn = new CheckIn();
        checkIn.setType(CheckIn.TYPE_EVENING);
        
        initViews();
        showCurrentQuestion();
    }
    
    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvQuestion = findViewById(R.id.tvQuestion);
        answersContainer = findViewById(R.id.answersContainer);
        btnSkip = findViewById(R.id.btnSkip);
        
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
    }
    
    private void showCurrentQuestion() {
        answersContainer.removeAllViews();
        updateProgress();
        
        switch (currentQuestion) {
            case QUESTION_OVEREATING:
                showOvereatingQuestion();
                break;
            case QUESTION_LONG_GAPS:
                showLongGapsQuestion();
                break;
            case QUESTION_DAY_OVERALL:
                showDayOverallQuestion();
                break;
            case QUESTION_CURRENT_HUNGER:
                showCurrentHungerQuestion();
                break;
            case QUESTION_COMPLETE:
                completeCheckIn();
                break;
        }
    }
    
    private void showOvereatingQuestion() {
        tvQuestion.setText(R.string.evening_overeating);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setOvereatingEpisodes("yes");
                nextQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setOvereatingEpisodes("no");
                nextQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_unsure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setOvereatingEpisodes("unsure");
                nextQuestion();
            }
        });
    }
    
    private void showLongGapsQuestion() {
        tvQuestion.setText(R.string.evening_long_gaps);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setLongGaps("yes");
                nextQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setLongGaps("no");
                nextQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_dont_remember, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setLongGaps("dont_remember");
                nextQuestion();
            }
        });
    }
    
    private void showDayOverallQuestion() {
        tvQuestion.setText(R.string.evening_day_overall);
        
        addAnswerButton(R.string.day_calm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setDayOverall("calm");
                nextQuestion();
            }
        });
        
        addAnswerButton(R.string.day_hard, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setDayOverall("hard");
                nextQuestion();
            }
        });
        
        addAnswerButton(R.string.day_exhausting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setDayOverall("exhausting");
                nextQuestion();
            }
        });
    }
    
    private void showCurrentHungerQuestion() {
        tvQuestion.setText(R.string.evening_current_hunger);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setCurrentHunger("yes");
                maybeShowExpressiveWindow();
            }
        });
        
        addAnswerButton(R.string.answer_somewhat, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setCurrentHunger("somewhat");
                maybeShowExpressiveWindow();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setCurrentHunger("no");
                maybeShowExpressiveWindow();
            }
        });
    }
    
    private void maybeShowExpressiveWindow() {
        // Always offer expressive window for evening check-in if under limit
        if (GentleObserverApp.getInstance().canShowExpressiveWindow() && new Random().nextBoolean()) {
            // Would show evening expressive window
            nextQuestion();
        } else {
            nextQuestion();
        }
    }
    
    private void addAnswerButton(int textResId, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(textResId);
        button.setBackgroundResource(R.drawable.button_answer_background);
        button.setTextColor(getResources().getColor(R.color.text_primary));
        button.setTextSize(getResources().getDimension(R.dimen.text_button));
        button.setPadding(
            (int) getResources().getDimension(R.dimen.button_padding_horizontal),
            (int) getResources().getDimension(R.dimen.button_padding_vertical),
            (int) getResources().getDimension(R.dimen.button_padding_horizontal),
            (int) getResources().getDimension(R.dimen.button_padding_vertical)
        );
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (int) getResources().getDimension(R.dimen.button_height)
        );
        params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.button_margin));
        button.setLayoutParams(params);
        
        button.setOnClickListener(listener);
        answersContainer.addView(button);
    }
    
    private void nextQuestion() {
        currentQuestion++;
        if (currentQuestion > QUESTION_CURRENT_HUNGER) {
            currentQuestion = QUESTION_COMPLETE;
        }
        showCurrentQuestion();
    }
    
    private void updateProgress() {
        int progress = ((currentQuestion + 1) * 100) / 5;
        progressBar.setProgress(progress);
    }
    
    private void completeCheckIn() {
        try {
            checkIn.markCompleted();
            dbHelper.insertCheckIn(checkIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Intent intent = new Intent(this, CheckInCompleteActivity.class);
        intent.putExtra("check_in_type", CheckIn.TYPE_EVENING);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
