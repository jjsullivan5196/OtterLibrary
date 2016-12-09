package edu.csumb.scd.otterlibrary;

import android.content.ContentValues;

import java.util.Calendar;

/**
 * Created by jsullivan on 12/8/16.
 */

public class LibraryBook {
    private String title;
    private String author;
    private String isbn;
    private String heldBy;
    private Calendar pickup;
    private Calendar dropoff;
    private double fee;
    private boolean hold;

    public LibraryBook(String title, String author, String isbn, double fee) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.fee = fee;
        this.hold = false;

        this.heldBy = null;
        this.pickup = this.dropoff = null;
    }

    public ContentValues prepareBook() {
        ContentValues prep = new ContentValues();

        prep.put(LibraryContract.BookEntry.COLUMN_NAME_TITLE, title);
        prep.put(LibraryContract.BookEntry.COLUMN_NAME_AUTHOR, author);
        prep.put(LibraryContract.BookEntry.COLUMN_NAME_ISBN, isbn);
        prep.put(LibraryContract.BookEntry.COLUMN_NAME_FEE, fee);
        prep.put(LibraryContract.BookEntry.COLUMN_NAME_HOLD, hold);

        if(heldBy != null) {
            prep.put(LibraryContract.BookEntry.COLUMN_NAME_HELD_BY, heldBy);
            prep.put(LibraryContract.BookEntry.COLUMN_NAME_PICKUP, pickup.getTimeInMillis());
            prep.put(LibraryContract.BookEntry.COLUMN_NAME_DROPOFF, dropoff.getTimeInMillis());
        }

        return prep;
    }

    public void setHold(String name, Calendar pickup, Calendar dropoff) {
        this.heldBy = name;
        this.pickup = pickup;
        this.dropoff = dropoff;
    }

    public void unHold() {
        heldBy = null;
        pickup = dropoff = null;
    }

    @Override
    public String toString() {
        return String.format("%s by %s | ISBN: %S | Fee: $%.2f", title, author, isbn, fee);
    }
}
