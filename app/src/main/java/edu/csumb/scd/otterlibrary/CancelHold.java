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

    private static final String[] PROJECTION = {
            LibraryContract.BookEntry._ID,
            LibraryContract.BookEntry.COLUMN_NAME_TITLE,
            LibraryContract.BookEntry.COLUMN_NAME_AUTHOR,
            LibraryContract.BookEntry.COLUMN_NAME_FEE,
            LibraryContract.BookEntry.COLUMN_NAME_PICKUP,
            LibraryContract.BookEntry.COLUMN_NAME_DROPOFF
    };
    private static final String SELECTION = LibraryContract.BookEntry.COLUMN_NAME_HELD_BY + " = ?";
    private static final String sortOrder = LibraryContract.BookEntry.COLUMN_NAME_TITLE + " DESC";

    public static final String BOOK_SELECT_ID = "bookid";
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
                LibraryContract.BookEntry.TABLE_NAME,
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
                        LibraryContract.BookEntry._ID,
                        LibraryContract.BookEntry.COLUMN_NAME_AUTHOR,
                        LibraryContract.BookEntry.COLUMN_NAME_FEE
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

        DialogFragment choice = MyAlertDialogFragment.newInstance(R.string.cancel_hold, "Are you sure?");
        choice.show(getFragmentManager(), "cancelHold");
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        TextView mText = (TextView)view;
        Calendar pickup = Calendar.getInstance();
        Calendar dropoff = Calendar.getInstance();
        switch(mText.getId()) {
            case R.id.bookTitle:
                int rId = cursor.getInt(i);
                String title = cursor.getString(cursor.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_TITLE));
                mText.setText(String.format("%s (ID: %d)", title, rId));
                return true;
            case R.id.bookAuthor:
                String author = cursor.getString(i);
                pickup.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_PICKUP)));
                dropoff.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_DROPOFF)));
                mText.setText(String.format("%s\nPickup: %s\nDropoff: %s", author, infoDate.format(pickup.getTime()), infoDate.format(dropoff.getTime())));
                return true;
            case R.id.bookPrice:
                double price = cursor.getDouble(i);
                pickup.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_PICKUP)));
                dropoff.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_DROPOFF)));

                long hours = (dropoff.getTimeInMillis() - pickup.getTimeInMillis()) / (1000 * 60 * 60);

                mText.setText(String.format("$%.2f", price * hours));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void doPositiveClick() {
        String title = selection.getString(selection.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_TITLE));
        int rId = selection.getInt(selection.getColumnIndex(LibraryContract.BookEntry._ID));
        long pick = selection.getLong(selection.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_PICKUP));
        long drop = selection.getLong(selection.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_DROPOFF));
        SQLiteDatabase db = libraryHelper.getWritableDatabase();

        String select = LibraryContract.BookEntry._ID + " = " + Integer.toString(rId);

        ContentValues updateTitle = new ContentValues();
        ContentValues updateLogs = new ContentValues();

        updateTitle.put(LibraryContract.BookEntry.COLUMN_NAME_HOLD, false);
        updateTitle.put(LibraryContract.BookEntry.COLUMN_NAME_HELD_BY, (String)null);
        updateTitle.put(LibraryContract.BookEntry.COLUMN_NAME_PICKUP, (Long)null);
        updateTitle.put(LibraryContract.BookEntry.COLUMN_NAME_DROPOFF, (Long)null);

        updateLogs.put(LibraryContract.LogEntry.COLUMN_NAME_TYPE, LibraryContract.LogEntry.COLUMN_TYPE_CANCEL);
        updateLogs.put(LibraryContract.LogEntry.COLUMN_NAME_TIME, Calendar.getInstance().getTimeInMillis());
        updateLogs.put(LibraryContract.LogEntry.COLUMN_NAME_NAME, name);
        updateLogs.put(LibraryContract.LogEntry.COLUMN_NAME_TITLE, title);

        db.update(LibraryContract.BookEntry.TABLE_NAME, updateTitle, select, null);
        db.insert(LibraryContract.LogEntry.TABLE_NAME, null, updateLogs);

        Toast report = Toast.makeText(getApplicationContext(), R.string.toast_hold_cancel, Toast.LENGTH_SHORT);
        report.show();

        finish();
    }

    @Override
    public void doNegativeClick() {

    }
}
