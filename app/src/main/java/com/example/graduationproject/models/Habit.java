package com.example.graduationproject.models;

public class Habit {
    public long id;
    public String name;
    public String icon;
    public boolean done;
    public int streak;
    public Reminder reminder;

    public Habit(long id, String name, String icon, boolean done, int streak, Reminder reminder) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.done = done;
        this.streak = streak;
        this.reminder = reminder;
    }
}
