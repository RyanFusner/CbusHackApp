package com.example.ryan.cbushackapp;

import android.provider.BaseColumns;

public final class BadgesContract
{
    private BadgesContract() {}

    public static class BadgeEntry implements BaseColumns
    {
        // Constants for table title and columns
        public static final String TABLE_NAME = "badge";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
    }
}
