package com.gentleobserver.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
 * CheckIn Activity - handles daytime check-in flow
 * Glass Design System with 4 category colors
 */
public class CheckInActivity extends AppCompatActivity {

    public static final String EXTRA_CHECK_IN_TYPE = "check_in_type";
    public static final String TYPE_DAYTIME = "daytime";
    
    // Block types
    private static final int BLOCK_BODY = 0;
    private static final int BLOCK_FOOD = 1;
    private static final int BLOCK_MINDFULNESS = 2;
    private static final int BLOCK_EMOTIONS = 3;
    private static final int BLOCK_COMPLETE = 4;
    
    // Category names
    private static final String CATEGORY_BODY = "ТЕЛО";
    private static final String CATEGORY_FOOD = "ЕДА";
    private static final String CATEGORY_MINDFULNESS = "ОСОЗНАННОСТЬ";
    private static final String CATEGORY_EMOTIONS = "ЭМОЦИИ";
    
    private int currentBlock = BLOCK_BODY;
    private CheckIn checkIn;
    private DatabaseHelper dbHelper;
    
    // Views
    private ProgressBar progressBar;
    private TextView tvQuestion;
    private TextView tvCategory;
    private LinearLayout answersContainer;
    private LinearLayout questionContainer;
    private Button btnSkip;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        
        dbHelper = DatabaseHelper.getInstance(this);
        checkIn = new CheckIn();
        checkIn.setType(TYPE_DAYTIME);
        
        initViews();
        showCurrentBlock();
    }
    
    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCategory = findViewById(R.id.tvCategory);
        answersContainer = findViewById(R.id.answersContainer);
        questionContainer = findViewById(R.id.questionContainer);
        btnSkip = findViewById(R.id.btnSkip);
        
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipCurrentBlock();
            }
        });
    }
    
    private void showCurrentBlock() {
        answersContainer.removeAllViews();
        updateProgress();
        updateCategoryStyle();
        
        switch (currentBlock) {
            case BLOCK_BODY:
                showBodyBlock();
                break;
            case BLOCK_FOOD:
                showFoodBlock();
                break;
            case BLOCK_MINDFULNESS:
                showMindfulnessBlock();
                break;
            case BLOCK_EMOTIONS:
                showEmotionsBlock();
                break;
            case BLOCK_COMPLETE:
                completeCheckIn();
                break;
        }
    }
    
    /**
     * Update category label and card styling based on current block
     */
    private void updateCategoryStyle() {
        int bgResId;
        int categoryColorResId;
        String categoryName;
        
        switch (currentBlock) {
            case BLOCK_BODY:
                categoryName = CATEGORY_BODY;
                bgResId = R.drawable.glass_card_body;
                categoryColorResId = R.color.category_body_text;
                break;
            case BLOCK_FOOD:
                categoryName = CATEGORY_FOOD;
                bgResId = R.drawable.glass_card_food;
                categoryColorResId = R.color.category_food_text;
                break;
            case BLOCK_MINDFULNESS:
                categoryName = CATEGORY_MINDFULNESS;
                bgResId = R.drawable.glass_card_awareness;
                categoryColorResId = R.color.category_awareness_text;
                break;
            case BLOCK_EMOTIONS:
                categoryName = CATEGORY_EMOTIONS;
                bgResId = R.drawable.glass_card_emotions;
                categoryColorResId = R.color.category_emotions_text;
                break;
            default:
                categoryName = "";
                bgResId = R.drawable.glass_card_background;
                categoryColorResId = R.color.text_secondary;
                break;
        }
        
        tvCategory.setText(categoryName);
        tvCategory.setTextColor(ContextCompat.getColor(this, categoryColorResId));
        questionContainer.setBackgroundResource(bgResId);
    }
    
    private void showBodyBlock() {
        tvQuestion.setText(R.string.body_hunger);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setHunger("yes");
                showSecondBodyQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_somewhat, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setHunger("somewhat");
                showSecondBodyQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setHunger("no");
                showSecondBodyQuestion();
            }
        });
    }
    
    private void showSecondBodyQuestion() {
        answersContainer.removeAllViews();
        tvQuestion.setText(R.string.body_fatigue);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setFatigue("yes");
                showThirdBodyQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setFatigue("no");
                showThirdBodyQuestion();
            }
        });
    }
    
    private void showThirdBodyQuestion() {
        answersContainer.removeAllViews();
        tvQuestion.setText(R.string.body_tension);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setTension("yes");
                nextBlock();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setTension("no");
                nextBlock();
            }
        });
    }
    
    private void showFoodBlock() {
        tvQuestion.setText(R.string.food_hours_since_meal);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setHoursSinceLastMeal("yes");
                showSecondFoodQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setHoursSinceLastMeal("no");
                showSecondFoodQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_unsure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setHoursSinceLastMeal("unsure");
                showSecondFoodQuestion();
            }
        });
    }
    
    private void showSecondFoodQuestion() {
        answersContainer.removeAllViews();
        tvQuestion.setText(R.string.food_ate_since_last);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setAteSinceLastCheckIn("yes");
                showThirdFoodQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setAteSinceLastCheckIn("no");
                nextBlock();
            }
        });
    }
    
    private void showThirdFoodQuestion() {
        answersContainer.removeAllViews();
        tvQuestion.setText(R.string.food_enjoyment);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setFoodEnjoyment("yes");
                nextBlock();
            }
        });
        
        addAnswerButton(R.string.answer_okay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setFoodEnjoyment("okay");
                nextBlock();
            }
        });
        
        addAnswerButton(R.string.answer_not_really, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setFoodEnjoyment("not_really");
                nextBlock();
            }
        });
    }
    
    private void showMindfulnessBlock() {
        tvQuestion.setText(R.string.mindfulness_ate_mindfully);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setAteMindfully("yes");
                showSecondMindfulnessQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_partially, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setAteMindfully("partially");
                showSecondMindfulnessQuestion();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setAteMindfully("no");
                showSecondMindfulnessQuestion();
            }
        });
    }
    
    private void showSecondMindfulnessQuestion() {
        answersContainer.removeAllViews();
        tvQuestion.setText(R.string.mindfulness_tasted_food);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setTastedFood("yes");
                nextBlock();
            }
        });
        
        addAnswerButton(R.string.answer_partially, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setTastedFood("partially");
                nextBlock();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setTastedFood("no");
                nextBlock();
            }
        });
    }
    
    private void showEmotionsBlock() {
        tvQuestion.setText(R.string.emotions_mood);
        
        addAnswerButton(R.string.mood_calm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setMood("calm");
                showSecondEmotionsQuestion();
            }
        });
        
        addAnswerButton(R.string.mood_tired, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setMood("tired");
                showSecondEmotionsQuestion();
            }
        });
        
        addAnswerButton(R.string.mood_irritated, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setMood("irritated");
                showSecondEmotionsQuestion();
            }
        });
        
        addAnswerButton(R.string.mood_sad, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setMood("sad");
                showSecondEmotionsQuestion();
            }
        });
    }
    
    private void showSecondEmotionsQuestion() {
        answersContainer.removeAllViews();
        tvQuestion.setText(R.string.emotions_urge_to_eat);
        
        addAnswerButton(R.string.answer_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setUrgeToEat("yes");
                maybeShowExpressiveWindow();
            }
        });
        
        addAnswerButton(R.string.answer_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn.setUrgeToEat("no");
                maybeShowExpressiveWindow();
            }
        });
    }
    
    private void maybeShowExpressiveWindow() {
        // Randomly decide to show expressive window (max 2 per day)
        if (GentleObserverApp.getInstance().canShowExpressiveWindow() && new Random().nextBoolean()) {
            // Would show expressive window dialog here
            // For now, just complete
            nextBlock();
        } else {
            nextBlock();
        }
    }
    
    private void addAnswerButton(int textResId, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(textResId);
        button.setBackgroundResource(R.drawable.button_answer_background);
        button.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        // Use fixed text size for simplicity
        button.setTextSize(18);
        int paddingH = (int) (28 * getResources().getDisplayMetrics().density);
        int paddingV = (int) (16 * getResources().getDisplayMetrics().density);
        button.setPadding(paddingH, paddingV, paddingH, paddingV);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (12 * getResources().getDisplayMetrics().density);
        params.setMargins(0, 0, 0, margin);
        button.setLayoutParams(params);
        
        button.setOnClickListener(listener);
        answersContainer.addView(button);
    }
    
    private void skipCurrentBlock() {
        nextBlock();
    }
    
    private void nextBlock() {
        currentBlock++;
        if (currentBlock > BLOCK_EMOTIONS) {
            currentBlock = BLOCK_COMPLETE;
        }
        showCurrentBlock();
    }
    
    private void updateProgress() {
        int progress = (currentBlock * 100) / 5;
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
        intent.putExtra("check_in_type", TYPE_DAYTIME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
