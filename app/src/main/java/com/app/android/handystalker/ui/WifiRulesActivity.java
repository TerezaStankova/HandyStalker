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
import android.support.v4.content.ContextCompat;
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
import com.app.android.handystalker.utilities.WifiRulesViewModel;

import java.util.List;

public class WifiRulesActivity extends AppCompatActivity {

    // Member variables
    private RulesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;

    private static final int MY_PERMISSIONS_REQUEST_WIFI = 1234;
    private static final int PERMISSIONS_REQUEST_STORAGE = 161;

    // Final String to store state information about the rules
    private static final String RULES = "rules";

    private static final String LIST_STATE_KEY = "list_state";
    // Member variable for the Database
    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_rules);
        setTitle(R.string.wifi_rules);
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Set up the recycler view
        mRecyclerView = findViewById(R.id.handyrules_list_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        restoreLayoutManagerPosition();
        mRecyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            // Load the saved state if there is one
            restoreLayoutManagerPosition();
            showContactsDataView();
        }

        mAdapter = new RulesAdapter(this, null);
        mAdapter.setHandy(true);
        mRecyclerView.setAdapter(mAdapter);

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
        mAdapter.setDatabase(mDb);

        setUpRulesViewModel();
    }

    public void onAddSendRulesButtonClicked(View view) {
            goToNewRule();
    }

    private void goToNewRule(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, getString(R.string.storage), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(WifiRulesActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_STORAGE);
            return;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CHANGE_WIFI_STATE)) {
                // Show an explanation to the user *asynchronously*
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                        MY_PERMISSIONS_REQUEST_WIFI);
            }

            return;
        }

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                final int countPlaces = mDb.placeDao().countPlaceIds();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (countPlaces == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.one_place), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), NewWifiRuleActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private void showContactsDataView() {
        /* Then, make sure the data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRulesDataView() {
        /* Then, make sure the data is invisible */
        mRecyclerView.setVisibility(View.GONE);
    }


    private void setUpRulesViewModel() {
        WifiRulesViewModel viewModel = ViewModelProviders.of(this).get(WifiRulesViewModel.class);
        viewModel.getRules().observe(this, new Observer<List<RuleEntry>>() {
            @Override
            public void onChanged(@Nullable List<RuleEntry> ruleEntries) {
                if (ruleEntries != null) {
                    Log.d("message", "Updating list of rules from LiveData in ViewModel" + ruleEntries.size());
                    if (ruleEntries.size() == 0) {
                        hideRulesDataView();
                        return;
                    }
                        showContactsDataView();
                        mAdapter.setWifiRule(true);
                        mAdapter.setRulesFromDatabase(ruleEntries);

                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        // Initialize permissions checkbox
        /*CheckBox wifiPermissions = findViewById(R.id.wifi_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(WifiRulesActivity.this,
                Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            wifiPermissions.setChecked(false);
        } else {
            wifiPermissions.setChecked(true);
            wifiPermissions.setEnabled(false);
        }*/

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }

    private void restoreLayoutManagerPosition() {
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        // Save list state
        mListState = layoutManager.onSaveInstanceState();
        savedInstanceState.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    goToNewRule();
                }
            }
        }
    }
}
