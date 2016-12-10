package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConfirmHold extends Activity {
    private String name;
    private String title;
    private String author;
    private Calendar pickup;
    private Calendar dropoff;
    private SimpleDateFormat infoDate;
    private double fee;
    private int recordId;
    private long hoursBorrow;

    private TextView confirm;

    private LibraryDbHelper libraryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hold);
        Intent mIntent = getIntent();

        name = mIntent.getStringExtra(LoginActivity.PASS_USER);

        recordId = mIntent.getIntExtra(SelectBook.BOOK_SELECT_ID, 0);
        title = mIntent.getStringExtra(SelectBook.BOOK_SELECT_TITLE);
        author = mIntent.getStringExtra(SelectBook.BOOK_SELECT_AUTHOR);

        pickup = Calendar.getInstance();
        dropoff = Calendar.getInstance();

        pickup.setTimeInMillis(mIntent.getLongExtra(PlaceHold.HOLD_PICKUP, 0));
        dropoff.setTimeInMillis(mIntent.getLongExtra(PlaceHold.HOLD_DROPOFF, 0));

        fee = mIntent.getDoubleExtra(SelectBook.BOOK_SELECT_FEE, 0);

        infoDate = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
        hoursBorrow = (dropoff.getTimeInMillis() - pickup.getTimeInMillis()) / (1000 * 60 * 60);
        confirm = (TextView)findViewById(R.id.confirmText);
        confirm.setText(String.format(
                getString(R.string.confirm_dialog),
                name,
                title,
                author,
                fee,
                infoDate.format(pickup.getTime()),
                infoDate.format(dropoff.getTime()),
                fee * hoursBorrow
        ));

        libraryHelper = new LibraryDbHelper(getApplicationContext());
    }

    public void confirmHold(View view) {
        Toast report;
        switch(view.getId()) {
            case R.id.bConfirm:
                SQLiteDatabase db = libraryHelper.getWritableDatabase();
                /*ContentValues updateBooks = new ContentValues();

                updateBooks.put(LibraryContract.BookEntry.COLUMN_NAME_HOLD, true);
                updateBooks.put(LibraryContract.BookEntry.COLUMN_NAME_HELD_BY, name);
                updateBooks.put(LibraryContract.BookEntry.COLUMN_NAME_PICKUP, pickup.getTimeInMillis());
                updateBooks.put(LibraryContract.BookEntry.COLUMN_NAME_DROPOFF, dropoff.getTimeInMillis());

                String selection = LibraryContract.BookEntry._ID + " = " + Integer.toString(recordId);

                db.update(LibraryContract.BookEntry.TABLE_NAME, updateBooks, selection, null);*/

                LibraryHold newHold = new LibraryHold(name, title, pickup.getTimeInMillis(), dropoff.getTimeInMillis(), fee * hoursBorrow);
                ContentValues holdStore = newHold.prepareHold();
                long id = db.insert(LibraryContract.HoldEntry.TABLE_NAME, null, holdStore);

                ContentValues log = new ContentValues();

                log.put(LibraryContract.LogEntry.COLUMN_NAME_TYPE, LibraryContract.LogEntry.COLUMN_TYPE_HOLD);
                log.put(LibraryContract.LogEntry.COLUMN_NAME_TIME, Calendar.getInstance().getTimeInMillis());
                log.put(LibraryContract.LogEntry.COLUMN_NAME_NAME, name);
                log.put(LibraryContract.LogEntry.COLUMN_NAME_TITLE, title);
                log.put(LibraryContract.LogEntry.COLUMN_NAME_PICKUP, pickup.getTimeInMillis());
                log.put(LibraryContract.LogEntry.COLUMN_NAME_DROPOFF, dropoff.getTimeInMillis());
                log.put(LibraryContract.LogEntry.COLUMN_NAME_FEE, fee * hoursBorrow);

                db.insert(LibraryContract.LogEntry.TABLE_NAME, null, log);

                report = Toast.makeText(getApplicationContext(), getString(R.string.toast_hold_success) + String.format(": %d", id), Toast.LENGTH_SHORT);
                report.show();

                finish();
                break;
            case R.id.bCancel:
                report = Toast.makeText(getApplicationContext(), R.string.toast_hold_cancel, Toast.LENGTH_SHORT);
                report.show();

                finish();
                break;
        }
    }
}
