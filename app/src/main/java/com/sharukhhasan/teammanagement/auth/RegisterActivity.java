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
import com.sharukhhasan.teammanagement.R;
import com.sharukhhasan.teammanagement.activity.UserListActivity;
import com.sharukhhasan.teammanagement.util.Constants;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText mEditName, mEditEmail, mEditPassword;
    Button mButtonSignup;

    ProgressDialog mProgressDialog;

    private String mName;
    private String mEmail;

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Register");

        mPrefs = getApplication().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");

        mEditName = (EditText) findViewById(R.id.edt_name);
        mEditEmail = (EditText) findViewById(R.id.edt_mail);
        mEditPassword = (EditText) findViewById(R.id.edt_pass);

        mButtonSignup = (Button) findViewById(R.id.btn_reg);
        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createAccount(mEditEmail.getText().toString(), mEditPassword.getText().toString());
            }
        });


        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    public void createAccount(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                Log.d(TAG, task.getResult().toString());
                if (!task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RegisterActivity.this, UserListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

}
