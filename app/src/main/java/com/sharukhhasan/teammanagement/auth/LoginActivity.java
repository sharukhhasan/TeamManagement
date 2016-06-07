package com.sharukhhasan.teammanagement.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sharukhhasan.teammanagement.R;
import com.sharukhhasan.teammanagement.activity.UserListActivity;
import com.sharukhhasan.teammanagement.model.User;
import com.sharukhhasan.teammanagement.util.Constants;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEditEmail, mEditPassword;
    private Button mButtonSingin;

    private ProgressDialog mProgressDialog;

    private String mEmail;

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    //onAuthenticatedStartMain(user.getUid(), user.getEmail());
                    onSignin(getCurrentFocus());
                }
                else
                {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        setTitle("Log in");

        mPrefs = getApplication().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");

        mEditEmail = (EditText) findViewById(R.id.editName);
        mEditPassword = (EditText) findViewById(R.id.editPass);
        mButtonSingin = (Button) findViewById(R.id.buttonSignin);

        setupUsername();

    }

    private void onAuthenticatedStartMain(String uid, String email) {
        mEmail = email;
        mPrefs.edit().putString("email", mEmail).apply();

        // check level of auth uid
        /*.child(Constants.FIREBASE_USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User mUserModel = snapshot.getValue(User.class);

                Intent intent = new Intent(getApplicationContext(), Constants.LEVEL_TO_ACTIVITY[mUserModel.getLevel()]);
                startActivity(intent);
                finish();

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                mProgressDialog.dismiss();
            }
        });*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        //mFirebaseRef.removeAuthStateListener(mAuthListener);
    }

    private void setupUsername() {
        //SharedPreferences mPrefs = getApplication().getSharedPreferences(AppConstants.SHARED_PREFERENCES_NAME, 0);
        mEmail = mPrefs.getString("email", null);
        if (mEmail != null && mEmail.length() > 0) {
            mEditEmail.setText(mEmail);
        }

    }

    public void onSignin(View v) {
        mProgressDialog.show();

        final String email = mEditEmail.getText().toString();
        final String password = mEditPassword.getText().toString();

        if (email != null && email.length() > 0 && password != null && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    if(!task.isSuccessful())
                    {
                        Log.w(TAG, "signInWithEmail", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(LoginActivity.this, UserListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else {
            mProgressDialog.dismiss();
        }



    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void onRegister(View v) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

}
