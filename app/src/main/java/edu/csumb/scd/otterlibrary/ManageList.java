package edu.csumb.scd.otterlibrary;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ManageList extends ListActivity implements SimpleCursorAdapter.ViewBinder, MyAlertDialogFragment.FragmentAlertDialog, View.OnClickListener {
    private SimpleCursorAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private LibraryDbHelper libraryHelper;
    private Context context;
    private String name;
    private SimpleDateFormat infoDate;
    private Button okButton;

    private static final String[] PROJECTION = {
            LibraryContract.LogEntry._ID,
            LibraryContract.LogEntry.COLUMN_NAME_TYPE,
            LibraryContract.LogEntry.COLUMN_NAME_TIME,
            LibraryContract.LogEntry.COLUMN_NAME_NAME,
            LibraryContract.LogEntry.COLUMN_NAME_TITLE,
            LibraryContract.LogEntry.COLUMN_NAME_PICKUP,
            LibraryContract.LogEntry.COLUMN_NAME_DROPOFF,
            LibraryContract.LogEntry.COLUMN_NAME_FEE
    };

    public static final String BOOK_SELECT_TITLE = "booktitle";
    public static final String BOOK_SELECT_AUTHOR = "bookauthor";
    public static final String BOOK_SELECT_FEE = "bookfee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_admin);

        okButton = (Button)findViewById(R.id.bAdminOK);
        okButton.setOnClickListener(this);

        infoDate = new SimpleDateFormat("MMM dd yyyy hh:mm aa");

        Intent mInstance = getIntent();
        name = mInstance.getStringExtra(LoginActivity.PASS_USER);

        if(!name.equals("!admin2")) {
            Toast alert = Toast.makeText(getApplicationContext(), R.string.not_admin, Toast.LENGTH_SHORT);
            alert.show();
            finish();
        }

        context = getApplicationContext();
        libraryHelper = new LibraryDbHelper(context);
        mDb = libraryHelper.getReadableDatabase();
        mCursor = mDb.rawQuery("SELECT * FROM " + LibraryContract.LogEntry.TABLE_NAME, null);

        mAdapter = new SimpleCursorAdapter(
                this, // Context.
                R.layout.listitem_book,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {
                        LibraryContract.LogEntry.COLUMN_NAME_TYPE,
                        LibraryContract.LogEntry.COLUMN_NAME_NAME,
                        LibraryContract.LogEntry.COLUMN_NAME_TIME
                },
                new int[] {R.id.bookTitle, R.id.bookAuthor, R.id.bookPrice},
                0
        );
        mAdapter.setViewBinder(this);

        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int i) {
        TextView mText = (TextView)view;

        String type = cursor.getString(cursor.getColumnIndex(LibraryContract.LogEntry.COLUMN_NAME_TYPE));
        String name = cursor.getString(cursor.getColumnIndex(LibraryContract.LogEntry.COLUMN_NAME_NAME));
        String title = cursor.getString(cursor.getColumnIndex(LibraryContract.LogEntry.COLUMN_NAME_TITLE));
        Double fee = cursor.getDouble(cursor.getColumnIndex(LibraryContract.LogEntry.COLUMN_NAME_FEE));

        Calendar logtime = Calendar.getInstance();
        Calendar pickup = Calendar.getInstance();
        Calendar dropoff = Calendar.getInstance();

        logtime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LibraryContract.LogEntry.COLUMN_NAME_TIME)));
        pickup.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LibraryContract.LogEntry.COLUMN_NAME_PICKUP)));
        dropoff.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(LibraryContract.LogEntry.COLUMN_NAME_DROPOFF)));

        String setView = "";

        switch(mText.getId()) {
            case R.id.bookTitle:
                switch(type) {
                    case LibraryContract.LogEntry.COLUMN_TYPE_ACCOUNT:
                        setView = "Account Created";
                        break;
                    case LibraryContract.LogEntry.COLUMN_TYPE_HOLD:
                        setView = "Hold Created";
                        break;
                    case LibraryContract.LogEntry.COLUMN_TYPE_CANCEL:
                        setView = "Hold Cancelled";
                        break;
                }
                break;
            case R.id.bookAuthor:
                switch(type) {
                    case LibraryContract.LogEntry.COLUMN_TYPE_ACCOUNT:
                        setView = name;
                        break;
                    case LibraryContract.LogEntry.COLUMN_TYPE_HOLD:
                        setView = String.format("%s\nTitle: %s\nPickup: %s\nDropoff: %s\nPrice: $%.2f", name, title,infoDate.format(pickup.getTime()), infoDate.format(dropoff.getTime()), fee);
                        break;
                    case LibraryContract.LogEntry.COLUMN_TYPE_CANCEL:
                        setView = String.format("%s\nTitle: %s\nPickup: %s\nDropoff: %s", name, title, infoDate.format(pickup.getTime()), infoDate.format(dropoff.getTime()));
                        break;
                }
                break;
            case R.id.bookPrice:
                setView = infoDate.format(logtime.getTime());
                break;
            default:
                return false;
        }

        mText.setText(setView);
        return true;
    }

    @Override
    public void doPositiveClick() {
        Intent makeBook = new Intent(this, MakeBook.class);
        startActivity(makeBook);
        finish();
    }

    @Override
    public void doNegativeClick() {

    }

    @Override
    public void onClick(View v) {
        DialogFragment createBook = MyAlertDialogFragment.newInstance(R.string.make_new_book, "Create a new book entry?");
        createBook.show(getFragmentManager(), "createBook");
    }
}
