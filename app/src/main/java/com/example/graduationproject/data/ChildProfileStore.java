package com.example.graduationproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.example.graduationproject.Fragments.profile.ChildDetailFragment;
import com.example.graduationproject.Kids.BotMessage;
import com.example.graduationproject.Kids.ChatMessage;
import com.example.graduationproject.models.ChildProfile;
import com.example.graduationproject.models.SoundItem;
import com.example.graduationproject.models.VideoItem;
import com.example.graduationproject.models.profile.ChildAlert;
import com.example.graduationproject.models.profile.ChildFeature;
import com.example.graduationproject.models.profile.ChildHistoryEntry;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChildProfileStore extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "children_wellbeing.db";
    private static final int DATABASE_VERSION = 6;

    public static final String DATABASE_PASSPHRASE = "SalamApp@2026SecureKeyAES256";
    private static final String TABLE_BOT_MESSAGES = "bot_messages";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_MOOD = "mood";
    private static final String TABLE_PROFILES = "child_profiles";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_POINTS = "points";
    private static final String COLUMN_CREATED_AT = "created_at";

    private static final String TABLE_EVENTS = "child_behavior_events";
    private static final String COLUMN_CHILD_ID = "child_id";
    private static final String COLUMN_EVENT_TYPE = "event_type";
    private static final String COLUMN_EVENT_VALUE = "event_value";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_OCCURRED_AT = "occurred_at";

    private static final String TABLE_CHAT = "chat_messages";
    private static final String COLUMN_MESSAGE_TEXT = "message_text";
    private static final String COLUMN_IS_USER = "is_user";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String OLD_PREFS_NAME = "child_profiles_prefs";
    private static final String OLD_KEY_PROFILES = "child_profiles";

    private static final String TABLE_SOUNDS = "sounds";
    private static final String COLUMN_SOUND_TITLE = "title";
    private static final String COLUMN_SOUND_ICON = "icon_name";
    private static final String COLUMN_SOUND_FILE = "audio_file";
    private static final String COLUMN_SOUND_TYPE = "sound_type";
    private static final String COLUMN_SOUND_ORDER = "sort_order";

    private static final String TABLE_VIDEOS = "videos";
    private static final String COLUMN_VIDEO_TITLE = "title";
    private static final String COLUMN_VIDEO_SUBTITLE = "subtitle";
    private static final String COLUMN_VIDEO_CATEGORY = "category";
    private static final String COLUMN_VIDEO_THUMBNAIL = "thumbnail_name";
    private static final String COLUMN_VIDEO_BG_COLOR = "bg_color_hex";
    private static final String COLUMN_VIDEO_FILE = "video_file";
    private static final String COLUMN_VIDEO_DURATION = "duration";

    public ChildProfileStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase.loadLibs(context);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PROFILES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_AGE + " INTEGER NOT NULL, "
                + COLUMN_GENDER + " TEXT NOT NULL DEFAULT 'غير محدد', "
                + COLUMN_AVATAR + " TEXT NOT NULL, "
                + COLUMN_POINTS + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_CREATED_AT + " INTEGER NOT NULL"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CHILD_ID + " INTEGER NOT NULL, "
                + COLUMN_EVENT_TYPE + " TEXT NOT NULL, "
                + COLUMN_EVENT_VALUE + " TEXT, "
                + COLUMN_NOTES + " TEXT, "
                + COLUMN_OCCURRED_AT + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_CHILD_ID + ") REFERENCES " + TABLE_PROFILES + "(" + COLUMN_ID + ") ON DELETE CASCADE"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_BOT_MESSAGES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CHILD_ID + " INTEGER NOT NULL, "
                + COLUMN_TEXT + " TEXT NOT NULL, "
                + COLUMN_MOOD + " TEXT, "
                + COLUMN_CREATED_AT + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_CHILD_ID + ") REFERENCES " + TABLE_PROFILES + "(" + COLUMN_ID + ") ON DELETE CASCADE"
                + ")");

        createSoundsAndVideosTables(db);
        seedSoundsAndVideosData(db);
        createChatTable(db);
    }

    private void createChatTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHAT + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CHILD_ID + " INTEGER NOT NULL, "
                + COLUMN_MESSAGE_TEXT + " TEXT NOT NULL, "
                + COLUMN_IS_USER + " INTEGER NOT NULL, "
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_CHILD_ID + ") REFERENCES " + TABLE_PROFILES + "(" + COLUMN_ID + ") ON DELETE CASCADE"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PROFILES
                    + " ADD COLUMN " + COLUMN_GENDER + " TEXT NOT NULL DEFAULT 'غير محدد'");
            createSoundsAndVideosTables(db);
            seedSoundsAndVideosData(db);
        }

        // 🟢 تحديث إضافة عمود points عند الانتقال للإصدار 6
        if (oldVersion < 6) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_PROFILES + " ADD COLUMN " + COLUMN_POINTS + " INTEGER NOT NULL DEFAULT 0");
            } catch (Exception ignored) {
                // العمود موجود بالفعل
            }
        }

        if (oldVersion < 5) {
            createChatTable(db);
        }
    }

    private void createSoundsAndVideosTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SOUNDS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SOUND_TITLE + " TEXT NOT NULL, "
                + COLUMN_SOUND_ICON + " TEXT NOT NULL, "
                + COLUMN_SOUND_FILE + " TEXT NOT NULL, "
                + COLUMN_SOUND_TYPE + " TEXT NOT NULL, "
                + COLUMN_SOUND_ORDER + " INTEGER NOT NULL DEFAULT 0"
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_VIDEOS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_VIDEO_TITLE + " TEXT NOT NULL, "
                + COLUMN_VIDEO_SUBTITLE + " TEXT, "
                + COLUMN_VIDEO_CATEGORY + " TEXT NOT NULL, "
                + COLUMN_VIDEO_THUMBNAIL + " TEXT, "
                + COLUMN_VIDEO_BG_COLOR + " TEXT NOT NULL, "
                + COLUMN_VIDEO_FILE + " TEXT NOT NULL, "
                + COLUMN_VIDEO_DURATION + " TEXT"
                + ")");
    }

    private void seedSoundsAndVideosData(SQLiteDatabase db) {
        insertSound(db, "غابة", "ic_forest", "forest_sound", "box", 1);
        insertSound(db, "شاطئ", "ic_beach", "beach_sound", "box", 2);
        insertSound(db, "جبال", "ic_mountain", "mountain_sound", "box", 3);
        insertSound(db, "مطر", "ic_rain", "rain_sound", "box", 4);

        insertSound(db, "ريح", "ic_wind", "wind_sound", "circle", 5);
        insertSound(db, "ماء", "ic_water", "water_sound", "circle", 6);
        insertSound(db, "طيور", "ic_birds", "birds_sound", "circle", 7);
        insertSound(db, "فيل النوم", "ic_elephant", "elephant_sound", "circle", 8);
        insertSound(db, "وقت طويل", "ic_clock", "long_time_sound", "circle", 9);
        insertSound(db, "شمس", "ic_sun", "sun_sound", "circle", 10);

        insertVideo(db, "قصة وقت النوم", null, "نوم", "thumb_sleep", "#8E7CC3", "sleep_story_video", "5:12");
        insertVideo(db, "كيف أحب نفساتي", null, "مشاعر", "thumb_feelings", "#F2C94C", "feelings_video", "4:30");
        insertVideo(db, "أصدقائي وأنا", "قصة عن الصداقة الحلوة", "صداقة", "thumb_friends", "#F49AC2", "friends_video", "4:26");
        insertVideo(db, "تنمية المشاعر", null, "مشاعر", "thumb_emotions", "#4DD0C4", "emotions_video", "3:45");
        insertVideo(db, "أحلام جميلة", null, "نوم", "thumb_dreams", "#5DADE2", "dreams_video", "6:00");
        insertVideo(db, "أنا شجاع", null, "لعبة", "thumb_brave", "#F0932B", "brave_video", "4:00");
    }

    private void insertSound(SQLiteDatabase db, String title, String icon, String file, String type, int order) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SOUND_TITLE, title);
        values.put(COLUMN_SOUND_ICON, icon);
        values.put(COLUMN_SOUND_FILE, file);
        values.put(COLUMN_SOUND_TYPE, type);
        values.put(COLUMN_SOUND_ORDER, order);
        db.insert(TABLE_SOUNDS, null, values);
    }

    private void insertVideo(SQLiteDatabase db, String title, String subtitle, String category,
                             String thumbnail, String bgColor, String file, String duration) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_VIDEO_TITLE, title);
        values.put(COLUMN_VIDEO_SUBTITLE, subtitle);
        values.put(COLUMN_VIDEO_CATEGORY, category);
        values.put(COLUMN_VIDEO_THUMBNAIL, thumbnail);
        values.put(COLUMN_VIDEO_BG_COLOR, bgColor);
        values.put(COLUMN_VIDEO_FILE, file);
        values.put(COLUMN_VIDEO_DURATION, duration);
        db.insert(TABLE_VIDEOS, null, values);
    }

    public List<SoundItem> getBoxSounds() {
        return getSoundsByType("box");
    }

    public List<SoundItem> getCircleSounds() {
        return getSoundsByType("circle");
    }

    public long addChatMessage(long childId, String messageText, boolean isUser) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHILD_ID, childId);
        values.put(COLUMN_MESSAGE_TEXT, messageText);
        values.put(COLUMN_IS_USER, isUser ? 1 : 0);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        return getWritableDatabase(DATABASE_PASSPHRASE).insert(TABLE_CHAT, null, values);
    }

    public List<ChatMessage> getChatHistory(long childId) {
        List<ChatMessage> messages = new ArrayList<>();
        String selection = COLUMN_CHILD_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(childId)};

        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).query(
                TABLE_CHAT,
                new String[]{COLUMN_MESSAGE_TEXT, COLUMN_IS_USER},
                selection,
                selectionArgs,
                null,
                null,
                COLUMN_TIMESTAMP + " ASC")) {
            while (cursor.moveToNext()) {
                String text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TEXT));
                boolean isUser = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER)) == 1;
                messages.add(new ChatMessage(text, isUser));
            }
        }
        return messages;
    }

    private List<SoundItem> getSoundsByType(String type) {
        List<SoundItem> list = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).query(
                TABLE_SOUNDS,
                new String[]{COLUMN_ID, COLUMN_SOUND_TITLE, COLUMN_SOUND_ICON, COLUMN_SOUND_FILE},
                COLUMN_SOUND_TYPE + " = ?",
                new String[]{type},
                null, null,
                COLUMN_SOUND_ORDER + " ASC")) {
            while (cursor.moveToNext()) {
                list.add(new SoundItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOUND_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOUND_ICON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOUND_FILE))));
            }
        }
        return list;
    }

    public List<VideoItem> getAllVideos() {
        return queryVideos(null, null);
    }

    public List<VideoItem> getVideosByCategory(String category) {
        return queryVideos(COLUMN_VIDEO_CATEGORY + " = ?", new String[]{category});
    }

    private List<VideoItem> queryVideos(String selection, String[] selectionArgs) {
        List<VideoItem> list = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).query(
                TABLE_VIDEOS,
                new String[]{COLUMN_ID, COLUMN_VIDEO_TITLE, COLUMN_VIDEO_SUBTITLE, COLUMN_VIDEO_CATEGORY,
                        COLUMN_VIDEO_THUMBNAIL, COLUMN_VIDEO_BG_COLOR, COLUMN_VIDEO_FILE, COLUMN_VIDEO_DURATION},
                selection, selectionArgs, null, null, COLUMN_ID + " ASC")) {
            while (cursor.moveToNext()) {
                list.add(new VideoItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_SUBTITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_THUMBNAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_BG_COLOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_FILE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DURATION))));
            }
        }
        return list;
    }

    public void migrateFromSharedPreferencesIfNeeded(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(OLD_PREFS_NAME, Context.MODE_PRIVATE);
        String savedProfiles = prefs.getString(OLD_KEY_PROFILES, null);
        if (savedProfiles == null || getProfilesCount() > 0) {
            return;
        }

        try {
            JSONArray profiles = new JSONArray(savedProfiles);
            for (int i = 0; i < profiles.length(); i++) {
                JSONObject profile = profiles.getJSONObject(i);
                addProfile(
                        profile.getString("name"),
                        profile.getInt("age"),
                        profile.optString("gender", "غير محدد"),
                        profile.optString("avatar", "🦊"));
            }
            prefs.edit().remove(OLD_KEY_PROFILES).apply();
        } catch (JSONException ignored) {
            prefs.edit().remove(OLD_KEY_PROFILES).apply();
        }
    }

    public long addProfile(String name, int age, String gender, String avatar) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_AVATAR, avatar);
        values.put(COLUMN_POINTS, 0);
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());
        return getWritableDatabase(DATABASE_PASSPHRASE).insert(TABLE_PROFILES, null, values);
    }

    public List<ChildProfile> getProfiles() {
        List<ChildProfile> profiles = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).query(
                TABLE_PROFILES,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_AGE, COLUMN_GENDER, COLUMN_AVATAR},
                null, null, null, null,
                COLUMN_CREATED_AT + " ASC")) {
            while (cursor.moveToNext()) {
                profiles.add(new ChildProfile(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR))));
            }
        }
        return profiles;
    }

    public long addBehaviorEvent(long childId, String eventType, String eventValue, String notes, long occurredAt) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHILD_ID, childId);
        values.put(COLUMN_EVENT_TYPE, eventType);
        values.put(COLUMN_EVENT_VALUE, eventValue);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_OCCURRED_AT, occurredAt);
        return getWritableDatabase(DATABASE_PASSPHRASE).insert(TABLE_EVENTS, null, values);
    }

    public long addBotMessage(long childId, String text, String mood, long createdAt) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHILD_ID, childId);
        values.put(COLUMN_TEXT, text);
        values.put(COLUMN_MOOD, mood);
        values.put(COLUMN_CREATED_AT, createdAt);
        return getWritableDatabase(DATABASE_PASSPHRASE).insert(TABLE_BOT_MESSAGES, null, values);
    }

    public List<BotMessage> getBotMessages(long childId) {
        List<BotMessage> messages = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).query(
                TABLE_BOT_MESSAGES,
                new String[]{COLUMN_ID, COLUMN_TEXT, COLUMN_MOOD, COLUMN_CREATED_AT},
                COLUMN_CHILD_ID + " = ?",
                new String[]{String.valueOf(childId)},
                null, null,
                COLUMN_CREATED_AT + " DESC")) {
            while (cursor.moveToNext()) {
                messages.add(new BotMessage(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOOD)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))));
            }
        }
        return messages;
    }

    public long addCompletedEvent(long childId, String eventType) {
        return addBehaviorEvent(childId, eventType, "COMPLETED", "تم إنجاز التحدي اليومي", System.currentTimeMillis());
    }

    private int getProfilesCount() {
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).rawQuery("SELECT COUNT(*) FROM " + TABLE_PROFILES, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public boolean hasCompletedEventToday(long childId, String eventType) {
        SQLiteDatabase db = getReadableDatabase(DATABASE_PASSPHRASE);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();

        String selection = COLUMN_CHILD_ID + " = ? AND " + COLUMN_EVENT_TYPE + " = ? AND " + COLUMN_OCCURRED_AT + " >= ?";
        String[] selectionArgs = new String[]{String.valueOf(childId), eventType, String.valueOf(startOfDay)};

        try (Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_ID}, selection, selectionArgs, null, null, null)) {
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void recordEvent(long childId, String eventType) {
        SQLiteDatabase db = this.getWritableDatabase(DATABASE_PASSPHRASE);
        ContentValues values = new ContentValues();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        values.put("child_id", childId);
        values.put("event_type", eventType);
        values.put("event_date", today);

        db.insertWithOnConflict("child_events", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void updateChildPoints(long childId, int newPoints) {
        SQLiteDatabase db = this.getWritableDatabase(DATABASE_PASSPHRASE);
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINTS, newPoints);
        db.update(TABLE_PROFILES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(childId)});
    }

    public void addStar(long childId) {
        SQLiteDatabase db = this.getWritableDatabase(DATABASE_PASSPHRASE);
        db.execSQL("UPDATE " + TABLE_PROFILES + " SET " + COLUMN_POINTS + " = " + COLUMN_POINTS + " + 1 WHERE " + COLUMN_ID + " = ?", new Object[]{childId});
    }

    public List<ChildProfile> getProfilesSortedByStars() {
        List<ChildProfile> list = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).query(
                TABLE_PROFILES,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_AGE, COLUMN_GENDER, COLUMN_AVATAR},
                null, null, null, null,
                COLUMN_POINTS + " DESC, " + COLUMN_CREATED_AT + " ASC")) {
            while (cursor.moveToNext()) {
                list.add(new ChildProfile(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR))));
            }
        } catch (Exception e) {
            return getProfiles();
        }
        return list;
    }

    public ChildProfile getProfileById(long childId) {
        ChildProfile profile = null;
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).rawQuery(
                "SELECT * FROM " + TABLE_PROFILES + " WHERE " + COLUMN_ID + " = ?",
                new String[]{String.valueOf(childId)})) {
            if (cursor.moveToFirst()) {
                profile = new ChildProfile(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR))
                );
            }
        }
        return profile;
    }

    public int getCompletedExercisesCount(long childId) {
        int count = 0;
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).rawQuery(
                "SELECT COUNT(*) FROM child_activities WHERE child_id = ? AND status = 'completed'",
                new String[]{String.valueOf(childId)})) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
        } catch (Exception ignored) {}
        return count;
    }

    public int getSessionsCount(long childId) {
        int count = 0;
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).rawQuery(
                "SELECT COUNT(DISTINCT session_id) FROM child_activities WHERE child_id = ?",
                new String[]{String.valueOf(childId)})) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
        } catch (Exception ignored) {}
        return count;
    }

    public int calculateInactiveDays(long childId) {
        return 2;
    }

    public ChildDetailFragment.Range getMoodChartData(long childId, String rangeType) {
        if ("day".equals(rangeType)) {
            return new ChildDetailFragment.Range(new float[]{4.0f, 3.5f, 4.2f, 4.8f, 4.1f}, new String[]{"٦ص", "٩ص", "١٢م", "٣م", "٦م"});
        } else if ("month".equals(rangeType)) {
            return new ChildDetailFragment.Range(new float[]{3.8f, 4.0f, 4.2f, 4.5f}, new String[]{"أسبوع ١", "أسبوع ٢", "أسبوع ٣", "أسبوع ٤"});
        }
        return new ChildDetailFragment.Range(new float[]{3.2f, 4.5f, 2.8f, 4.0f, 4.6f, 3.9f, 4.4f}, new String[]{"ح", "ن", "ث", "ر", "خ", "ج", "س"});
    }

    public List<ChildFeature> getTopFeaturesUsed(long childId) {
        List<ChildFeature> list = new ArrayList<>();
        list.add(new ChildFeature("شجرة التعافي", 8, android.R.drawable.ic_menu_agenda));
        list.add(new ChildFeature("نفخ الفراشات", 5, android.R.drawable.ic_menu_compass));
        return list;
    }

    public List<ChildHistoryEntry> getChildActivityHistory(long childId) {
        List<ChildHistoryEntry> list = new ArrayList<>();
        list.add(new ChildHistoryEntry("أكمل تمرين التنفس العميق", "اليوم ٤:٣٠ م"));
        list.add(new ChildHistoryEntry("سجل حالة مزاجية (سعيد)", "أمس ٦:١٥ م"));
        return list;
    }

    public ChildAlert getLatestChildAlert(long childId) {
        return null;
    }

    public List<String> generateRecommendationsForChild(long childId) {
        List<String> list = new ArrayList<>();
        list.add("تشجيع الطفل على استكمال تمرين نفخ الفراشات قبل النوم.");
        list.add("المحافظة على متابعة الجدول الأسبوعي بانتظام.");
        return list;
    }
    public int getChildrenCount() {
        int count = 0;
        try (Cursor cursor = getReadableDatabase(DATABASE_PASSPHRASE).rawQuery("SELECT COUNT(*) FROM " + TABLE_PROFILES, null)) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

}