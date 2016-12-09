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
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Library.db";

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LibraryContract.SQL_CREATE_ENTRIES);
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
        db.execSQL(LibraryContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
