package com.example.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.model.Contact;
import com.example.android.handystalker.model.Rule;
import com.example.android.handystalker.ui.Adapters.RulesAdapter;
import com.example.android.handystalker.utilities.AppExecutors;
import com.example.android.handystalker.utilities.ContactsViewModel;
import com.example.android.handystalker.utilities.RulesViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class SmsRulesActivity extends AppCompatActivity {

    // Member variables
    private RulesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;

    // Member variable for the Database
    private AppDatabase mDb;
    //List<Rule> mRuleDatabase = newArrayList();

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
        mRecyclerView = (RecyclerView) findViewById(R.id.sendrules_list_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        restoreLayoutManagerPosition();

        if (savedInstanceState != null) {
            // Load the saved state (the array of trailers) if there is one
            showContactsDataView();
            //mRuleDatabase = savedInstanceState.getParcelableArrayList(RULES);
        }

        mAdapter = new RulesAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        mDb = AppDatabase.getInstance(getApplicationContext());
        mAdapter.setDatabase(mDb);

        setUpRulesViewModel();
    }

    public void onAddSendRulesButtonClicked(View view) {
        Intent intent = new Intent(this, NewRuleActivity.class);
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

    public static void setmRulesDatabase(List<Rule> ruleDatabase) {
        //mRuleDatabase = ruleDatabase;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialize location permissions checkbox
        CheckBox smsPermissions = (CheckBox) findViewById(R.id.sms_permission_checkbox);
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
        //savedInstanceState.putParcelableArrayList(RULES, (ArrayList<Rule>) mRuleDatabase);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }
}




