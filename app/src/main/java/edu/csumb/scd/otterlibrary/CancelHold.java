package edu.csumb.scd.otterlibrary;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CancelHold extends ListActivity implements SimpleCursorAdapter.ViewBinder, MyAlertDialogFragment.FragmentAlertDialog {
    private SimpleCursorAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private LibraryDbHelper libraryHelper;
    private Context context;
    private String name;
    private SimpleDateFormat infoDate;
    private Cursor selection;
    private LibraryHold cancel;

    private static final String[] PROJECTION = {
            LibraryContract.HoldEntry._ID,
            LibraryContract.HoldEntry.COLUMN_NAME_TITLE,
            LibraryContract.HoldEntry.COLUMN_NAME_HELD_BY,
            LibraryContract.HoldEntry.COLUMN_NAME_FEE,
            LibraryContract.HoldEntry.COLUMN_NAME_PICKUP,
            LibraryContract.HoldEntry.COLUMN_NAME_DROPOFF
    };
    private static final String SELECTION = LibraryContract.HoldEntry.COLUMN_NAME_HELD_BY + " = ?";
    private static final String sortOrder = LibraryContract.HoldEntry.COLUMN_NAME_TITLE + " DESC";

    public static final String BOOK_SELECT_TITLE = "booktitle";
    public static final String BOOK_SELECT_AUTHOR = "bookauthor";
    public static final String BOOK_SELECT_FEE = "bookfee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_default);

        infoDate = new SimpleDateFormat("MMM dd yyyy hh:mm aa");

        Intent mInstance = getIntent();
        name = mInstance.getStringExtra(LoginActivity.PASS_USER);
        String[] select = { name };

        context = getApplicationContext();
        libraryHelper = new LibraryDbHelper(context);
        mDb = libraryHelper.getReadableDatabase();
        mCursor = mDb.query(
                LibraryContract.HoldEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                select,
                null,
                null,
                sortOrder
        );

        mAdapter = new SimpleCursorAdapter(
                this, // Context.
                R.layout.listitem_book,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {
                        LibraryContract.HoldEntry._ID,
                        LibraryContract.HoldEntry.COLUMN_NAME_TITLE,
                        LibraryContract.HoldEntry.COLUMN_NAME_FEE
                },
                new int[] {R.id.bookTitle, R.id.bookAuthor, R.id.bookPrice},
                0
        );
        mAdapter.setViewBinder(this);

        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        selection = (Cursor)getListView().getItemAtPosition(position);
        Intent loginIntent = getIntent();
        cancel = LibraryHold.getHoldFromCursor(selection);

        DialogFragment choice = MyAlertDialogFragment.newInstance(R.string.cancel_hold, "Are you sure?");
        choice.show(getFragmentManager(), "cancelHold");
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        TextView mText = (TextView)view;

        Calendar pickup = Calendar.getInstance();
        Calendar dropoff = Calendar.getInstance();

        LibraryHold c = LibraryHold.getHoldFromCursor(cursor);
        pickup.setTimeInMillis(c.getPickup());
        dropoff.setTimeInMillis(c.getDropoff());

        switch(mText.getId()) {
            case R.id.bookTitle:
                mText.setText(String.format("%s (ID: %d)", c.getTitle(), c.getResId()));
                return true;
            case R.id.bookAuthor:
                mText.setText(String.format("Pickup: %s\nDropoff: %s", infoDate.format(pickup.getTime()), infoDate.format(dropoff.getTime())));
                return true;
            case R.id.bookPrice:
                mText.setText(String.format("$%.2f", c.getFee()));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void doPositiveClick() {
        String title = cancel.getTitle();
        int rId = cancel.getResId();
        long pick = cancel.getPickup();
        long drop = cancel.getDropoff();
        SQLiteDatabase db = libraryHelper.getWritableDatabase();

        String select = LibraryContract.HoldEntry._ID + " = " + Integer.toString(rId);
        ContentValues updateLogs = LibraryDbHelper.updateLog(LibraryContract.LogEntry.COLUMN_TYPE_CANCEL, Calendar.getInstance().getTimeInMillis(), name, title, pick, drop, null);

        db.delete(LibraryContract.HoldEntry.TABLE_NAME, select, null);
        db.insert(LibraryContract.LogEntry.TABLE_NAME, null, updateLogs);

        Toast report = Toast.makeText(getApplicationContext(), R.string.toast_hold_cancel, Toast.LENGTH_SHORT);
        report.show();

        finish();
    }

    @Override
    public void doNegativeClick() {

    }
}
