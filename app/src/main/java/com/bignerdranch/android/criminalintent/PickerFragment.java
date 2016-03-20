package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by EdwardLichtman on 3/20/16.
 */
public abstract class PickerFragment extends DialogFragment {

    public static final String ARG_DATE = "date";
    public static final String ARG_TIME = "time";
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    private TimePicker mTimePicker;
    protected int year;
    protected int month;
    protected int day;
    protected int hour;
    protected int minute;

    public abstract PickerFragment newInstance(Date date);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setDatePickerDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        setDatePickerTime(calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE));

        return buildAlertDialog();
    }

    protected abstract Dialog buildAlertDialog ();

    protected void setDatePickerDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    protected void setDatePickerTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    protected void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
