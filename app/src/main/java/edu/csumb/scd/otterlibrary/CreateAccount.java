package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;

public class CreateAccount extends Activity {
    private TextView etextUsername;
    private TextView etextPassword;
    private Context context;
    private LibraryDbHelper libraryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        context = getApplicationContext();
        etextUsername = (TextView)findViewById(R.id.etextUsername);
        etextPassword = (TextView)findViewById(R.id.etextPassword);

        libraryHelper = new LibraryDbHelper(context);
    }

    public void createAccount(View view) {
        Toast report;
        LibraryUser newUser;

        try {
            newUser = new LibraryUser(etextUsername.getText().toString(), etextPassword.getText().toString());
        } catch (RuntimeException e) {
            report = Toast.makeText(context, getString(R.string.toast_account_failure) + e.getMessage(), Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        try {
            SQLiteDatabase db = libraryHelper.getWritableDatabase();
            ContentValues prep = newUser.prepareUser();
            db.insertOrThrow(LibraryContract.UserEntry.TABLE_NAME, null, prep);
        } catch(SQLException e) {
            report = Toast.makeText(context, getString(R.string.toast_db_failure) + " " + newUser.toString(), Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        report = Toast.makeText(context, getString(R.string.toast_account_success) + " " + newUser.toString(), Toast.LENGTH_SHORT);

        report.show();
        finish();
    }
}
