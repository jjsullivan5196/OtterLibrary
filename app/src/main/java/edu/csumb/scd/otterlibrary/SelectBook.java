package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectBook extends ListActivity implements SimpleCursorAdapter.ViewBinder {
    private SimpleCursorAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private LibraryDbHelper libraryHelper;
    private Context context;
    private long pickup;
    private long dropoff;

    private static final String[] PROJECTION = {
            LibraryContract.BookEntry._ID,
            LibraryContract.BookEntry.COLUMN_NAME_TITLE,
            LibraryContract.BookEntry.COLUMN_NAME_AUTHOR,
            LibraryContract.BookEntry.COLUMN_NAME_ISBN,
            LibraryContract.BookEntry.COLUMN_NAME_FEE
    };
    private static final String SELECTION = "";
    private static final String sortOrder = LibraryContract.BookEntry.COLUMN_NAME_TITLE + " DESC";

    public static final String BOOK_SELECT_ID = "bookid";
    public static final String BOOK_SELECT_TITLE = "booktitle";
    public static final String BOOK_SELECT_AUTHOR = "bookauthor";
    public static final String BOOK_SELECT_FEE = "bookfee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_default);

        Intent mIntent = getIntent();
        pickup = mIntent.getLongExtra(PlaceHold.HOLD_PICKUP, 0);
        dropoff = mIntent.getLongExtra(PlaceHold.HOLD_DROPOFF, 0);
        context = getApplicationContext();
        libraryHelper = new LibraryDbHelper(context);
        mDb = libraryHelper.getReadableDatabase();

        mCursor = mDb.rawQuery("SELECT * FROM " + LibraryContract.BookEntry.TABLE_NAME, null);

        mAdapter = new SimpleCursorAdapter(
                this, // Context.
                R.layout.listitem_book,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {
                        LibraryContract.BookEntry.COLUMN_NAME_TITLE,
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
        Cursor sel = (Cursor)getListView().getItemAtPosition(position);
        LibraryBook sBook = LibraryBook.getBookFromCursor(sel);
        Intent loginIntent = getIntent();
        Toast report;

        if(LibraryHold.checkHolds(sBook.getTitle(), pickup, dropoff, libraryHelper.getReadableDatabase())) {
            loginIntent.setClass(context, LoginActivity.class);
            loginIntent.putExtra(BOOK_SELECT_TITLE, sBook.getTitle());
            loginIntent.putExtra(BOOK_SELECT_AUTHOR, sBook.getAuthor());
            loginIntent.putExtra(BOOK_SELECT_FEE, sBook.getFee());
            loginIntent.putExtra(LoginActivity.PASS_INTENT, ConfirmHold.class.getCanonicalName());
            startActivity(loginIntent);
            finish();
        }
        else {
            report = Toast.makeText(getApplicationContext(), R.string.error_res_invalid, Toast.LENGTH_SHORT);
            report.show();
        }
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        TextView mText = (TextView)view;
        LibraryBook b = LibraryBook.getBookFromCursor(cursor);
        switch(mText.getId()) {
            case R.id.bookTitle:
                mText.setText(String.format("%s (ISBN: %s)", b.getTitle(), b.getIsbn()));
                return true;
            case R.id.bookAuthor:
                mText.setText(b.getAuthor());
                return true;
            case R.id.bookPrice:
                mText.setText(String.format("$%.2f", b.getFee()));
                return true;
            default:
                return false;
        }
    }
}
