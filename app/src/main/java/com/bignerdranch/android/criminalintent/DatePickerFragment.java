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
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by EdwardLichtman on 3/15/16.
 */
public class DatePickerFragment extends PickerFragment {

    private DatePicker mDatePicker;

    @Override
    protected Dialog buildAlertDialog() {

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
                                implementOnClick();
                            }
                        })
                .create();
    }

    @Override
    protected void implementOnClick() {
        setDatePickerDate(mDatePicker.getYear(), mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth());
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, hour, minute, 0);
        sendResult(Activity.RESULT_OK, date.getTime());
    }

    @Override
    public PickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.view_date, container, false);
    }

    @Override
    protected void buildPickerView(View v) {
        mDatePicker = (DatePicker) v.findViewById(R.id.view_date_date_picker);
        mDatePicker.init(year, month, day, null);
        mButtonOk = (Button) v.findViewById(R.id.view_date_picker_button);
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                implementOnClick();
            }
        });
    }
}
