package com.example.graduationproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.graduationproject.models.FavoriteStory;

import java.util.ArrayList;
import java.util.List;

public class FavoriteStoryDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "favorite_stories.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "favorite_stories";
    private static final String COL_ID = "_id";
    private static final String COL_CATEGORY = "category";
    private static final String COL_TITLE = "title";
    private static final String COL_STORY_TEXT = "story_text";
    private static final String COL_CREATED_AT = "created_at";

    public FavoriteStoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_STORY_TEXT + " TEXT NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /** يضيف قصة للمفضلة ويرجع الـ id تبعها */
    public long addFavorite(String category, String title, String storyText) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CATEGORY, category);
        values.put(COL_TITLE, title);
        values.put(COL_STORY_TEXT, storyText);
        values.put(COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_NAME, null, values);
    }

    public void removeFavoriteById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /** يتحقق إذا نفس القصة (بنفس النص والتصنيف) موجودة بالمفضلة، يرجع الـ id أو -1 */
    public long findFavoriteId(String category, String storyText) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_ID},
                COL_CATEGORY + " = ? AND " + COL_STORY_TEXT + " = ?",
                new String[]{category, storyText}, null, null, null);

        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        }
        cursor.close();
        return id;
    }

    public List<FavoriteStory> getAllFavorites() {
        List<FavoriteStory> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
                COL_CREATED_AT + " DESC");

        while (cursor.moveToNext()) {
            result.add(new FavoriteStory(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STORY_TEXT)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
            ));
        }
        cursor.close();
        return result;
    }
}