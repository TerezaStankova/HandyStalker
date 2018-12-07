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

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2222;
    private static final int MY_PERMISSIONS_REQUEST_SEND_NOTIFICATIONS = 1111;

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner typeSpinner;
    Spinner arrivalSpinner;
    Spinner departureSpinner;
    Spinner departureAnywhereSpinner;

    Spinner contactNameSpinnerNotify;
    Spinner placeNotificationSpinner;

    Integer placeId = null;
    Integer arrivalId = null;
    Integer departureId = null;
    Integer departureId2 = null;
   
    String type = "sms";
    private boolean arrivalNotificationRule = true;
    private boolean depRule = false;

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
        typeSpinner = findViewById(R.id.type_spinner);
        contactNameSpinnerNotify = findViewById(R.id.name_spinner2);
        placeNotificationSpinner = findViewById(R.id.place_spinner);

        setupTypeSpinner();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupPlacesViewModel();
    }


    public void onSaveNotifyRuleClick(View view) {
        type = "notify";

        if (arrivalNotificationRule){
            final RuleEntry ruleEntry = new RuleEntry(placeId, null, null, type, false);
            Log.d("rules entred notify", "r " + placeId + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert new rule
                    mDb.ruleDao().insertRule(ruleEntry);

                }});
        } else {
            final RuleEntry ruleEntry = new RuleEntry(null, placeId, null, type, false);
            Log.d("rules entred notify", "r " + placeId  + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert new rule
                    mDb.ruleDao().insertRule(ruleEntry);

                }});
        }
        Intent intent = new Intent(this, SmsRulesActivity.class);
        startActivity(intent);
        }


    private void setupTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen arrival
                        arrivalNotificationRule = true;
                        break;
                    case 1:
                        // Chosen departure
                        arrivalNotificationRule = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                arrivalNotificationRule = true;
            }
        });
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
                    placeNotificationSpinner.setAdapter(adapterPlace);
                    departureSpinner.setAdapter(adapterDepartureAnywherePlace);

                    arrivalSpinner.setOnItemSelectedListener(new ArrivalSpinnerClass());
                    departureSpinner.setOnItemSelectedListener(new DepartureAnywhereSpinnerClass());
                    departureAnywhereSpinner.setOnItemSelectedListener(new DepartureSpinnerClass());
                    placeNotificationSpinner.setOnItemSelectedListener(new PlaceSpinnerClass());
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
            departureId = null;
        } else {
            departureId = placeIds.get(position - 1);}
        }
        public void onNothingSelected(AdapterView<?> parent) {
            departureId = null;
        }
    }

    class DepartureSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {  departureId2 = placeIds.get(position);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            departureId2 = null;
        }
    }

    class PlaceSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {   placeId = placeIds.get(position);
        }
            public void onNothingSelected(AdapterView<?> parent) {
            placeId = null;
        }
    }

    
}