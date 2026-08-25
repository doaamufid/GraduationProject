package com.example.graduationproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.graduationproject.Kids.ChatMessage;
import com.example.graduationproject.models.ChildProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChildProfileStore extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "children_wellbeing.db";
    private static final int DATABASE_VERSION = 5; // تم رفع الإصدار لـ 5 لإضافة جدول الشات

    private static final String TABLE_PROFILES = "child_profiles";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_CREATED_AT = "created_at";

    private static final String TABLE_EVENTS = "child_behavior_events";
    private static final String COLUMN_CHILD_ID = "child_id";
    private static final String COLUMN_EVENT_TYPE = "event_type";
    private static final String COLUMN_EVENT_VALUE = "event_value";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_OCCURRED_AT = "occurred_at";

    // ⭐ جدول رسائل المحادثة الجديد
    private static final String TABLE_CHAT = "chat_messages";
    private static final String COLUMN_MESSAGE_TEXT = "message_text";
    private static final String COLUMN_IS_USER = "is_user";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String OLD_PREFS_NAME = "child_profiles_prefs";
    private static final String OLD_KEY_PROFILES = "child_profiles";

    public ChildProfileStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

        // ⭐ إنشاء جدول المحادثات
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
        }
        if (oldVersion < 5) {
            createChatTable(db);
        }
    }

    // --- ⭐ دوال حفظ واسترجاع المحادثات من SQLite ---

    public long addChatMessage(long childId, String messageText, boolean isUser) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHILD_ID, childId);
        values.put(COLUMN_MESSAGE_TEXT, messageText);
        values.put(COLUMN_IS_USER, isUser ? 1 : 0);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE_CHAT, null, values);
    }

    public List<ChatMessage> getChatHistory(long childId) {
        List<ChatMessage> messages = new ArrayList<>();
        String selection = COLUMN_CHILD_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(childId)};

        try (Cursor cursor = getReadableDatabase().query(
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
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE_PROFILES, null, values);
    }

    public List<ChildProfile> getProfiles() {
        List<ChildProfile> profiles = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_PROFILES,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_AGE, COLUMN_GENDER, COLUMN_AVATAR},
                null,
                null,
                null,
                null,
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
        return getWritableDatabase().insert(TABLE_EVENTS, null, values);
    }

    public long addCompletedEvent(long childId, String eventType) {
        return addBehaviorEvent(childId, eventType, "COMPLETED", "تم إنجاز التحدي اليومي", System.currentTimeMillis());
    }

    private int getProfilesCount() {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_PROFILES, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public boolean hasCompletedEventToday(long childId, String eventType) {
        SQLiteDatabase db = getReadableDatabase();

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
}