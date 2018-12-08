package com.example.android.handystalker.ui;

import android.Manifest;
import android.app.NotificationManager;
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
import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.utilities.AppExecutors;
import com.example.android.handystalker.utilities.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;

public class NewWifiRuleActivity extends AppCompatActivity {

    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_WIFI = 1234;

    // Member variable for the Database
    private AppDatabase mDb;

    private String WIFI = "wifi";
    private String WIFIOFF = "wifioff";

    List<String> placeNamesAnywhere = new ArrayList<String>();

    //edit texts
    Spinner arrivalSpinner;
    Spinner departureSpinner;
    Spinner departureAnywhereSpinner;

    Integer arrivalId = null;
    Integer departureAnywhereId = null;
    Integer departureId = null;


    //spinners
    Spinner onOfWifiSpinner;
    Spinner onOfWifiSpinnerDeparture;

    Integer contactId = null;
    String type = WIFI;
    private boolean onWifi = true;
    private boolean onWifiDeparture = true;

    private boolean arrival = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_wifi_rule);
        setTitle("New WiFi Rule");

        //place spinner
        arrivalSpinner = findViewById(R.id.arrival_wifi_spinner);
        departureSpinner = findViewById(R.id.departure_wifi_spinner);
        departureAnywhereSpinner = findViewById(R.id.departure_anywhere_wifi_spinner);

        //on-of spinner
        onOfWifiSpinner = findViewById(R.id.on_wifi_spinner);
        onOfWifiSpinnerDeparture = findViewById(R.id.wifi_on_spinner_departure);

        setupTypeSpinner();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupPlacesViewModel();
    }



    public void saveRule(){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CHANGE_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CHANGE_WIFI_STATE)) {
                    // Show an explanation to the user *asynchronously*
                } else {
                    // No explanation needed, request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                            MY_PERMISSIONS_REQUEST_WIFI);
                }
            } else {

                if (arrival) {

                    // Permission has already been granted
                    final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureAnywhereId, contactId, null, type, false);
                    Log.d("rules entred", "r " + arrivalId + contactId + type);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Insert new rule
                            mDb.ruleDao().insertRule(ruleEntry);

                        }
                    });

                } else {
                    final RuleEntry ruleEntry = new RuleEntry(null, departureId, contactId, null, type, false);
                    Log.d("rules entred", "r " + departureId + contactId + type);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // insert new rule
                            mDb.ruleDao().insertRule(ruleEntry);

                        }
                    });
                }


                Intent intent = new Intent(this, WifiRulesActivity.class);
                startActivity(intent);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WIFI: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    saveRule();
                }
            }
        }
    }

    private void setupTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.on_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        // Apply the adapter to the spinner
        onOfWifiSpinner.setAdapter(adapter);
        onOfWifiSpinnerDeparture.setAdapter(adapter);

        onOfWifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen ON
                        onWifi = true;
                        break;
                    case 1:
                        // Chosen OFF
                        onWifi = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                onWifi = true;
            }
        });

        onOfWifiSpinnerDeparture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen ON
                        onWifiDeparture = true;
                        break;
                    case 1:
                        // Chosen OFF
                        onWifiDeparture = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                onWifiDeparture = true;
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
                    departureAnywhereSpinner.setAdapter(adapterDepartureAnywherePlace);
                    departureSpinner.setAdapter(adapterPlace);

                    arrivalSpinner.setOnItemSelectedListener(new ArrivalSpinnerClass());
                    departureSpinner.setOnItemSelectedListener(new DepartureSpinnerClass());
                    departureAnywhereSpinner.setOnItemSelectedListener(new DepartureAnywhereSpinnerClass());
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

    public void onSaveWifiArrivalRuleClick(View view) {

        if (onWifi) {type = WIFI;} else {
            type = WIFIOFF;}

        arrival = true;

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
        } else {
            // Permission has already been granted

            final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureAnywhereId, contactId, null, type, false);
                Log.d("rules entred", "r " +  arrivalId + contactId + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // insert new contact
                        mDb.ruleDao().insertRule(ruleEntry);

                    }
                });

            Intent intent = new Intent(this, WifiRulesActivity.class);
            startActivity(intent);
        }
    }

        public void onSaveWifiDepartureRuleClick(View view) {

        if (onWifiDeparture) {type = WIFI;} else {
            type = WIFIOFF;}

            arrival = false;

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
        } else {
            // Permission has already been granted

                final RuleEntry ruleEntry = new RuleEntry(null, departureId, contactId, null, type, false);
                Log.d("rules entred", "r " + departureId + contactId + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // insert new contact
                        mDb.ruleDao().insertRule(ruleEntry);

                    }
                });

            Intent intent = new Intent(this, WifiRulesActivity.class);
            startActivity(intent);
        }
    }
}
