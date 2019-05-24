package com.app.android.handystalker.ui;

import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import com.app.android.handystalker.utilities.SoundRulesViewModel;

import java.util.List;

public class SoundRulesActivity extends AppCompatActivity {

    // Member variables
    private RulesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;

    private static final String LIST_STATE_KEY = "list_state";
    // Member variable for the Database
    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_rules);
        setTitle(R.string.sound_rules);
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Set up the recycler view
        mRecyclerView = findViewById(R.id.soundrules_list_recycler_view);
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

    public void onAddSoundRulesButtonClicked(View view) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted
        if (android.os.Build.VERSION.SDK_INT >= 24 && !notificationManager.isNotificationPolicyAccessGranted()) {
            Toast.makeText(this, getString(R.string.allow_sound), Toast.LENGTH_LONG).show();
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
                            return;
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), NewSoundRuleActivity.class);
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
        SoundRulesViewModel viewModel = ViewModelProviders.of(this).get(SoundRulesViewModel.class);
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
                        mAdapter.setSoundRule(true);
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
}




