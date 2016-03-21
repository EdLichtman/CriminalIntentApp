package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by EdwardLichtman on 3/20/16.
 */
public class DatePickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(PickerFragment.EXTRA_DATE);

        DatePickerFragment fragment = new DatePickerFragment();

        return fragment.newInstance(date);

    }
}
