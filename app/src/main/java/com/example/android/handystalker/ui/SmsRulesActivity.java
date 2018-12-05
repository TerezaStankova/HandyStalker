package com.example.android.handystalker.ui;

import android.Manifest;
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
import com.example.android.handystalker.utilities.RulesViewModel;

import java.util.List;

public class SmsRulesActivity extends AppCompatActivity {

    // Member variables
    private RulesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;


    private static final int PERMISSIONS_REQUEST = 2222;

    // Final String to store state information about the rules
    private static final String RULES = "rules";

    private static final String LIST_STATE_KEY = "list_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_rules);
        setTitle("Stalking Rules");

        // Set up the recycler view
        mRecyclerView = findViewById(R.id.sendrules_list_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        restoreLayoutManagerPosition();
        mRecyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            // Load the saved state (the array of trailers) if there is one
            showContactsDataView();
        }

        mAdapter = new RulesAdapter(this, null);
        mAdapter.setHandy(false);
        mRecyclerView.setAdapter(mAdapter);

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
        mAdapter.setDatabase(mDb);

        setUpRulesViewModel();
    }

    public void onAddSendRulesButtonClicked(View view) {
        Intent intent = new Intent(this, NewTextRuleActivity.class);
        startActivity(intent);
    }

    private void showContactsDataView() {
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRulesDataView() {
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.GONE);
    }


    private void setUpRulesViewModel() {
        RulesViewModel viewModel = ViewModelProviders.of(this).get(RulesViewModel.class);
        viewModel.getRules().observe(this, new Observer<List<RuleEntry>>() {
            @Override
            public void onChanged(@Nullable List<RuleEntry> ruleEntries) {
                Log.d("message", "Updating list of rules from LiveData in ViewModel"  + ruleEntries.size() );
                if (ruleEntries.size() == 0) {
                    hideRulesDataView();
                    return;
                }

                if (ruleEntries != null) {
                        showContactsDataView();
                        mAdapter.setHandy(false);
                        mAdapter.setRulesFromDatabase(ruleEntries);
                }
            }

        });
    }

    public void onSMSPermissionClicked(View view) {
        ActivityCompat.requestPermissions(SmsRulesActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                PERMISSIONS_REQUEST);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialize location permissions checkbox
        CheckBox smsPermissions = findViewById(R.id.sms_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(SmsRulesActivity.this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            smsPermissions.setChecked(false);
        } else {
            smsPermissions.setChecked(true);
            smsPermissions.setEnabled(false);
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

    public void onMessagesButtonClicked(View view) {
        Intent intent = new Intent(this, AddMessageActivity.class);
        startActivity(intent);
    }

    public void onContactsButtonClicked(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }
}




