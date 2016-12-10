package edu.csumb.scd.otterlibrary;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

/**
 * Created by jsullivan on 12/9/16.
 */

public class LibraryHold {
    private int resId;
    private double fee;
    private String heldBy;
    private String title;
    private Calendar pickup;
    private Calendar dropoff;

    private LibraryHold() {
        this.pickup = Calendar.getInstance();
        this.dropoff = Calendar.getInstance();
    }

    public LibraryHold(String heldBy, String title, long pickup, long dropoff, double fee) {
        this();
        this.heldBy = heldBy;
        this.title = title;
        this.fee = fee;

        this.pickup.setTimeInMillis(pickup);
        this.dropoff.setTimeInMillis(dropoff);
    }

    public ContentValues prepareHold() {
        ContentValues ret = new ContentValues();

        ret.put(LibraryContract.HoldEntry.COLUMN_NAME_HELD_BY, heldBy);
        ret.put(LibraryContract.HoldEntry.COLUMN_NAME_TITLE, title);
        ret.put(LibraryContract.HoldEntry.COLUMN_NAME_PICKUP, pickup.getTimeInMillis());
        ret.put(LibraryContract.HoldEntry.COLUMN_NAME_DROPOFF, dropoff.getTimeInMillis());
        ret.put(LibraryContract.HoldEntry.COLUMN_NAME_FEE, fee);

        return ret;
    }

    public static LibraryHold getHoldFromCursor(Cursor c) {
        LibraryHold ret = new LibraryHold();

        ret.resId = c.getInt(c.getColumnIndex(LibraryContract.HoldEntry._ID));
        ret.heldBy = c.getString(c.getColumnIndex(LibraryContract.HoldEntry.COLUMN_NAME_HELD_BY));
        ret.title = c.getString(c.getColumnIndex(LibraryContract.HoldEntry.COLUMN_NAME_TITLE));
        ret.pickup.setTimeInMillis(c.getLong(c.getColumnIndex(LibraryContract.HoldEntry.COLUMN_NAME_PICKUP)));
        ret.dropoff.setTimeInMillis(c.getLong(c.getColumnIndex(LibraryContract.HoldEntry.COLUMN_NAME_DROPOFF)));
        ret.fee = c.getDouble(c.getColumnIndex(LibraryContract.HoldEntry.COLUMN_NAME_FEE));

        return ret;
    }

    public static boolean checkHolds(String title, long pickup, long dropoff, SQLiteDatabase db) {
        String[] holdProjection = {
                LibraryContract.HoldEntry._ID,
                LibraryContract.HoldEntry.COLUMN_NAME_HELD_BY,
                LibraryContract.HoldEntry.COLUMN_NAME_TITLE,
                LibraryContract.HoldEntry.COLUMN_NAME_PICKUP,
                LibraryContract.HoldEntry.COLUMN_NAME_DROPOFF
        };

        String select = LibraryContract.HoldEntry.COLUMN_NAME_TITLE + " = ?";
        String[] args = { title };

        String sort = LibraryContract.HoldEntry._ID + " DESC";

        Cursor c;
        try {
            c = db.query(
                    LibraryContract.HoldEntry.TABLE_NAME,
                    holdProjection,
                    select,
                    args,
                    null,
                    null,
                    sort
            );

            c.moveToFirst();

            do {
                long pickupTime = c.getLong(c.getColumnIndex(LibraryContract.HoldEntry.COLUMN_NAME_PICKUP));
                long dropoffTime = c.getLong(c.getColumnIndex(LibraryContract.HoldEntry.COLUMN_NAME_DROPOFF));

                if(!((pickup < pickupTime && dropoff < pickupTime) || (pickup > dropoffTime && dropoff > dropoffTime))) {
                    return false;
                }
            } while(c.moveToNext() == true);

            return true;
        }
        catch(Exception e)
        {
            return true;
        }
    }

    public String getHeldBy() { return heldBy; }
    public String getTitle() { return title; }
    public long getPickup() { return pickup.getTimeInMillis(); }
    public long getDropoff() { return dropoff.getTimeInMillis(); }
    public int getResId() { return resId; }
    public double getFee() { return fee; }
}
