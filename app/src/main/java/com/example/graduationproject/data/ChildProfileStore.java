package com.example.graduationproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.graduationproject.models.ChildProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChildProfileStore extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "children_wellbeing.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PROFILES = "child_profiles";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_CREATED_AT = "created_at";

    private static final String TABLE_EVENTS = "child_behavior_events";
    private static final String COLUMN_CHILD_ID = "child_id";
    private static final String COLUMN_EVENT_TYPE = "event_type";
    private static final String COLUMN_EVENT_VALUE = "event_value";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_OCCURRED_AT = "occurred_at";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
                        profile.optString("avatar", "🦊"));
            }
            prefs.edit().remove(OLD_KEY_PROFILES).apply();
        } catch (JSONException ignored) {
            prefs.edit().remove(OLD_KEY_PROFILES).apply();
        }
    }

    public long addProfile(String name, int age, String avatar) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_AVATAR, avatar);
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE_PROFILES, null, values);
    }

    public List<ChildProfile> getProfiles() {
        List<ChildProfile> profiles = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_PROFILES,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_AGE, COLUMN_AVATAR},
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

    private int getProfilesCount() {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_PROFILES, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }
}
