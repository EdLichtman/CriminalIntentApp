package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by EdwardLichtman on 3/20/16.
 */
public class TimePickerFragment extends PickerFragment{

    private TimePicker mTimePicker;

    @Override
    protected Dialog buildAlertDialog() {


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
                        implementOnClick();
                    }
                })
                .create();
    }
    @Override
    protected void implementOnClick() {
        setDatePickerTime(mTimePicker.getCurrentHour(),
                mTimePicker.getCurrentMinute());
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, hour, minute, 0);
        sendResult(Activity.RESULT_OK, date.getTime());
    }

    @Override
    public PickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.view_time, container, false);
    }

    @Override
    protected void buildPickerView(View v) {
        mTimePicker = (TimePicker) v.findViewById(R.id.view_time_time_picker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        mButtonOk = (Button) v.findViewById(R.id.view_time_picker_button);
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                implementOnClick();
            }
        });
    }
}
