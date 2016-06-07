package com.sharukhhasan.teammanagement.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.sharukhhasan.teammanagement.R;
import com.sharukhhasan.teammanagement.model.User;
import com.sharukhhasan.teammanagement.util.Constants;
import com.sharukhhasan.teammanagement.util.InputFilterMinMax;

public class UserSettingsActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase mFirebaseUserSettingsRef;

    EditText editTextPrefHours;
    LinearLayout mlayoutLevel;

    User mUserModel;
    AuthData mAuthData;
    User mAuthUserModel;

    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        editTextPrefHours = (EditText) findViewById(R.id.editTextPrefHours);
        editTextPrefHours.setFilters(new InputFilter[]{new InputFilterMinMax(Constants.HOURS_MIN, Constants.HOURS_MAX)});

        Bundle b = getIntent().getExtras();
        mUid = b.getString("uid");
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
        mFirebaseUserSettingsRef = mFirebaseRef.child(Constants.FIREBASE_USERS).child(mUid);
        mAuthData = mFirebaseRef.getAuth();

        mlayoutLevel = (LinearLayout) findViewById(R.id.layoutLevel);
        mlayoutLevel.setVisibility(View.INVISIBLE);

        mFirebaseUserSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //System.out.println(snapshot.getValue());
                if (snapshot == null || snapshot.getValue() == null)
                    return;

                mUserModel = snapshot.getValue(User.class);
                editTextPrefHours.setText(mUserModel.getPrefHours());

                // check if current auth user is the same as incoming uid
                // if not then it's a manager or admin -> open level option

                if (mAuthData != null) {
                    boolean isUser = mAuthData.getUid().equals(mUid);
                    mlayoutLevel.setVisibility(isUser ? View.INVISIBLE : View.VISIBLE);
                    updateUserLevelDisplay();
                } else {
                    // no user authenticated
                    setResult(RESULT_CANCELED);
                    finish();
                }


                mFirebaseRef.child(Constants.FIREBASE_USERS).child(mAuthData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        mAuthUserModel = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void onUpdate(View v) {
        try {

            mUserModel.setPrefHours(editTextPrefHours.getText().toString());
            mFirebaseUserSettingsRef.setValue(mUserModel); //settingModel);//userPrefHours);
            Intent intent = new Intent();

            //Manager promoted
            //If user is promoted to a manager or admin, we need to remove him from the manager's users list and remove the manager key inside of the user
            if (mUserModel.getLevel() != Constants.LEVEL_USER && mUserModel.getManager() != null) {
                mFirebaseRef.child(Constants.FIREBASE_USERS).child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User thisUser = snapshot.getValue(User.class);
                        mFirebaseRef.child(Constants.FIREBASE_MANAGERS).child(thisUser.getManager()).child(mUid).removeValue();
                        mFirebaseRef.child(Constants.FIREBASE_USERS).child(mUid).child("manager").setValue(null);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }

            setResult(RESULT_OK, intent);
        } catch (Exception e) {
            setResult(RESULT_CANCELED);
        }

        finish();
    }

    public void onCancel(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED);
        finish();
    }


    public void onPlus(View v) {
        setLevel(mUserModel.getLevel() + 1);
        //mFirebaseUserSettingsRef.setValue(mUserModel);
    }

    public void onMinus(View v) {
        setLevel(mUserModel.getLevel() - 1);
        //mFirebaseUserSettingsRef.setValue(mUserModel);
    }

    private void setLevel(int level) {
        if (mAuthUserModel == null) return;

        int requestedLevel = level;

        if (requestedLevel > mAuthUserModel.getLevel())
            requestedLevel = mAuthUserModel.getLevel();

        if (requestedLevel < 0)
            requestedLevel = 0;

        mUserModel.setLevel(requestedLevel);
        updateUserLevelDisplay();
    }

    private void updateUserLevelDisplay() {
        ((TextView) findViewById(R.id.textViewLevel)).setText("User Level: " + mUserModel.getLevel());
    }
}
