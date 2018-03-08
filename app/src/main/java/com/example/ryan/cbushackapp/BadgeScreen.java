package com.example.ryan.cbushackapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BadgeScreen extends AppCompatActivity
{
    public Context context;
    private ListView listView;
    private TextView badgeCounter;
    private Integer badgesOwned;
    List<Map<String,String>> allBadges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_screen);
        context = this;
        getBadges();
        // Displays amount of badges owned
        badgeCounter =(TextView) findViewById(R.id.BadgeCounter);
        badgeCounter.setText(badgesOwned + "/" + allBadges.size() + " Badges");
    }

    public void getBadges()
    {
        // Gets badge info from the database
        BadgesDbHelper mDbHelper = new BadgesDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BadgesContract.BadgeEntry._ID,
                BadgesContract.BadgeEntry.COLUMN_NAME_TITLE,
                BadgesContract.BadgeEntry.COLUMN_NAME_DESCRIPTION,
                BadgesContract.BadgeEntry.COLUMN_NAME_STATUS
        };

        Cursor cursor = db.query(
                BadgesContract.BadgeEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                                  // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                     // The sort order
        );

        allBadges = new ArrayList<>();
        badgesOwned = 0;

        while(cursor.moveToNext())
        {
            Map<String, String> dbBadgeEntry = new HashMap<String, String>();
            String title = cursor.getString(
                    cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_TITLE)
            );
            String description = cursor.getString(
                    cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_DESCRIPTION)
            );
            String status = cursor.getString(
                    cursor.getColumnIndex(BadgesContract.BadgeEntry.COLUMN_NAME_STATUS)
            );

            dbBadgeEntry.put("title", title);
            dbBadgeEntry.put("description", description);
            dbBadgeEntry.put("status", status);
            allBadges.add(dbBadgeEntry);
            if(Objects.equals(status, "1"))
            {
                badgesOwned++;
            }
        }
        cursor.close();

        // Displays the badge info in a list view using an adapter
        ListAdapter adapter = new ListAdapter(this, allBadges);
        listView = (ListView) findViewById(R.id.BadgeList);
        listView.setAdapter(adapter);
        mDbHelper.close();
    }
}
