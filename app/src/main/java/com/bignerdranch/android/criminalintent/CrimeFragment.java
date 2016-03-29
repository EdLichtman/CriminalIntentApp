package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.support.v4.app.ShareCompat.IntentBuilder;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private SimpleDateFormat dtFormat = new SimpleDateFormat("MM/dd/yyyy");

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mSuspectPhoneNumberButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                IntentBuilder builder = IntentBuilder.from(getActivity());

                builder.setType("text/plain");
                builder.setText(getCrimeReport());
                builder.setSubject(getString(R.string.crime_report_subject));
                builder.createChooserIntent();
                startActivity(builder.getIntent());

            }

        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mSuspectPhoneNumberButton = (Button) v.findViewById(R.id.call_suspect);

        updateSuspect();


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();

        } else if (requestCode == REQUEST_CONTACT && data != null) {
            String contactId = "-1";

            Uri uri = data.getData();
            //Specify which fields you want your query to return values for
            String[] columns = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };
            Cursor c = getActivity().getContentResolver()
                    .query(uri, columns, null, null, null);

            try {
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    contactId = c.getString(1);
                    mCrime.setSuspect(suspect);
                }
            } catch (Exception e) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this.getActivity());

                dlgAlert.setMessage("No Contact found by that name");
                dlgAlert.setTitle("Error Message...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            return;
                            }
                            });
            } finally {
                c.close();
            }
            uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            columns = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
            String[] whereArgs = new String[] {contactId.toString()};

            c = getActivity().getContentResolver().query(
                    uri, columns, where, whereArgs, null);
            try {
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    mCrime.setSuspectPhoneNumber(c.getString(0));
                } else {
                    mCrime.setSuspectPhoneNumber(null);
                }

            } finally {
                c.close();
            }
            updateSuspect();
        }
    }

    private void updateSuspect() {
        if (mCrime.getSuspect() != "" || mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        if (mCrime.getSuspectPhoneNumber() == "" || mCrime.getSuspectPhoneNumber() == null ) {
            mSuspectPhoneNumberButton.setEnabled(false);
            mSuspectPhoneNumberButton.setText("No Phone Number associated");
        } else {
            mSuspectPhoneNumberButton.setText(getString(R.string.crime_call_suspect, mCrime.getSuspect()));
            mSuspectPhoneNumberButton.setEnabled(true);
        }

        if (mCrime.getSuspectPhoneNumber() != null) {
            final Intent callContact = new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + mCrime.getSuspectPhoneNumber()));

            mSuspectPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(callContact);
                }
            });
        }
    }

    private void updateDate() {
        mDateButton.setText(dtFormat.format(mCrime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
