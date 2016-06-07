package com.sharukhhasan.teammanagement.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sharukhhasan.teammanagement.R;
import com.sharukhhasan.teammanagement.adapter.FirebaseGenericListAdapter;
import com.sharukhhasan.teammanagement.auth.LoginActivity;
import com.sharukhhasan.teammanagement.model.Report;
import com.sharukhhasan.teammanagement.util.Constants;

public class AdminSettingsActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase mFirebaseReportDateRef;
    private Firebase.AuthStateListener mAuthListener;

    private TextView mDateFromView;
    private TextView mDateToView;
    private ListView mListView;

    private FirebaseGenericListAdapter<Report> mListAdapter;

    long timeFrom = 0;
    long timeTo = 0;

    //private String mUid = "";

    private DatePickerDialog.OnDateSetListener myDateFromListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showFromDate(arg1, arg2, arg3);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateToListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showToDate(arg1, arg2, arg3);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        mDateFromView = (TextView) findViewById(R.id.dateViewFrom);
        mDateToView = (TextView) findViewById(R.id.dateViewTo);
        mListView = (ListView) findViewById(R.id.listView);

        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
        mFirebaseReportDateRef = mFirebaseRef.child(Constants.FIREBASE_REPORT_DATES);

        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {

                } else {
                    Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mIntent);
                    finish();
                    return;
                }
            }
        };
        mFirebaseRef.addAuthStateListener(mAuthListener);


    }


    // From DateModel selection
    public void showFromDatePickerDialog(View v) {
        if (mDateFromView.getText().toString() != null && mDateFromView.getText().toString().length() > 0) {
            try {
                Calendar calFrom = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
                calFrom.setTime(df.parse(mDateFromView.getText().toString()));
                new DatePickerDialog(this, myDateFromListener, calFrom.get(Calendar.YEAR), calFrom.get(Calendar.MONTH), calFrom.get(Calendar.DAY_OF_MONTH)).show();
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        } else {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, myDateFromListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private void showFromDate(int year, int month, int day) {
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, month, day);
        String formattedDate = df.format(newDate.getTime());
        mDateFromView.setText(formattedDate);
        timeFrom = newDate.getTimeInMillis();
    }


    // To DateModel selection
    public void showToDatePickerDialog(View v) {
        if (mDateToView.getText().toString() != null && mDateToView.getText().toString().length() > 0) {
            try {
                Calendar calTo = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
                calTo.setTime(df.parse(mDateToView.getText().toString()));
                new DatePickerDialog(this, myDateToListener, calTo.get(Calendar.YEAR), calTo.get(Calendar.MONTH), calTo.get(Calendar.DAY_OF_MONTH)).show();
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        } else {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, myDateToListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private void showToDate(int year, int month, int day) {
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, month, day);
        String formattedDate = df.format(newDate.getTime());
        mDateToView.setText(formattedDate);
        timeTo = newDate.getTimeInMillis();
    }


    public void onSubmit(View v) {
        if (timeFrom > 0 && timeTo > 0) {
            Query q = mFirebaseReportDateRef.orderByChild("time").startAt(timeFrom).endAt(timeTo);
            mListAdapter = new FirebaseGenericListAdapter<Report>(q, Report.class, R.layout.report_row, this) {
                @Override
                protected void populateView(View v, Report model) {
                    ((TextView) v.findViewById(R.id.report_info)).setText(model.getDate() + " - " + model.getName() + " - " + model.getHours() + " hours");
                }
            };
            mListView.setAdapter(mListAdapter);
        }
    }
}