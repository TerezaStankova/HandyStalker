package com.app.android.handystalker.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.PlaceEntry;
import com.app.android.handystalker.database.RuleEntry;
import com.app.android.handystalker.utilities.AppExecutors;
import com.app.android.handystalker.utilities.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;

public class NewNotificationRuleActivity extends AppCompatActivity {

    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();
    List<String> placeNamesAnywhere = new ArrayList<String>();
    public final String CHANNEL_ID = "default";

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner arrivalSpinner;
    Spinner departureSpinner;
    Spinner departureAnywhereSpinner;

    Integer arrivalId = null;
    Integer departureAnywhereId = null;
    Integer departureId = null;

    String type = "notify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_notification_rule);
        setTitle(R.string.new_notification_rule);

        //Notification spinners
        arrivalSpinner = findViewById(R.id.arrival_notification_spinner);
        departureSpinner = findViewById(R.id.departure_notification_spinner);

        //Departure spinner
        departureAnywhereSpinner = findViewById(R.id.departure_anywhere_notification_spinner);

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupPlacesViewModel();
        createNotificationChannel();
    }


    private void setupPlacesViewModel() {
        PlacesViewModel viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.getPlaces().observe(this, new Observer<List<PlaceEntry>>() {
            @Override
            public void onChanged(@Nullable List<PlaceEntry> placeEntries) {
                if (placeEntries != null){
                    Log.d("message", "Updating list of places from LiveData in ViewModel"  + placeEntries.size() );
                    if (placeEntries.size() == 0) {
                        return;
                    }
                    placeEntries.size();
                    placeIds.clear();
                    placeNames.clear();
                    placeNamesAnywhere.clear();

                    for (int i = 0; i < placeEntries.size(); i++) {
                        placeIds.add(placeEntries.get(i).getId());
                        System.out.println("placeIds" + i + placeIds.get(i));
                        placeNames.add(placeEntries.get(i).getPlaceName());
                        System.out.println("placeNames" + i + placeNames.get(i));
                    }


                    Log.d("placeNames", " " + placeNames.get(0));
                    placeNamesAnywhere.addAll(placeNames);
                    placeNamesAnywhere.add(0, getString(R.string.anywhere));
                    Log.d("placeNames", " " + placeNames.get(0));


                    ArrayAdapter<String> adapterDepartureAnywherePlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            R.layout.spinner_item_drop,
                            placeNamesAnywhere
                    );

                    ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            R.layout.spinner_item_drop,
                            placeNames
                    );


                    // Specify the layout to use when the list of choices appears
                    adapterPlace.setDropDownViewResource(R.layout.spinner_item);
                    adapterDepartureAnywherePlace.setDropDownViewResource(R.layout.spinner_item);
                    // Apply the adapter to the spinner
                    arrivalSpinner.setAdapter(adapterPlace);
                    departureAnywhereSpinner.setAdapter(adapterDepartureAnywherePlace);
                    departureSpinner.setAdapter(adapterPlace);

                    arrivalSpinner.setOnItemSelectedListener(new ArrivalSpinnerClass());
                    departureSpinner.setOnItemSelectedListener(new DepartureSpinnerClass());
                    departureAnywhereSpinner.setOnItemSelectedListener(new DepartureAnywhereSpinnerClass());
                }


            }
        });
    }

    public void onNotificationRuleDepartureClicked(View view) {
        final RuleEntry ruleEntry = new RuleEntry(null, departureId, null,null, type, false);
        Log.d("rules entred notify", "r " + departureId  + type);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // Insert new rule

                List<RuleEntry> ruleEntries = mDb.ruleDao().findRulesForDeparturePlace(departureId);
                boolean update = false;

                if(ruleEntries != null && ruleEntries.size() > 0){

                    for (RuleEntry rule : ruleEntries) {

                        if (rule.getArrivalId() == null && rule.getType().equals(type)) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.already_saved), Toast.LENGTH_LONG).show();
                                }
                            });
                            update = true;
                            break;
                        }
                    }
                }

                if (!update){
                    mDb.ruleDao().insertRule(ruleEntry);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.rule_saved_toast), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }});
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

    public void onNotificationArrivalButtonClicked(View view) {
        final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureAnywhereId, null, null, type, false);
        Log.d("rules entred notify", "r " + arrivalId + departureAnywhereId + type);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // Insert new rule

                List<RuleEntry> ruleEntries = mDb.ruleDao().findRulesForArrivalPlace(arrivalId);
                boolean update = false;

                if (departureAnywhereId == null) {


                    if(ruleEntries != null && ruleEntries.size() > 0){

                        for (RuleEntry rule : ruleEntries) {

                            if (rule.getType().equals(type) && rule.getDepartureId() == null) {
                                update = true;
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.already_saved), Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            }
                        }
                    }

                    if (!update){
                        mDb.ruleDao().insertRule(ruleEntry);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getString(R.string.rule_saved_toast), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } else {


                    for (RuleEntry rule : ruleEntries) {

                        if (rule.getDepartureId() != null) {
                            int depId = rule.getDepartureId();


                            if (depId == departureAnywhereId && (rule.getType().equals(type))) {
                                update = true;
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getString(R.string.already_saved), Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            }
                        }

                    }

                    if (!update) {
                        mDb.ruleDao().insertRule(ruleEntry);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getString(R.string.rule_saved_toast), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            }});
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

    class ArrivalSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {  arrivalId = placeIds.get(position);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            arrivalId = null;
        }
    }

    class DepartureAnywhereSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        { if (position == 0){
            departureAnywhereId = null;
        } else {
            departureAnywhereId = placeIds.get(position - 1);}
        }
        public void onNothingSelected(AdapterView<?> parent) {
            departureAnywhereId = null;
        }
    }

    class DepartureSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {  departureId = placeIds.get(position);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            departureId = null;
        }
    }

    private void createNotificationChannel() {
        /***
        * Create the NotificationChannel, but only on API 26+
        *(the NotificationChannel class is new and not in the support library)
        ***/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            /***
             * Register the channel with the system
             *(I can't change the importance or other notification behaviors after this)
             ***/
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}