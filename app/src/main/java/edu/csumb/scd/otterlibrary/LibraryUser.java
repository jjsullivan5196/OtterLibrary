package edu.csumb.scd.otterlibrary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jsullivan on 12/6/16.
 */

public class LibraryUser {
    private String name;
    private String password;
    private static final Pattern fieldPattern = Pattern.compile("(=?[!@#$])(=?[0-9])(=?[A-Za-z]{3,})");

    private LibraryUser(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public LibraryUser createUser(String name, String password) {
        boolean matchName = fieldPattern.matcher(name).matches();
        boolean matchPass = field
    }
}
