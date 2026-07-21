package com.example.graduationproject.models;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory singleton holding `habits` state, equivalent to
 * `useState(INITIAL_HABITS)` in the root component. Also holds the
 * static ICONS and AI_SUGGESTIONS constants.
 */
public final class HabitRepository {

    public static final String[] ICONS = {"\uD83C\uDF19", "\uD83C\uDF31", "\u270F\uFE0F", "\uD83E\uDEB6", "\uD83D\uDCA7", "\uD83D\uDDBC\uFE0F", "\uD83E\uDDD1"};

    private static HabitRepository instance;

    private final List<Habit> habits = new ArrayList<>();
    private final List<Suggestion> suggestions = new ArrayList<>();

    private HabitRepository() {
        seed();
    }

    public static synchronized HabitRepository getInstance() {
        if (instance == null) instance = new HabitRepository();
        return instance;
    }

    private void seed() {
        habits.add(new Habit(1, "تمرين تنفس صباحي", "\uD83E\uDDD1", true, 9, new Reminder(true, "9:00 AM")));
        habits.add(new Habit(2, "٣ أشياء ممتن لها", "\u270F\uFE0F", false, 12, new Reminder(true, "9:00 PM")));
        habits.add(new Habit(3, "شرب ماء كافي", "\uD83D\uDCA7", false, 5, new Reminder(false, "9:00 AM")));

        suggestions.add(new Suggestion("s1", "تنفس ٥ دقائق صباحاً", "\uD83E\uDDD1",
                "لاحظنا إنك ترتفع بمزاجك بعد يوم بدأته بتنفس", false));
        suggestions.add(new Suggestion("s2", "أذكار المساء", "\uD83C\uDF19",
                "روتين نوم مؤجّل — رزين مسائي هادي يساعد", true));
        suggestions.add(new Suggestion("s3", "تواصل مع صديق", "\u2600\uFE0F",
                "لاحظنا إشارات لعزلة الأخيرة", false));
        suggestions.add(new Suggestion("s4", "مشي ١٠ دقائق", "\uD83E\uDDD1",
                "الحركة البسيطة تخفف توتر الجسدي", false));
        suggestions.add(new Suggestion("s5", "قراءة ١٠ دقائق قبل النوم", "\uD83C\uDF19",
                "نشاط هادئ يساعد يبعدك عن الشاشة قبل النوم", true));
    }

    public List<Habit> getHabits() {
        return habits;
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public Habit findById(long id) {
        for (Habit h : habits) if (h.id == id) return h;
        return null;
    }

    /** Equivalent of `toggleHabit`: flips done and bumps/decrements streak. */
    public void toggleHabit(long id) {
        Habit h = findById(id);
        if (h == null) return;
        boolean newDone = !h.done;
        h.streak = newDone ? h.streak + 1 : Math.max(0, h.streak - 1);
        h.done = newDone;
    }

    /** Equivalent of `addHabit`. */
    public void addHabit(String name, String icon, Reminder reminder) {
        habits.add(new Habit(System.currentTimeMillis(), name, icon, false, 0, reminder));
    }

    /** Equivalent of `saveEdit`. */
    public void updateHabit(long id, String name, String icon, Reminder reminder) {
        Habit h = findById(id);
        if (h == null) return;
        h.name = name;
        h.icon = icon;
        h.reminder = reminder;
    }

    /** Equivalent of `addFromAI`. */
    public void addFromSuggestions(List<Suggestion> picked) {
        for (Suggestion s : picked) {
            habits.add(new Habit(System.currentTimeMillis() + (long) (Math.random() * 1000),
                    s.name, s.icon, false, 0, new Reminder(false, "9:00 AM")));
        }
    }
}
