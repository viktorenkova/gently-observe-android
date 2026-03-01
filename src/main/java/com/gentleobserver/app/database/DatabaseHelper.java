package com.gentleobserver.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gentleobserver.app.models.CheckIn;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Database Helper for storing check-in data
 * Compatible with Android 4.0+ (API 14+)
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "gentle_observer.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table name
    private static final String TABLE_CHECKINS = "checkins";
    
    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_COMPLETED_AT = "completed_at";
    private static final String KEY_SKIPPED = "skipped";
    
    // Body block
    private static final String KEY_HUNGER = "hunger";
    private static final String KEY_FATIGUE = "fatigue";
    private static final String KEY_TENSION = "tension";
    
    // Food block
    private static final String KEY_HOURS_SINCE_MEAL = "hours_since_meal";
    private static final String KEY_ATE_SINCE_LAST = "ate_since_last";
    private static final String KEY_FOOD_ENJOYMENT = "food_enjoyment";
    
    // Mindfulness block
    private static final String KEY_ATE_MINDFULLY = "ate_mindfully";
    private static final String KEY_TASTED_FOOD = "tasted_food";
    
    // Emotions block
    private static final String KEY_MOOD = "mood";
    private static final String KEY_URGE_TO_EAT = "urge_to_eat";
    
    // Evening specific
    private static final String KEY_OVEREATING_EPISODES = "overeating_episodes";
    private static final String KEY_LONG_GAPS = "long_gaps";
    private static final String KEY_DAY_OVERALL = "day_overall";
    private static final String KEY_CURRENT_HUNGER = "current_hunger";
    
    // Expressive window
    private static final String KEY_EXPRESSIVE_CONTENT = "expressive_content";
    private static final String KEY_EXPRESSIVE_CONTENT_TYPE = "expressive_content_type";
    private static final String KEY_EXPRESSIVE_BLOCK_TYPE = "expressive_block_type";
    
    // Create table SQL
    private static final String CREATE_TABLE_CHECKINS = "CREATE TABLE " + TABLE_CHECKINS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TYPE + " TEXT,"
            + KEY_TIMESTAMP + " INTEGER,"
            + KEY_COMPLETED_AT + " INTEGER,"
            + KEY_SKIPPED + " INTEGER DEFAULT 0,"
            + KEY_HUNGER + " TEXT,"
            + KEY_FATIGUE + " TEXT,"
            + KEY_TENSION + " TEXT,"
            + KEY_HOURS_SINCE_MEAL + " TEXT,"
            + KEY_ATE_SINCE_LAST + " TEXT,"
            + KEY_FOOD_ENJOYMENT + " TEXT,"
            + KEY_ATE_MINDFULLY + " TEXT,"
            + KEY_TASTED_FOOD + " TEXT,"
            + KEY_MOOD + " TEXT,"
            + KEY_URGE_TO_EAT + " TEXT,"
            + KEY_OVEREATING_EPISODES + " TEXT,"
            + KEY_LONG_GAPS + " TEXT,"
            + KEY_DAY_OVERALL + " TEXT,"
            + KEY_CURRENT_HUNGER + " TEXT,"
            + KEY_EXPRESSIVE_CONTENT + " TEXT,"
            + KEY_EXPRESSIVE_CONTENT_TYPE + " TEXT,"
            + KEY_EXPRESSIVE_BLOCK_TYPE + " TEXT"
            + ")";
    
    private static volatile DatabaseHelper instance;
    
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHECKINS);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle future database upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKINS);
        onCreate(db);
    }
    
    /**
     * Insert a new check-in
     */
    public long insertCheckIn(CheckIn checkIn) {
        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = checkInToValues(checkIn);
            id = db.insert(TABLE_CHECKINS, null, values);
            checkIn.setId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    /**
     * Update an existing check-in
     */
    public int updateCheckIn(CheckIn checkIn) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = checkInToValues(checkIn);
            return db.update(TABLE_CHECKINS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(checkIn.getId())});
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Get check-in by ID
     */
    public CheckIn getCheckIn(long id) {
        CheckIn checkIn = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.query(TABLE_CHECKINS, null, KEY_ID + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                checkIn = cursorToCheckIn(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return checkIn;
    }
    
    /**
     * Get all check-ins ordered by timestamp desc
     */
    public List<CheckIn> getAllCheckIns() {
        List<CheckIn> checkIns = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + TABLE_CHECKINS + " ORDER BY " + KEY_TIMESTAMP + " DESC";
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    checkIns.add(cursorToCheckIn(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return checkIns;
    }
    
    /**
     * Get check-ins for a date range
     */
    public List<CheckIn> getCheckInsForRange(long startTime, long endTime) {
        List<CheckIn> checkIns = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + TABLE_CHECKINS +
                    " WHERE " + KEY_TIMESTAMP + " >= ? AND " + KEY_TIMESTAMP + " <= ?" +
                    " ORDER BY " + KEY_TIMESTAMP + " DESC";
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(startTime), String.valueOf(endTime)});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    checkIns.add(cursorToCheckIn(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return checkIns;
    }
    
    /**
     * Get today's check-ins
     */
    public List<CheckIn> getTodaysCheckIns(long dayStart, long dayEnd) {
        return getCheckInsForRange(dayStart, dayEnd);
    }
    
    /**
     * Delete a check-in
     */
    public void deleteCheckIn(long id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CHECKINS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Delete all check-ins
     */
    public void deleteAllCheckIns() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_CHECKINS, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get count of all check-ins
     */
    public int getCheckInCount() {
        Cursor cursor = null;
        int count = 0;
        try {
            String countQuery = "SELECT * FROM " + TABLE_CHECKINS;
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
    
    /**
     * Convert CheckIn to ContentValues
     */
    private ContentValues checkInToValues(CheckIn checkIn) {
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, checkIn.getType());
        values.put(KEY_TIMESTAMP, checkIn.getTimestamp());
        values.put(KEY_COMPLETED_AT, checkIn.getCompletedAt());
        values.put(KEY_SKIPPED, checkIn.isSkipped() ? 1 : 0);
        values.put(KEY_HUNGER, checkIn.getHunger());
        values.put(KEY_FATIGUE, checkIn.getFatigue());
        values.put(KEY_TENSION, checkIn.getTension());
        values.put(KEY_HOURS_SINCE_MEAL, checkIn.getHoursSinceLastMeal());
        values.put(KEY_ATE_SINCE_LAST, checkIn.getAteSinceLastCheckIn());
        values.put(KEY_FOOD_ENJOYMENT, checkIn.getFoodEnjoyment());
        values.put(KEY_ATE_MINDFULLY, checkIn.getAteMindfully());
        values.put(KEY_TASTED_FOOD, checkIn.getTastedFood());
        values.put(KEY_MOOD, checkIn.getMood());
        values.put(KEY_URGE_TO_EAT, checkIn.getUrgeToEat());
        values.put(KEY_OVEREATING_EPISODES, checkIn.getOvereatingEpisodes());
        values.put(KEY_LONG_GAPS, checkIn.getLongGaps());
        values.put(KEY_DAY_OVERALL, checkIn.getDayOverall());
        values.put(KEY_CURRENT_HUNGER, checkIn.getCurrentHunger());
        values.put(KEY_EXPRESSIVE_CONTENT, checkIn.getExpressiveContent());
        values.put(KEY_EXPRESSIVE_CONTENT_TYPE, checkIn.getExpressiveContentType());
        values.put(KEY_EXPRESSIVE_BLOCK_TYPE, checkIn.getExpressiveBlockType());
        return values;
    }
    
    /**
     * Convert cursor to CheckIn
     */
    private CheckIn cursorToCheckIn(Cursor cursor) {
        CheckIn checkIn = new CheckIn();
        
        int index;
        
        index = cursor.getColumnIndex(KEY_ID);
        if (index >= 0) checkIn.setId(cursor.getLong(index));
        
        index = cursor.getColumnIndex(KEY_TYPE);
        if (index >= 0) checkIn.setType(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_TIMESTAMP);
        if (index >= 0) checkIn.setTimestamp(cursor.getLong(index));
        
        index = cursor.getColumnIndex(KEY_COMPLETED_AT);
        if (index >= 0 && !cursor.isNull(index)) {
            checkIn.setCompletedAt(cursor.getLong(index));
        }
        
        index = cursor.getColumnIndex(KEY_SKIPPED);
        if (index >= 0) checkIn.setSkipped(cursor.getInt(index) == 1);
        
        index = cursor.getColumnIndex(KEY_HUNGER);
        if (index >= 0) checkIn.setHunger(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_FATIGUE);
        if (index >= 0) checkIn.setFatigue(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_TENSION);
        if (index >= 0) checkIn.setTension(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_HOURS_SINCE_MEAL);
        if (index >= 0) checkIn.setHoursSinceLastMeal(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_ATE_SINCE_LAST);
        if (index >= 0) checkIn.setAteSinceLastCheckIn(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_FOOD_ENJOYMENT);
        if (index >= 0) checkIn.setFoodEnjoyment(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_ATE_MINDFULLY);
        if (index >= 0) checkIn.setAteMindfully(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_TASTED_FOOD);
        if (index >= 0) checkIn.setTastedFood(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_MOOD);
        if (index >= 0) checkIn.setMood(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_URGE_TO_EAT);
        if (index >= 0) checkIn.setUrgeToEat(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_OVEREATING_EPISODES);
        if (index >= 0) checkIn.setOvereatingEpisodes(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_LONG_GAPS);
        if (index >= 0) checkIn.setLongGaps(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_DAY_OVERALL);
        if (index >= 0) checkIn.setDayOverall(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_CURRENT_HUNGER);
        if (index >= 0) checkIn.setCurrentHunger(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_EXPRESSIVE_CONTENT);
        if (index >= 0) checkIn.setExpressiveContent(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_EXPRESSIVE_CONTENT_TYPE);
        if (index >= 0) checkIn.setExpressiveContentType(cursor.getString(index));
        
        index = cursor.getColumnIndex(KEY_EXPRESSIVE_BLOCK_TYPE);
        if (index >= 0) checkIn.setExpressiveBlockType(cursor.getString(index));
        
        return checkIn;
    }
}
