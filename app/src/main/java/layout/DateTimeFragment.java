package layout;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.csumb.scd.otterlibrary.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DateTimeFragment.OnTimeSetListener} interface
 * to handle interaction events.
 * Use the {@link DateTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateTimeFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FIELD = "FIELD";
    private static final String ARG_TIME = "TIME";

    // TODO: Rename and change types of parameters
    private String mField;
    private long mTime;
    private OnTimeSetListener mListener;
    private Calendar mCalendar;

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    public DateTimeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param field Parameter 1.
     * @return A new instance of fragment DateTimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DateTimeFragment newInstance(String field, long time) {
        DateTimeFragment fragment = new DateTimeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FIELD, field);
        args.putLong(ARG_TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mField = getArguments().getString(ARG_FIELD);
            mTime = getArguments().getLong(ARG_TIME);
        }

        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(mTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_date_time, container, false);

        mDatePicker = (DatePicker)v.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker)v.findViewById(R.id.timePicker);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        mDatePicker.updateDate(year, month, day);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);

        ((Button)v.findViewById(R.id.bTimeOk)).setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimeSetListener) {
            mListener = (OnTimeSetListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimeSetListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Calendar ret = new GregorianCalendar(
                mDatePicker.getYear(),
                mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(),
                mTimePicker.getHour(),
                mTimePicker.getMinute()
        );

        mListener.onTimeSet(ret.getTimeInMillis(), mField);
        dismiss();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTimeSetListener {
        // TODO: Update argument type and name
        void onTimeSet(long ret, String field);
    }
}
