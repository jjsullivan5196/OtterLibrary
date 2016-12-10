package edu.csumb.scd.otterlibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by John Sullivan on 12/7/2016.
 */

public class LibraryDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Library.db";

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        for (String sqlStatement : LibraryContract.SQL_CREATE_ENTRIES) {
            db.execSQL(sqlStatement);
        }

        LibraryUser defaultUsers[] = new LibraryUser[4];
        LibraryBook defaultBooks[] = new LibraryBook[3];

        defaultUsers[0] = new LibraryUser("!admin2", "!admin2");
        defaultUsers[1] = new LibraryUser("a@lice5", "@csit100");
        defaultUsers[2] = new LibraryUser("$brian7", "123abc##");
        defaultUsers[3] = new LibraryUser("!chris12!", "CHRIS12!!");

        defaultBooks[0] = new LibraryBook("Hot Java", "S. Narayanan", "123-ABC-101", 0.05);
        defaultBooks[1] = new LibraryBook("Fun Java", "Y. Byun", "ABCDEF-09", 1.00);
        defaultBooks[2] = new LibraryBook("Algorithm for Java", "K. Alice", "CDE-777-123", 0.25);

        for(LibraryUser user : defaultUsers) {
            ContentValues prep = user.prepareUser();
            db.insert(LibraryContract.UserEntry.TABLE_NAME, null, prep);
        }

        for(LibraryBook book : defaultBooks) {
            ContentValues prep = book.prepareBook();
            db.insert(LibraryContract.BookEntry.TABLE_NAME, null, prep);
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        for (String sqlStatement : LibraryContract.SQL_DELETE_ENTRIES) {
            db.execSQL(sqlStatement);
        }
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public static ContentValues updateLog(String type, Long time, String name, String title, Long pickup, Long dropoff, Double fee) {
        ContentValues newLog = new ContentValues();

        newLog.put(LibraryContract.LogEntry.COLUMN_NAME_TYPE, type);
        newLog.put(LibraryContract.LogEntry.COLUMN_NAME_TIME, time);
        newLog.put(LibraryContract.LogEntry.COLUMN_NAME_NAME, name);
        newLog.put(LibraryContract.LogEntry.COLUMN_NAME_TITLE, title);
        newLog.put(LibraryContract.LogEntry.COLUMN_NAME_PICKUP, pickup);
        newLog.put(LibraryContract.LogEntry.COLUMN_NAME_DROPOFF, dropoff);
        newLog.put(LibraryContract.LogEntry.COLUMN_NAME_FEE, fee);

        return newLog;
    }
}
