package edu.csumb.scd.otterlibrary;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

/**
 * Created by jsullivan on 12/8/16.
 */

public class LibraryBook {
    private String title;
    private String author;
    private String isbn;
    private double fee;
    private int id;

    public LibraryBook(String title, String author, String isbn, double fee) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.fee = fee;
    }

    private LibraryBook(String title, String author, String isbn, double fee, int id) {
        this(title, author, isbn, fee);
        this.id = id;
    }

    public ContentValues prepareBook() {
        ContentValues prep = new ContentValues();

        prep.put(LibraryContract.BookEntry.COLUMN_NAME_TITLE, title);
        prep.put(LibraryContract.BookEntry.COLUMN_NAME_AUTHOR, author);
        prep.put(LibraryContract.BookEntry.COLUMN_NAME_ISBN, isbn);
        prep.put(LibraryContract.BookEntry.COLUMN_NAME_FEE, fee);

        return prep;
    }

    public static LibraryBook getBookFromDb(String title, SQLiteDatabase db) {
        String[] bookProjection = {
                LibraryContract.BookEntry.COLUMN_NAME_TITLE,
                LibraryContract.BookEntry.COLUMN_NAME_AUTHOR,
                LibraryContract.BookEntry.COLUMN_NAME_ISBN,
                LibraryContract.BookEntry.COLUMN_NAME_FEE
        };

        String select = LibraryContract.BookEntry.COLUMN_NAME_TITLE + " = ?";
        String[] args = { title };

        String sort = LibraryContract.UserEntry.COLUMN_NAME_USERNAME + " DESC";

        Cursor c;
        String dbTitle = "";
        String dbAuthor = "";
        String dbISBN = "";
        double dbFee = 0;
        int dbId = 0;
        try {
            c = db.query(
                    LibraryContract.BookEntry.TABLE_NAME,
                    bookProjection,
                    select,
                    args,
                    null,
                    null,
                    sort
            );

            c.moveToFirst();
            dbTitle = c.getString(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_TITLE));
            dbAuthor = c.getString(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_AUTHOR));
            dbISBN = c.getString(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_ISBN));
            dbFee = c.getDouble(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_FEE));
            dbId = c.getInt(c.getColumnIndex(LibraryContract.BookEntry._ID));
        }
        catch(Exception e)
        {
            throw new RuntimeException("Book not found");
        }

        return new LibraryBook(dbTitle, dbAuthor, dbISBN, dbFee);
    }

    public static LibraryBook getBookFromCursor(Cursor c) {
        String dbTitle = "";
        String dbAuthor = "";
        String dbISBN = "";
        double dbFee = 0;
        int dbId = 0;

        dbTitle = c.getString(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_TITLE));
        dbAuthor = c.getString(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_AUTHOR));
        dbISBN = c.getString(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_ISBN));
        dbFee = c.getDouble(c.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_FEE));
        dbId = c.getInt(c.getColumnIndex(LibraryContract.BookEntry._ID));

        return new LibraryBook(dbTitle, dbAuthor, dbISBN, dbFee, dbId);
    }

    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getAuthor() { return author; }
    public double getFee() { return fee; }
    public int getId() { return id; }

    @Override
    public String toString() {
        return String.format("%s by %s | ISBN: %S | Fee: $%.2f", title, author, isbn, fee);
    }
}
