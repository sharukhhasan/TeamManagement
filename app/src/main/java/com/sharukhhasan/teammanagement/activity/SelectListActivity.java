package com.sharukhhasan.teammanagement.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.util.HashMap;
import java.util.Map;

import com.sharukhhasan.teammanagement.R;
import com.sharukhhasan.teammanagement.adapter.FilteredAdapter;
import com.sharukhhasan.teammanagement.auth.LoginActivity;
import com.sharukhhasan.teammanagement.model.FilteredAdapterInterface;
import com.sharukhhasan.teammanagement.model.Manager;
import com.sharukhhasan.teammanagement.util.Constants;

public class SelectListActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthListener;

    private String mUid = "";
    private String mUidFromOutside;

    private ListView mListView;
    FilteredAdapter<Manager> mUsersListAdapter;

    // This activity need to work in 3 cases: 1- PromoteDemoteUsers , 2- Admin see all users , 3 - Manager can select users
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_list);

        setTitle("Add Users");

        mListView = (ListView) this.findViewById(R.id.listView);

        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        Bundle b = getIntent().getExtras();
        mUidFromOutside = b == null ? null : b.getString("uid");
        if (mUidFromOutside != null && mUidFromOutside.length() > 0) {
            mUid = mUidFromOutside;
            setupList();
        } else {
            mAuthListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                    if (authData != null) {
                        mUid = authData.getUid();
                        setupList();
                    } else {
                        //will be called when user logout (menu) -> mFirebaseRef.unauth()
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

    }


    private void setupList() {
        Query firebaseUsersRef = mFirebaseRef.child(Constants.FIREBASE_USERS).orderByChild("name");
        mUsersListAdapter = new FilteredAdapter<Manager>(firebaseUsersRef, Manager.class, R.layout.user_row, this, new FilteredAdapterInterface() {
            @Override
            public boolean allowObject(Object x) {
                Manager y = ((Manager) x);
                // Account for if manager key is not set, so pretend it is ""
                String adjManager = y.getManager() == null ? "" : y.getManager();
                // If the manager the user has is uid length < 2, then it is not a real manager
                return y.getLevel() == Constants.LEVEL_USER && adjManager.length() < 2;
            }
        }) {
            @Override
            protected void populateView(View v, Manager model) {
                ((TextView) v.findViewById(R.id.user_info)).setText(model.getName() + " - " + model.getEmail());

                // show add button in the list
                final String userUid = model.getKey();
                final String name = model.getName();
                final String email = model.getEmail();
                Button addButton = (Button) v.findViewById(R.id.buttonAction);
                addButton.setText("Add");
                if (addButton != null)
                    addButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            // add manager to user
                            Map<String, Object> post = new HashMap<String, Object>();
                            post.put("manager", mUid);
                            mFirebaseRef.child(Constants.FIREBASE_USERS).child(userUid).updateChildren(post);

                            // add user to manager
                            post = new HashMap<String, Object>();
                            post.put("name", name);
                            post.put("email", email);
                            mFirebaseRef.child(Constants.FIREBASE_MANAGERS).child(mUid).child(userUid).updateChildren(post);


                        }
                    });
            }
        };

        mListView.setAdapter(mUsersListAdapter);
    }

    private void openPromoteDemoteUsers(String uid) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUidFromOutside == null) {
        /* Cleanup the AuthStateListener */
            mFirebaseRef.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.managersettings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // mAuthListener will call finish() -> onDestroy()
            mFirebaseRef.unauth();
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
