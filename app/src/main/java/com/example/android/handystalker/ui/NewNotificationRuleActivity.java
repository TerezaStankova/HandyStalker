package com.example.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.utilities.AppExecutors;
import com.example.android.handystalker.utilities.ContactsViewModel;
import com.example.android.handystalker.utilities.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class NewNotificationRuleActivity extends AppCompatActivity {
    
    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();
    List<String> placeNamesAnywhere = new ArrayList<String>();

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner arrivalSpinner;
    Spinner departureSpinner;
    Spinner departureAnywhereSpinner;

    Spinner contactNameSpinnerNotify;
    Spinner placeNotificationSpinner;

    Integer arrivalId = null;
    Integer departureAnywhereId = null;
    Integer departureId = null;
   
    String type = "notify";
    private boolean arrivalNotificationRule = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_notification_rule);
        setTitle("New Notification Rule");

        //SMS for arrival spinners
        arrivalSpinner = findViewById(R.id.arrival_spinner);
        departureSpinner = findViewById(R.id.departure_spinner);

        //Departure SMS spinners
        departureAnywhereSpinner = findViewById(R.id.departure_place_rule_spinner);

        //Notification spinners
        contactNameSpinnerNotify = findViewById(R.id.name_spinner2);
        placeNotificationSpinner = findViewById(R.id.place_spinner);

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupPlacesViewModel();
    }


    public void onSaveNotifyRuleClick(View view) {
        type = "notify";

        if (arrivalNotificationRule){
            final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureAnywhereId, null, type, false);
            Log.d("rules entred notify", "r " + arrivalId + + departureAnywhereId + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert new rule
                    mDb.ruleDao().insertRule(ruleEntry);

                }});
        } else {
            final RuleEntry ruleEntry = new RuleEntry(null, departureId, null, type, false);
            Log.d("rules entred notify", "r " + departureId  + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert new rule
                    mDb.ruleDao().insertRule(ruleEntry);

                }});
        }
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
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
                    placeNamesAnywhere.add(0, "anywhere");
                    Log.d("placeNames", " " + placeNames.get(0));


                    ArrayAdapter<String> adapterDepartureAnywherePlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            R.layout.spinner_item,
                            placeNamesAnywhere
                    );

                    ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            R.layout.spinner_item,
                            placeNames
                    );


                    // Specify the layout to use when the list of choices appears
                    adapterPlace.setDropDownViewResource(R.layout.spinner_item);
                    adapterDepartureAnywherePlace.setDropDownViewResource(R.layout.spinner_item);
                    // Apply the adapter to the spinner
                    arrivalSpinner.setAdapter(adapterPlace);
                    departureAnywhereSpinner.setAdapter(adapterPlace);
                    departureSpinner.setAdapter(adapterDepartureAnywherePlace);

                    arrivalSpinner.setOnItemSelectedListener(new ArrivalSpinnerClass());
                    departureAnywhereSpinner.setOnItemSelectedListener(new DepartureSpinnerClass());
                    departureSpinner.setOnItemSelectedListener(new DepartureAnywhereSpinnerClass());
                }


            }
        });
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

}