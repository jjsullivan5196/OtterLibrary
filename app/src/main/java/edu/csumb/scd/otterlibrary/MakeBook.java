package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MakeBook extends Activity implements MyAlertDialogFragment.FragmentAlertDialog {
    private EditText title;
    private EditText author;
    private EditText isbn;
    private EditText fee;

    String mAuthor;
    String mTitle;
    String mIsbn;
    String mFeeText;
    double mFee;

    ContentValues insertValues;

    private LibraryDbHelper libraryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_book);

        title = (EditText) findViewById(R.id.title);
        author = (EditText) findViewById(R.id.author);
        isbn = (EditText) findViewById(R.id.isbn);
        fee = (EditText) findViewById(R.id.fee);

        libraryHelper = new LibraryDbHelper(getApplicationContext());
    }

    public void submitBook(View view) {
        mTitle = title.getText().toString();
        mAuthor = author.getText().toString();
        mIsbn = isbn.getText().toString();
        mFeeText = fee.getText().toString();
        Toast report;

        if(mTitle.equals("") || mAuthor.equals("") || mIsbn.equals("") || mFeeText.equals("")) {
            report = Toast.makeText(getApplicationContext(), R.string.error_noinfo, Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        try {
            mFee = Double.parseDouble(mFeeText);
        } catch(NumberFormatException e) {
            report = Toast.makeText(getApplicationContext(), R.string.error_noinfo, Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        LibraryBook newBook = new LibraryBook(
                mTitle,
                mAuthor,
                mIsbn,
                mFee
        );
        insertValues = newBook.prepareBook();

        DialogFragment confirmBook = MyAlertDialogFragment.newInstance(R.string.make_book,
                String.format("Title: %s\nAuthor: %s\nISBN: %s\nFee $%.2f\n\nMake this title?", mTitle, mAuthor, mIsbn, mFee)
        );

        confirmBook.show(getFragmentManager(), "confirmBook");
    }

    @Override
    public void doPositiveClick() {
        SQLiteDatabase db = libraryHelper.getWritableDatabase();
        Toast report;

        try {
            db.insertOrThrow(LibraryContract.BookEntry.TABLE_NAME, null, insertValues);
        } catch (SQLException e) {
            report = Toast.makeText(getApplicationContext(), R.string.error_title, Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        report = Toast.makeText(getApplicationContext(), R.string.made_book, Toast.LENGTH_SHORT);
        report.show();
    }

    @Override
    public void doNegativeClick() {

    }
}
