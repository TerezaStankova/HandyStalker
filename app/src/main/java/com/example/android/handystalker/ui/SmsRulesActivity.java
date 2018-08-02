package com.example.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class SmsRulesActivity extends AppCompatActivity {

    // Member variables
    private RulesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    // Member variable for the Database
    private AppDatabase mDb;

    private static final int PERMISSIONS_REQUEST = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_rules);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.sendrules_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void hidePlacesDataView() {
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.GONE);
    }


    private void setUpRulesViewModel() {
        showContactsDataView();
        RulesViewModel viewModel = ViewModelProviders.of(this).get(RulesViewModel.class);
        viewModel.getRules().observe(this, new Observer<List<RuleEntry>>() {
            @Override
            public void onChanged(@Nullable List<RuleEntry> ruleEntries) {
                Log.d("message", "Updating list of rules from LiveData in ViewModel"  + ruleEntries.size() );

                    if (ruleEntries != null) {
                        final List<Rule> mContactDatabase = newArrayList();

                        for (int i = 0; i < ruleEntries.size(); i++) {
                            System.out.println("mRuleEntry" + i + ruleEntries.get(i).getContactId());

                            final int id = ruleEntries.get(i).getId();

                            final int idContact = ruleEntries.get(i).getContactId();
                            final int idArrival = ruleEntries.get(i).getArrivalId();
                            final int idDeparture = ruleEntries.get(i).getDepartureId();

                            // Select from database
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    final String name = mDb.contactDao().findNameForContactId(idContact);
                                    final String arrival = mDb.placeDao().findPlaceNameById(idArrival);
                                    final String departure = mDb.placeDao().findPlaceNameById(idDeparture);
                                    Log.d("collect contact data","contact: ");

                                    Rule newRule = new Rule(id, arrival, name, departure);
                                    mContactDatabase.add(newRule);
                                }
                            });
                        }
                        mAdapter.setRules(mContactDatabase);
                }
            }

        });
    }

    public void onSMSPermissionClicked(View view) {
        ActivityCompat.requestPermissions(SmsRulesActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                PERMISSIONS_REQUEST);
    }

    public void onEmailPermissionClicked(View view) {

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
    }

}




