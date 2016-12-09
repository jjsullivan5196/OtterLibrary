package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class MenuActivity extends Activity {
    private Button bCreateAccount;
    private Button bPlaceHold;
    private Button bCancelHold;
    private Button bManageSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bCreateAccount = (Button)findViewById(R.id.bCreateAccount);
        bPlaceHold = (Button)findViewById(R.id.bPlaceHold);
        bCancelHold = (Button)findViewById(R.id.bCancelHold);
        bManageSystem = (Button)findViewById(R.id.bManageSystem);
    }

    public void launchActivity(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.bCreateAccount:
                intent = new Intent(this, CreateAccount.class);
                break;
            case R.id.bPlaceHold:
                intent = new Intent(this, PlaceHold.class);
                break;
            case R.id.bCancelHold:
                intent = new Intent(this, LoginActivity.class);
                String act = CancelHold.class.getCanonicalName();
                intent.putExtra(LoginActivity.PASS_INTENT, act);
                break;
            default:
                return;
        }

        startActivity(intent);
    }
}
