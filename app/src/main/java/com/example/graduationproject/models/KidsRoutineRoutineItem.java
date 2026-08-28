package com.example.graduationproject.models;

/**
 * Mirrors the item object shape from the original React component:
 * { id, emoji, label, period, done }
 */
public class KidsRoutineRoutineItem {

    public static final String PERIOD_MORNING = "صباح";
    public static final String PERIOD_NOON = "ظهر";
    public static final String PERIOD_EVENING = "مساء";

    private final String id;
    private String emoji;
    private String label;
    private String period;
    private boolean done;

    public KidsRoutineRoutineItem(String id, String emoji, String label, String period, boolean done) {
        this.id = id;
        this.emoji = emoji;
        this.label = label;
        this.period = period;
        this.done = done;
    }

    public String getId() { return id; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
