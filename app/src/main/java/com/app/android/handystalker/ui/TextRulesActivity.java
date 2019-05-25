package com.app.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.RuleEntry;
import com.app.android.handystalker.ui.Adapters.RulesAdapter;
import com.app.android.handystalker.utilities.AppExecutors;
import com.app.android.handystalker.utilities.TextRulesViewModel;

import java.util.List;

public class TextRulesActivity extends AppCompatActivity {

    // Member variables
    private RulesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;

    // Member variable for the Database
    private AppDatabase mDb;


    private static final int PERMISSIONS_REQUEST = 2222;
    private static final int PERMISSIONS_REQUEST_STORAGE = 161;

    // Final String to store state information about the rules
    private static final String RULES = "rules";

    private static final String LIST_STATE_KEY = "list_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_rules);
        setTitle(R.string.sms_rules);
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Set up the recycler view
        mRecyclerView = findViewById(R.id.sendrules_list_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        restoreLayoutManagerPosition();
        mRecyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            // Load the saved state (the array of rules) if there is one
            showRulesDataView();
        }

        mAdapter = new RulesAdapter(this, null);
        mAdapter.setHandy(false);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setDatabase(mDb);

        setUpRulesViewModel();
    }

    public void onAddSendRulesButtonClicked(View view) {

        if (ActivityCompat.checkSelfPermission(TextRulesActivity.this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.allow_texting), Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(TextRulesActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSIONS_REQUEST);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, getString(R.string.storage), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(TextRulesActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_STORAGE);
            return;
        }

        checkDatabase();

    }

    private void checkDatabase() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                final int countContacts = mDb.contactDao().countContacts();
                final int countPlaces = mDb.placeDao().countPlaceIds();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (countPlaces == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.one_place), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(intent);
                        } else if (countContacts == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.one_contact), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), NewContactActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), NewTextRuleActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private void showRulesDataView() {
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRulesDataView() {
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.GONE);
    }


    private void setUpRulesViewModel() {
        TextRulesViewModel viewModel = ViewModelProviders.of(this).get(TextRulesViewModel.class);
        viewModel.getRules().observe(this, new Observer<List<RuleEntry>>() {
            @Override
            public void onChanged(@Nullable List<RuleEntry> ruleEntries) {
                Log.d("message", "Updating list of rules from LiveData in ViewModel" + ruleEntries.size());
                if (ruleEntries.size() == 0) {
                    hideRulesDataView();
                    return;
                }

                if (ruleEntries != null) {
                    showRulesDataView();
                    mAdapter.setTextRule(true);
                    mAdapter.setRulesFromDatabase(ruleEntries);
                }
            }

        });
    }

    public void onSMSPermissionClicked(View view) {
        /*ActivityCompat.requestPermissions(TextRulesActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                PERMISSIONS_REQUEST);*/
    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialize location permissions checkbox
        /*CheckBox smsPermissions = findViewById(R.id.sms_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(TextRulesActivity.this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            smsPermissions.setChecked(false);
        } else {
            smsPermissions.setChecked(true);
            smsPermissions.setEnabled(false);
        }

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }*/
    }

    private void restoreLayoutManagerPosition() {
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save list state
        mListState = layoutManager.onSaveInstanceState();
        savedInstanceState.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if (state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    public void onMessagesButtonClicked(View view) {
        Intent intent = new Intent(this, AddMessageActivity.class);
        startActivity(intent);
    }

    public void onContactsButtonClicked(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    checkDatabase();
                }
            }
            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    checkDatabase();
                }
            }
        }
    }
}




