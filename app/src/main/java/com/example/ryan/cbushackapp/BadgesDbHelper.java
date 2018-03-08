package com.example.ryan.cbushackapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BadgesDbHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BadgeDatabase.db";

    // SQLite command to create table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BadgesContract.BadgeEntry.TABLE_NAME + " (" +
                    BadgesContract.BadgeEntry._ID + " INTEGER PRIMARY KEY," +
                    BadgesContract.BadgeEntry.COLUMN_NAME_TITLE + " TEXT," +
                    BadgesContract.BadgeEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    BadgesContract.BadgeEntry.COLUMN_NAME_LAT + " TEXT," +
                    BadgesContract.BadgeEntry.COLUMN_NAME_LNG + " TEXT," +
                    BadgesContract.BadgeEntry.COLUMN_NAME_STATUS + " TEXT)";

    // SQLite command to delete the table
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BadgesContract.BadgeEntry.TABLE_NAME;

    // Data for badges
    private String[][] BADGE_INFO = {
            {"Columbus College of Arts and Design", "The most animated place around!", "39.965001", "-82.989781"},
            {"Columbus Metropolitan Library", "The Paper Forest", "39.9612", "-82.9895"},
            {"Columbus Museum of Art", "Almost as good as Schokko-late!", "39.96416667", "-82.98777778"},
            {"Columbus State", "It gets technical here...", "39.969191", "-82.98719"},
            {"German Village Music Haus", "Das Haus der Musik", "39.9561", "-82.9894"},
            {"Kelton House Museum & Garden", "A blast to the past", "39.9608", "-82.9843"},
            {"Plaza Deli", "Great places like this are Medium rare!", "39.96254179152478", "-82.98774003982544"},
            {"The Hills Market Downtown", "It wouldn't hurt to plow into these fresh goods...", "39.9654", "-82.9917"},
            {"Topiary Park", "‘A Sun-daisy’s Afternoon on the Isle of La Grand Jatte’", "39.961016", "-82.987772"},
            {"Roosevelt Coffeehouse", "Good Coffee for Good", "39.96615", "-82.993109"},
            {"Soluna Cafe Bakery", "This place bierocks…!", "39.9599", "-82.9915"},
            {"Einstein Bros. Bagels", "These Bagels are relatively good.", "39.96544043349975", "-82.99054563045502"},
            {"Franklin University", "♫ Makes it possible! ♫", "39.958166870802295", "-82.99037396907806"},
            {"Carioti Jewelers", "You won’t want topaz this place up!", "39.967167225623356", "-82.9909747838974"}
    };

    public BadgesDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        // Creates table for badges
        db.execSQL(SQL_CREATE_ENTRIES);
        // Adds badges to database
        for (int i = 0; i<BADGE_INFO.length; i++)
        {
            addBadge(db, i+1, BADGE_INFO[i][0], BADGE_INFO[i][1], BADGE_INFO[i][2], BADGE_INFO[i][3], "0");
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Resets the database when upgrading
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Resets the database when downgrading
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addBadge(SQLiteDatabase db, Integer id, String title, String description, String lat, String lng, String status)
    {
        // Adds info into the table
        ContentValues values = new ContentValues();
        values.put(BadgesContract.BadgeEntry._ID, id);
        values.put(BadgesContract.BadgeEntry.COLUMN_NAME_TITLE, title);
        values.put(BadgesContract.BadgeEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(BadgesContract.BadgeEntry.COLUMN_NAME_LAT, lat);
        values.put(BadgesContract.BadgeEntry.COLUMN_NAME_LNG, lng);
        values.put(BadgesContract.BadgeEntry.COLUMN_NAME_STATUS, status);
        db.insert(BadgesContract.BadgeEntry.TABLE_NAME, null, values);
    }

}