package com.sharukhhasan.teammanagement.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sharukhhasan.teammanagement.R;
import com.sharukhhasan.teammanagement.adapter.FilteredAdapter;
import com.sharukhhasan.teammanagement.auth.LoginActivity;
import com.sharukhhasan.teammanagement.model.FilteredAdapterInterface;
import com.sharukhhasan.teammanagement.model.Manager;
import com.sharukhhasan.teammanagement.util.Constants;

public class AdminListActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private Firebase.AuthStateListener mAuthListener;

    private String mUid = "";

    private ListView mListView;
    FilteredAdapter<Manager> mAdminListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        setTitle("Admin");

        mListView = (ListView) this.findViewById(R.id.listView);

        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    mUid = authData.getUid();
                    setupAdminList();
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

    private void setupAdminList() {
        Query firebaseUsersRef = mFirebaseRef.child(Constants.FIREBASE_USERS).orderByChild("name");
        mAdminListAdapter = new FilteredAdapter<Manager>(firebaseUsersRef, Manager.class, R.layout.user_row, this, new FilteredAdapterInterface() {
            @Override
            public boolean allowObject(Object x) {
                Manager y = ((Manager) x);
                return !y.getKey().equals(mUid);
            }
        }) {
            @Override
            protected void populateView(View v, Manager model) {
                // show Name + Email + (Level)
                ((TextView) v.findViewById(R.id.user_info)).setText(model.getName() + " - " + model.getEmail() + " (" + Constants.LEVEL_TO_STRING[model.getLevel()] + ")");

                final String userUid = model.getKey();

                // remove the button from List that admin see. no need for it.
                Button removeButton = (Button) v.findViewById(R.id.buttonAction);
                if (removeButton != null) {
                    ViewGroup g = (ViewGroup) removeButton.getParent();
                    if (g != null)
                        g.removeView(removeButton);
                }


            }
        };
        mListView.setAdapter(mAdminListAdapter);

        // when List clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Manager mm = (Manager) (mAdminListAdapter.getItem((int) id));
                openActivityBasedOnLevel(mm.getKey(), mm.getLevel());
            }
        });
    }

    private void openActivityBasedOnLevel(String uid, int level) {
        if (level == Constants.LEVEL_ADMIN)
            return;
        Intent intent = new Intent(this, Constants.LEVEL_TO_ACTIVITY[level]);
        Bundle b = new Bundle();
        b.putString("uid", uid);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        mFirebaseRef.removeAuthStateListener(mAuthListener);
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
        if (id == R.id.action_settings) {
            startAdminSettingsActivity();
            return true;
        } else if (id == R.id.action_logout) {
            // mAuthListener will call finish() -> onDestroy()
            mFirebaseRef.unauth();
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    synchronized private void startAdminSettingsActivity() {

        Intent intent = new Intent(this, AdminSettingsActivity.class);
        startActivity(intent);
    }


}
