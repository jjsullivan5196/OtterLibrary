package edu.csumb.scd.otterlibrary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import layout.DateTimeFragment;

public class PlaceHold extends Activity implements DateTimeFragment.OnTimeSetListener {
    private Calendar pickupDate;
    private Calendar dropoffDate;

    private TextView pickupDateText;
    private TextView dropoffDateText;

    private SimpleDateFormat infoDate;
    private Context context;

    public static final String HOLD_PICKUP = "PICKUP";
    public static final String HOLD_DROPOFF = "DROPOFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_hold);
        context = getApplicationContext();

        infoDate = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
        pickupDate = Calendar.getInstance();
        dropoffDate = Calendar.getInstance();

        pickupDateText = (TextView)findViewById(R.id.pickupText);
        dropoffDateText = (TextView)findViewById(R.id.dropoffText);
    }

    private void setPickupDate() {
        pickupDateText.setText(infoDate.format(pickupDate.getTime()));
    }
    private void setDropoffDate() {
        dropoffDateText.setText(infoDate.format(dropoffDate.getTime()));
    }

    public void setTime(View view) {
        DialogFragment setReservation;
        switch(view.getId()) {
            case R.id.pickupText:
                setReservation = DateTimeFragment.newInstance(HOLD_PICKUP, pickupDate.getTimeInMillis());
                break;
            case R.id.dropoffText:
                setReservation = DateTimeFragment.newInstance(HOLD_DROPOFF, dropoffDate.getTimeInMillis());
                break;
            default:
                setReservation = null;
        }

        setReservation.show(getFragmentManager(), "setTime");
    }

    public void submitReservation(View view) {
        Toast report;

        if(dropoffDate.getTimeInMillis() <= pickupDate.getTimeInMillis())
        {
            report = Toast.makeText(context, R.string.error_invalid_date, Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        long reserveTime = TimeUnit.MILLISECONDS.toMinutes(dropoffDate.getTimeInMillis() - pickupDate.getTimeInMillis());

        if(reserveTime > (7 * 24 * 60)) {
            report = Toast.makeText(context, R.string.error_res_too_long, Toast.LENGTH_SHORT);
            report.show();
            return;
        }

        Intent chooseBook = new Intent(this, SelectBook.class);
        chooseBook.putExtra(HOLD_PICKUP, pickupDate.getTimeInMillis());
        chooseBook.putExtra(HOLD_DROPOFF, dropoffDate.getTimeInMillis());
        startActivity(chooseBook);
        finish();
    }

    @Override
    public void onTimeSet(long ret, String field) {
        switch(field) {
            case HOLD_PICKUP:
                pickupDate.setTimeInMillis(ret);
                setPickupDate();
                break;
            case HOLD_DROPOFF:
                dropoffDate.setTimeInMillis(ret);
                setDropoffDate();
                break;
        }
    }
}

