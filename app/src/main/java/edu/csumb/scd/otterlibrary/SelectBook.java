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

public class SelectBook extends ListActivity implements SimpleCursorAdapter.ViewBinder {
    private SimpleCursorAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private LibraryDbHelper libraryHelper;
    private Context context;

    private static final String[] PROJECTION = {
            LibraryContract.BookEntry._ID,
            LibraryContract.BookEntry.COLUMN_NAME_TITLE,
            LibraryContract.BookEntry.COLUMN_NAME_AUTHOR,
            LibraryContract.BookEntry.COLUMN_NAME_ISBN,
            LibraryContract.BookEntry.COLUMN_NAME_FEE
    };
    private static final String SELECTION = LibraryContract.BookEntry.COLUMN_NAME_HOLD + " = 0";
    private static final String sortOrder = LibraryContract.BookEntry.COLUMN_NAME_TITLE + " DESC";

    public static final String BOOK_SELECT_ID = "bookid";
    public static final String BOOK_SELECT_TITLE = "booktitle";
    public static final String BOOK_SELECT_AUTHOR = "bookauthor";
    public static final String BOOK_SELECT_FEE = "bookfee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_default);

        context = getApplicationContext();
        libraryHelper = new LibraryDbHelper(context);
        mDb = libraryHelper.getReadableDatabase();
        mCursor = mDb.query(
                LibraryContract.BookEntry.TABLE_NAME,
                PROJECTION,
                SELECTION,
                null,
                null,
                null,
                sortOrder
        );

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
        Intent loginIntent = getIntent();

        loginIntent.setClass(context, LoginActivity.class);
        loginIntent.putExtra(BOOK_SELECT_ID, sel.getInt(sel.getColumnIndex(LibraryContract.BookEntry._ID)));
        loginIntent.putExtra(BOOK_SELECT_TITLE, sel.getString(sel.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_TITLE)));
        loginIntent.putExtra(BOOK_SELECT_AUTHOR, sel.getString(sel.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_AUTHOR)));
        loginIntent.putExtra(BOOK_SELECT_FEE, sel.getDouble(sel.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_FEE)));
        loginIntent.putExtra(LoginActivity.PASS_INTENT, ConfirmHold.class.getCanonicalName());
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        TextView mText = (TextView)view;
        switch(mText.getId()) {
            case R.id.bookTitle:
                String title = cursor.getString(i);
                String isbn = cursor.getString(cursor.getColumnIndex(LibraryContract.BookEntry.COLUMN_NAME_ISBN));
                mText.setText(String.format("%s (ISBN: %s)", title, isbn));
                return true;
            case R.id.bookAuthor:
                String author = cursor.getString(i);
                mText.setText(author);
                return true;
            case R.id.bookPrice:
                double price = cursor.getDouble(i);
                mText.setText(String.format("$%.2f", price));
                return true;
            default:
                return false;
        }
    }
}
