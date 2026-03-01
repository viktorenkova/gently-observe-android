package com.gentleobserver.app.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class representing a check-in entry
 */
public class CheckIn implements Serializable {
    
    public static final String TYPE_DAYTIME = "daytime";
    public static final String TYPE_EVENING = "evening";
    
    private long id;
    private String type;
    private long timestamp;
    private Long completedAt;
    private boolean skipped;
    
    // Body block
    private String hunger;
    private String fatigue;
    private String tension;
    
    // Food block
    private String hoursSinceLastMeal;
    private String ateSinceLastCheckIn;
    private String foodEnjoyment;
    
    // Mindfulness block
    private String ateMindfully;
    private String tastedFood;
    
    // Emotions block
    private String mood;
    private String urgeToEat;
    
    // Evening check-in specific
    private String overeatingEpisodes;
    private String longGaps;
    private String dayOverall;
    private String currentHunger;
    
    // Expressive window
    private String expressiveContent;
    private String expressiveContentType;
    private String expressiveBlockType;
    
    public CheckIn() {
        this.timestamp = new Date().getTime();
        this.type = TYPE_DAYTIME;
        this.skipped = false;
    }
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public Long getCompletedAt() { return completedAt; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
    
    public boolean isSkipped() { return skipped; }
    public void setSkipped(boolean skipped) { this.skipped = skipped; }
    
    // Body block getters/setters
    public String getHunger() { return hunger; }
    public void setHunger(String hunger) { this.hunger = hunger; }
    
    public String getFatigue() { return fatigue; }
    public void setFatigue(String fatigue) { this.fatigue = fatigue; }
    
    public String getTension() { return tension; }
    public void setTension(String tension) { this.tension = tension; }
    
    // Food block getters/setters
    public String getHoursSinceLastMeal() { return hoursSinceLastMeal; }
    public void setHoursSinceLastMeal(String hoursSinceLastMeal) { this.hoursSinceLastMeal = hoursSinceLastMeal; }
    
    public String getAteSinceLastCheckIn() { return ateSinceLastCheckIn; }
    public void setAteSinceLastCheckIn(String ateSinceLastCheckIn) { this.ateSinceLastCheckIn = ateSinceLastCheckIn; }
    
    public String getFoodEnjoyment() { return foodEnjoyment; }
    public void setFoodEnjoyment(String foodEnjoyment) { this.foodEnjoyment = foodEnjoyment; }
    
    // Mindfulness block getters/setters
    public String getAteMindfully() { return ateMindfully; }
    public void setAteMindfully(String ateMindfully) { this.ateMindfully = ateMindfully; }
    
    public String getTastedFood() { return tastedFood; }
    public void setTastedFood(String tastedFood) { this.tastedFood = tastedFood; }
    
    // Emotions block getters/setters
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    
    public String getUrgeToEat() { return urgeToEat; }
    public void setUrgeToEat(String urgeToEat) { this.urgeToEat = urgeToEat; }
    
    // Evening check-in getters/setters
    public String getOvereatingEpisodes() { return overeatingEpisodes; }
    public void setOvereatingEpisodes(String overeatingEpisodes) { this.overeatingEpisodes = overeatingEpisodes; }
    
    public String getLongGaps() { return longGaps; }
    public void setLongGaps(String longGaps) { this.longGaps = longGaps; }
    
    public String getDayOverall() { return dayOverall; }
    public void setDayOverall(String dayOverall) { this.dayOverall = dayOverall; }
    
    public String getCurrentHunger() { return currentHunger; }
    public void setCurrentHunger(String currentHunger) { this.currentHunger = currentHunger; }
    
    // Expressive window getters/setters
    public String getExpressiveContent() { return expressiveContent; }
    public void setExpressiveContent(String expressiveContent) { this.expressiveContent = expressiveContent; }
    
    public String getExpressiveContentType() { return expressiveContentType; }
    public void setExpressiveContentType(String expressiveContentType) { this.expressiveContentType = expressiveContentType; }
    
    public String getExpressiveBlockType() { return expressiveBlockType; }
    public void setExpressiveBlockType(String expressiveBlockType) { this.expressiveBlockType = expressiveBlockType; }
    
    public boolean isEvening() {
        return TYPE_EVENING.equals(type);
    }
    
    public Date getTimestampAsDate() {
        return new Date(timestamp);
    }
    
    public void markCompleted() {
        this.completedAt = new Date().getTime();
    }
}
