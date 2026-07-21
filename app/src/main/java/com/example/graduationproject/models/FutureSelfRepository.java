package com.example.graduationproject.models;

import com.example.graduationproject.util.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * In-memory singleton holding the `messages` state, equivalent to the
 * `const [messages, setMessages] = useState(SEED_MESSAGES)` in the root
 * component. Seeded with the exact same 3 demo messages.
 */
public final class FutureSelfRepository {

    public static final List<DateOption> DATE_OPTIONS = Arrays.asList(
            new DateOption("week", "أسبوع", 7),
            new DateOption("month", "شهر", 30),
            new DateOption("3m", "٣ أشهر", 90),
            new DateOption("year", "سنة", 365)
    );

    private static FutureSelfRepository instance;

    private final List<FutureMessage> messages = new ArrayList<>();

    private FutureSelfRepository() {
        seed();
    }

    public static synchronized FutureSelfRepository getInstance() {
        if (instance == null) {
            instance = new FutureSelfRepository();
        }
        return instance;
    }

    private void seed() {
        Date today = new Date();
        messages.add(new FutureMessage(1,
                "اليوم كان صعباً لكنك تجاوزته، وأنت أقوى مما تتخيل الآن. احتفظ بهذا الشعور — لاحقاً.",
                "قبل شهر", today, true));
        messages.add(new FutureMessage(2,
                "لا تنسى إنك وعدت نفسك تاخذ الأمور بروية أكثر — كيف الوضع الحين؟",
                "قبل ٤ أيام", DateUtils.addDays(today, 23), false));
        messages.add(new FutureMessage(3,
                "أتمنى تكون حققت الهدف اللي حطيته اليوم. وإن ما تحقق، أنت لسا بخير.",
                "قبل يومين", DateUtils.addDays(today, 210), false));
    }

    public List<FutureMessage> getMessages() {
        return messages;
    }

    public FutureMessage findById(long id) {
        for (FutureMessage m : messages) {
            if (m.id == id) return m;
        }
        return null;
    }

    /** Equivalent of `handleSaveNew`: prepends a new, not-yet-arrived message. */
    public void addMessage(String text, Date targetDate) {
        messages.add(0, new FutureMessage(System.currentTimeMillis(), text, "الآن", targetDate, false));
    }

    /** Equivalent of `handleSaveEdit`: updates text + targetDate of an existing message. */
    public void updateMessage(long id, String text, Date targetDate) {
        FutureMessage m = findById(id);
        if (m != null) {
            m.text = text;
            m.targetDate = targetDate;
        }
    }

    public void deleteMessage(long id) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).id == id) messages.remove(i);
        }
    }
}
