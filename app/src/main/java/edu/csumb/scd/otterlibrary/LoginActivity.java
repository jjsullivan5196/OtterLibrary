package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private TextView etextUsername;
    private TextView etextPassword;
    private LibraryDbHelper libraryHelper;
    private Context context;
    private Intent pass;

    public static final String PASS_INTENT = "PASS";
    public static final String PASS_USER = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        pass = getIntent();

        etextUsername = (TextView)findViewById(R.id.etextUsername);
        etextPassword = (TextView)findViewById(R.id.etextPassword);
        libraryHelper = new LibraryDbHelper(context);
    }

    public void logIn(View view) {
        SQLiteDatabase db = libraryHelper.getReadableDatabase();
        LibraryUser holdUser;
        Toast report;

        String name = etextUsername.getText().toString();
        String password = etextPassword.getText().toString();

        try {
            holdUser = LibraryUser.getUserFromDb(name, password, db);
        } catch (RuntimeException e) {
            report = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        report = Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT);
        report.show();

        Class nextActivity = null;
        String act = pass.getStringExtra(PASS_INTENT);
        try {
            nextActivity = Class.forName(act);
        } catch (ClassNotFoundException e) {}
        pass.setClass(context, nextActivity);
        pass.putExtra(PASS_USER, name);

        startActivity(pass);
        finish();
    }
}
