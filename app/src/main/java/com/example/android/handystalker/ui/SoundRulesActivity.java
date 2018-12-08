package com.example.android.handystalker.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.ui.Adapters.RulesAdapter;
import com.example.android.handystalker.utilities.HandyRulesViewModel;

import java.util.List;

public class SoundRulesActivity extends AppCompatActivity {

    // Member variables
    private RulesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;

    private static final int MY_PERMISSIONS_REQUEST_WIFI = 1234;

    // Final String to store state information about the rules
    private static final String RULES = "rules";

    private static final String LIST_STATE_KEY = "list_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handy_rules);
        setTitle("Handy Rules");

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
        Intent intent = new Intent(this, NewSoundRuleActivity.class);
        startActivity(intent);
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
        HandyRulesViewModel viewModel = ViewModelProviders.of(this).get(HandyRulesViewModel.class);
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
                        mAdapter.setHandy(true);
                        mAdapter.setRulesFromDatabase(ruleEntries);

                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        // Initialize permissions checkbox
        CheckBox soundPermissions = findViewById(R.id.sound_permission_checkbox);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted
        if (android.os.Build.VERSION.SDK_INT >= 24 && !notificationManager.isNotificationPolicyAccessGranted()) {
            soundPermissions.setChecked(false);
        } else {
            soundPermissions.setChecked(true);
            soundPermissions.setEnabled(false);
        }

        CheckBox wifiPermissions = findViewById(R.id.wifi_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(SoundRulesActivity.this,
                Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            wifiPermissions.setChecked(false);
        } else {
            wifiPermissions.setChecked(true);
            wifiPermissions.setEnabled(false);
        }

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

    public void onSoundPermissionClicked(View view) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        }
        startActivity(intent);
    }

    public void onWifiPermissionClicked(View view) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                MY_PERMISSIONS_REQUEST_WIFI);
    }

}




