package edu.csumb.scd.otterlibrary;

import android.content.ContentValues;

/**
 * Created by jsullivan on 12/6/16.
 */

public class LibraryUser {
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
