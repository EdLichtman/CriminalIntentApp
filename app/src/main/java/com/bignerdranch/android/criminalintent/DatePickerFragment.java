package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by EdwardLichtman on 3/15/16.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String ARG_DATE = "date";
    public static final String ARG_TIME = "time";
    public static final String ARG_BUTTON = "button type";
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public static DatePickerFragment newInstance(Date date, String dateTime) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUTTON, dateTime);
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        String buttonType = (String) getArguments().getSerializable(ARG_BUTTON);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setDatePickerDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        setDatePickerTime(calendar.get(Calendar.HOUR),
                            calendar.get(Calendar.MINUTE));

        if (buttonType == ARG_DATE) {
            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_date, null);

            mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
            mDatePicker.init(year, month, day, null);

            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle(R.string.date_picker_title)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setDatePickerDate(mDatePicker.getYear(), mDatePicker.getMonth(),
                                            mDatePicker.getDayOfMonth());
                                    Calendar date = Calendar.getInstance();
                                    date.set(year, month, day, hour, minute, 0);
                                    sendResult(Activity.RESULT_OK, date.getTime());
                                }
                            })
                    .create();

        } else if (buttonType == ARG_TIME) {
            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_time, null);

            mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);

            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle(R.string.time_picker_title)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setDatePickerTime(mTimePicker.getCurrentHour(),
                                    mTimePicker.getCurrentMinute());
                            Calendar date = Calendar.getInstance();
                            date.set(year, month, day, hour, minute, 0);
                            sendResult(Activity.RESULT_OK, date.getTime());
                        }
                    })
                    .create();
        }

        return null;

    }

    private void setDatePickerDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private void setDatePickerTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
