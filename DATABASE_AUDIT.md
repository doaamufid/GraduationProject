# Database & Data Modeling Audit Report - Project SALAM

**Date:** September 2026  
**Subject:** Full technical audit of database schemas, data models, and storage mechanisms for Safety Architecture Specification.

---

## 1. Database Configuration (Room Database)

The project uses a single Room database for general/adult chat functionality.

- **Class:** `com.example.graduationproject.data.ChatDatabase`
- **Database Name:** `chat_db`
- **Version:** `1`
- **Export Schema:** `false`
- **Entities:**
    - `ChatMessageEntity.class`
- **Type Converters:** None registered.
- **Initialization:** Singleton pattern using `Room.databaseBuilder` in `getInstance(Context)`.
- **Destructive Migration:** Not enabled (`fallbackToDestructiveMigration()` is NOT present).
- **Views:** None.
- **Main Thread Queries:** Not enabled.

```java
@Database(entities = {ChatMessageEntity.class}, version = 1, exportSchema = false)
public abstract class ChatDatabase extends RoomDatabase {
    private static volatile ChatDatabase INSTANCE;
    public abstract ChatMessageDao chatMessageDao();
    public static ChatDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ChatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ChatDatabase.class, "chat_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

---

## 2. All Entities & Tables

### A. Room Entity: `chat_messages`
Stored in `ChatMessageEntity.java`. This table appears to be used by the adult/general chat companion.

```java
@Entity(tableName = "chat_messages")
public class ChatMessageEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public boolean fromUser;
    public String text;          // Null for voice messages
    public String time;          // Display text
    public long timestamp;       // Millis - used for 7-day auto-delete
    public String cardType;      // "breathing" | "dhikr" | "article" | null
    public String audioPath;     // Null for text messages
    public int audioDurationSec;
}
```

### B. SQLCipher SQLite Tables (`children_wellbeing.db`)
Managed by `ChildProfileStore.java`. This database is **encrypted using SQLCipher (AES-256)**.

- **Passphrase:** `SalamApp@2026SecureKeyAES256` (Hardcoded in `ChildProfileStore.java`).
- **Tables:**

#### 1. `child_profiles`
Stores profiles for the children (e.g., Youssef, Sara).
- `id`: INTEGER PRIMARY KEY AUTOINCREMENT
- `name`: TEXT NOT NULL
- `age`: INTEGER NOT NULL
- `gender`: TEXT NOT NULL DEFAULT 'غير محدد'
- `avatar`: TEXT NOT NULL
- `created_at`: INTEGER NOT NULL
- `points`: INTEGER DEFAULT 0 (Stars/XP)

#### 2. `child_behavior_events`
Tracks specific events for safety triggers and stats.
- `id`: INTEGER PRIMARY KEY AUTOINCREMENT
- `child_id`: INTEGER (FK -> `child_profiles.id`)
- `event_type`: TEXT NOT NULL (Values: `EXERCISE`, `CHAT_SESSION`, `CALM_CORNER`, etc.)
- `event_value`: TEXT
- `notes`: TEXT
- `occurred_at`: INTEGER NOT NULL

#### 3. `bot_messages`
Stores messages from the AI bot specifically for children.
- `id`: INTEGER PRIMARY KEY AUTOINCREMENT
- `child_id`: INTEGER (FK -> `child_profiles.id`)
- `text`: TEXT NOT NULL
- `mood`: TEXT
- `created_at`: INTEGER NOT NULL

#### 4. `chat_messages` (Kids specific)
Different from the Room table.
- `id`: INTEGER PRIMARY KEY AUTOINCREMENT
- `child_id`: INTEGER (FK -> `child_profiles.id`)
- `message_text`: TEXT NOT NULL
- `is_user`: INTEGER (0/1)
- `timestamp`: INTEGER NOT NULL

#### 5. `sounds` & `videos`
Catalog of therapeutic content.
- `sounds`: `id`, `title`, `icon_name`, `audio_file`, `sound_type`, `sort_order`
- `videos`: `id`, `title`, `subtitle`, `category`, `thumbnail_name`, `bg_color_hex`, `video_file`, `duration`

---

## 3. Enums in Data Layer

- `com.example.graduationproject.data.ChatSafetyRuleScreener.ScreenResult`: `{ NORMAL, DIAGNOSIS_REQUEST, MEDICATION_REQUEST, CRISIS_SIGNAL }`.
- Used in `SalamGeminiService` for real-time safety logic.
- **Storage:** Not stored in DB; used for ephemeral processing.

---

## 4. Type Converters

**Room (`ChatDatabase`):** No TypeConverters used. All fields are primitive types or Strings.

---

## 5. DAOs & Queries

### `ChatMessageDao` (Room)
- `insert(ChatMessageEntity entity)`
- `getAll()`: `SELECT * FROM chat_messages ORDER BY id ASC`
- `deleteOlderThan(long thresholdMillis)`: `DELETE FROM chat_messages WHERE timestamp < :thresholdMillis` (Used for 7-day retention).
- `deleteAll()`

### `ChildProfileStore` (Raw SQL)
- `getChatHistory(long childId)`: `SELECT ... FROM chat_messages WHERE child_id = ? ORDER BY timestamp ASC`
- `getRealChildStats(long childId)`: Complex aggregation queries for exercises, sessions, and visits.
- `hasCompletedEventToday(long childId, String eventType)`: Checks if a specific event occurred since midnight today.

---

## 6. Repository Layer

- `AppRepository`: **In-memory singleton** for Adult "Cards" and "Dhikr". Lost on app close.
- `HabitRepository`: **In-memory singleton** for Adult Habits.
- `MyFutureMsgRepository`: **In-memory singleton** for Adult "Message to Self".
- `ChildProfileStore`: Acts as a direct repository for all Child data, persisted via SQLCipher.
- `LocalStorageHelper`: Repository for drawing results, persisted via **JSON in SharedPreferences**.

---

## 7. Profile Modeling (Critical Audit)

- **Multiple Profiles:** Supported via `child_profiles` table.
- **Adult vs Child:** 
    - Adults use `AppPrefs` SharedPreferences and in-memory repositories. No `AdultProfileEntity` exists.
    - Children use `ChildProfileStore` (SQLCipher).
- **Active Profile Tracking:** Managed by `ActiveChildManager` via `SharedPreferences` ("KidsAppPrefs", key `active_child_id`).
- **Age Storage:** Stored as an `INTEGER` (raw number) in `child_profiles` and an index of `AGE_BRACKETS` for adults.
- **"Alam Noor" Tracking (4-7 years):**
    - **Code Evidence:** `ChildProfileStore.getRealChildStats()` does NOT have any age-based checks. It calculates stats for ANY `child_id` passed to it.
    - **Conclusion:** Behavior tracking logic is identical for all children regardless of age category in the current implementation.

---

## 8. Chat Storage & Safety

- **Raw Text Storage:** Yes, both `chat_messages` (Room) and `TABLE_CHAT` (SQLite) store raw text.
- **Clinical Fields:** None found. No `diagnosis` or `severityScore` fields in entities.
- **7-Day Auto-Delete:** 
    - Room: `ChatMessageDao.deleteOlderThan()` exists.
    - SQLite (Kids): No evidence of auto-delete implementation for `TABLE_CHAT` or `bot_messages` found in `ChildProfileStore.java`.
- **Pattern Detection:** `ChatSafetyRuleScreener.java` uses hardcoded patterns (`DIAGNOSIS_PATTERNS`, `MEDICATION_VERBS`) for real-time detection.

---

## 9. Mood Tracking

- **Adults:** Stored in `SharedPreferences` ("AppPrefs") with keys like `mood_2026_244_id`.
- **Children:** Stored as a `mood` string in `TABLE_BOT_MESSAGES` or as a `behavior_event`.

---

## 10. Notifications

- **NotificationEntity:** **NON-EXISTENT**. The suggested entity from Safety Doc Part 21 does not exist.
- **AdultNotifNotificationItem:** An in-memory POJO.
- **Workers:** Only `KidsReminderWorker` exists (shows a daily notification). `WeeklyReportWorker` is NOT in the code.

---

## 11. Achievements & Streaks

- **Growth Tracking:** `TreeProgressManager` uses `SharedPreferences` (keyed by `childId`) to store points/XP.
- **Streaks:** `Habit.streak` exists in-memory in `HabitRepository`.
- **Strengths Bank:** `StrengthsRepository` is in-memory. No ML integration logic for trait extraction was found in the data layer (mostly handled via Gemini prompts).

---

## 12. Content Catalog

- **Table Based:** Only for Kids (`sounds`, `videos` in `ChildProfileStore`).
- **Asset/In-memory Based:** Articles, Quotes, Dhikr (for adults) are in-memory or static arrays in `SeedData.java` and `ReframingAppData.java`.

---

## 13. Onboarding

- **Adults:** Ephemeral state during onboarding, partially saved to `AppPrefs`.
- **Kids:** `KidsAdaptivePrefsManager` uses `SharedPreferences` for onboarding flags and persona choices.

---

## 14. Encryption & Security Status

- **SQLCipher:** **ACTIVE** for `children_wellbeing.db`.
- **Room (`chat_db`):** **PLAINTEXT**. No `SupportFactory` (SQLCipher) implementation found in `ChatDatabase.java`.
- **Android Keystore:** Not used for database keys (passphrase is hardcoded).
- **allowBackup:** Not audited (requires Manifest check).
- **DataStore:** Not used.

---

## 15. Gemini / Firebase Integration

- **SDK:** Uses `com.google.firebase:firebase-ai` (Vertex AI for Firebase / Gemini AI Logic).
- **Logic:** Direct implementation in `SalamGeminiService.java` and `GeminiService.java`.
- **API Keys:** Fetched from `BuildConfig.GEMINI_API_KEY` (injected from `local.properties`).

---

## 16. Data Modeling Summary Table

| Table/Storage | Purpose | Owner | Free Text? | Sensitivity | Encrypted? |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `chat_messages` (Room) | Adult/General Chat | Shared | Yes | High | NO |
| `child_profiles` | Kids Profiles | Kids | No | Medium | YES |
| `chat_messages` (SQLite)| Kids Chat | Kids | Yes | High | YES |
| `child_behavior_events` | Safety/Stats | Kids | Yes (Notes) | High | YES |
| `AppPrefs` (SP) | Adult Mood/Onboarding | Adult | No | Medium | NO |
| `noor_results_prefs` (SP)| Drawing History | Kids | Yes (Feedback)| High | NO |
| `HabitRepository` (Mem) | Daily Habits | Adult | No | Low | N/A |

---

## 17. Gaps vs Safety Specification (Part 21)

- **Missing Entities:** `PersonalizationProfileEntity`, `CrisisResourceEntity`, `ApprovedContentEntity`, `SafetyEventLogEntity`, `NotificationEntity`.
- **Encryption Gap:** The Adult/General chat database (`chat_db`) is NOT encrypted.
- **Persistence Gap:** Most adult features (Habits, Dhikr, Future Messages) are in-memory and will not survive a process kill.
- **Worker Gap:** `WeeklyReportWorker` and `EmotionEnrichmentWorker` are missing.
- **Safety Gap:** The SQLCipher passphrase is hardcoded in the source code.

---

**End of Audit Report**
