package edu.csumb.scd.otterlibrary;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jsullivan on 12/6/16.
 */

public final class LibraryUser {
    private String name;
    private String password;

    private static final String MATCH_SYM = "^(?=(.*[#$!@])+).*$";
    private static final String MATCH_NUM = "^(?=(.*\\d)+).*$";
    private static final String MATCH_LET = "^(?=(.*[a-zA-Z]){3,}).*$";
    private static final String MATCH_ALL = "^(?=(.*[#$!@])+)(?=(.*\\d)+)(?=(.*[a-zA-Z]){3,}).*$";

    public LibraryUser(String name, String password) {
        if(!(password.matches(MATCH_ALL) && name.matches(MATCH_ALL))) {
            StringBuilder error = new StringBuilder();
            if(!name.matches(MATCH_ALL)) {
                if (!name.matches(MATCH_SYM)) {
                    error.append("\nUsername needs one special symbol: #$!@");
                }
                if (!name.matches(MATCH_NUM)) {
                    error.append("\nUsername needs one digit");
                }
                if (!name.matches(MATCH_LET)) {
                    error.append("\nUsername needs 3 letters");
                }
            }

            if(!password.matches(MATCH_ALL)) {
                if (!password.matches(MATCH_SYM)) {
                    error.append("\nPassword needs one special symbol: #$!@");
                }
                if (!password.matches(MATCH_NUM)) {
                    error.append("\nPassword needs one digit");
                }
                if (!password.matches(MATCH_LET)) {
                    error.append("\nPassword needs 3 letters");
                }
            }

            throw new RuntimeException(error.toString());
        }

        this.name = name;
        this.password = password;
    }

    public static LibraryUser getUserFromDb(String name, String password, SQLiteDatabase db) {
        String[] userProjection = {
                LibraryContract.UserEntry.COLUMN_NAME_USERNAME,
                LibraryContract.UserEntry.COLUMN_NAME_PASSWORD,
        };

        String select = LibraryContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] args = { name };

        String sort = LibraryContract.UserEntry.COLUMN_NAME_USERNAME + " DESC";

        Cursor c;
        String dbName = "";
        String dbPass = "";
        try {
            c = db.query(
                    LibraryContract.UserEntry.TABLE_NAME,
                    userProjection,
                    select,
                    args,
                    null,
                    null,
                    sort
            );

            c.moveToFirst();
            dbName = c.getString(c.getColumnIndex(LibraryContract.UserEntry.COLUMN_NAME_USERNAME));
            dbPass = c.getString(c.getColumnIndex(LibraryContract.UserEntry.COLUMN_NAME_PASSWORD));
        }
        catch(Exception e)
        {
            throw new RuntimeException("User not found");
        }

        if(name.equals(dbName) && password.equals(dbPass)) {
            return new LibraryUser(name, password);
        }
        else {
            throw new RuntimeException("Incorrect Password");
        }
    }

    public ContentValues prepareUser() {
        ContentValues prep = new ContentValues();

        prep.put(LibraryContract.UserEntry.COLUMN_NAME_USERNAME, name);
        prep.put(LibraryContract.UserEntry.COLUMN_NAME_PASSWORD, password);

        return prep;
    }

    @Override
    public String toString() {
        return name;
    }
}
