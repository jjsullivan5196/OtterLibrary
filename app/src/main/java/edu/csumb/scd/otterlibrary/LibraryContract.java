package edu.csumb.scd.otterlibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.Telephony;
import android.util.Log;

/**
 * Created by jsullivan on 12/6/16.
 */

public final class LibraryContract {
    private LibraryContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    public static class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_ISBN = "isbn";
        public static final String COLUMN_NAME_FEE = "fee";
    }

    public static class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "logs";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PICKUP = "pickup";
        public static final String COLUMN_NAME_DROPOFF = "dropoff";
        public static final String COLUMN_NAME_FEE = "fee";

        public static final String COLUMN_TYPE_ACCOUNT = "account";
        public static final String COLUMN_TYPE_HOLD = "hold";
        public static final String COLUMN_TYPE_CANCEL = "cancel";
    }

    public static class HoldEntry implements BaseColumns {
        public static final String TABLE_NAME = "holds";
        public static final String COLUMN_NAME_HELD_BY = "held_by";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PICKUP = "pickup";
        public static final String COLUMN_NAME_DROPOFF = "dropoff";
        public static final String COLUMN_NAME_FEE = "fee";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String[] SQL_CREATE_ENTRIES = {
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserEntry.COLUMN_NAME_USERNAME + TEXT_TYPE + " NOT NULL UNIQUE" + COMMA_SEP +
                    UserEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + " NOT NULL" + " )",
            "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                    BookEntry._ID + " INTEGER PRIMARY KEY," +
                    BookEntry.COLUMN_NAME_TITLE + TEXT_TYPE + " NOT NULL UNIQUE" + COMMA_SEP +
                    BookEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    BookEntry.COLUMN_NAME_ISBN + TEXT_TYPE + " NOT NULL UNIQUE" + COMMA_SEP +
                    BookEntry.COLUMN_NAME_FEE + " REAL NOT NULL" + " )",
            "CREATE TABLE " + LogEntry.TABLE_NAME + " (" +
                    LogEntry._ID + " INTEGER PRIMARY KEY," +
                    LogEntry.COLUMN_NAME_TYPE + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    LogEntry.COLUMN_NAME_TIME + " INTEGER NOT NULL" + COMMA_SEP +
                    LogEntry.COLUMN_NAME_NAME + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    LogEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    LogEntry.COLUMN_NAME_PICKUP + " INTEGER" + COMMA_SEP +
                    LogEntry.COLUMN_NAME_DROPOFF + " INTEGER" + COMMA_SEP +
                    LogEntry.COLUMN_NAME_FEE + " REAL" + " )",
            "CREATE TABLE " + HoldEntry.TABLE_NAME + " (" +
                    HoldEntry._ID + " INTEGER PRIMARY KEY," +
                    HoldEntry.COLUMN_NAME_HELD_BY + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    HoldEntry.COLUMN_NAME_TITLE + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    HoldEntry.COLUMN_NAME_PICKUP + " INTEGER NOT NULL" + COMMA_SEP +
                    HoldEntry.COLUMN_NAME_DROPOFF + " INTEGER NOT NULL" + COMMA_SEP +
                    HoldEntry.COLUMN_NAME_FEE + " REAL NOT NULL" + " )"
    };

    public static final String[] SQL_DELETE_ENTRIES = {
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME,
            "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME,
            "DROP TABLE IF EXISTS " + LogEntry.TABLE_NAME,
            "DROP TABLE IF EXISTS " + HoldEntry.TABLE_NAME
    };
}
