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

public class NewSoundRuleActivity extends AppCompatActivity {

    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_WIFI = 1234;

    // Member variable for the Database
    private AppDatabase mDb;
    
    List<String> placeNamesAnywhere = new ArrayList<String>();

    //edit texts
    Spinner arrivalSpinner;
    Spinner departureSpinner;
    Spinner departureAnywhereSpinner;

    Integer arrivalId = null;
    Integer departureAnywhereId = null;
    Integer departureId = null;


    //spinners
    Spinner onOfSoundSpinner;
    Spinner onOfSoundSpinnerDeparture;
    Integer contactId = null;

    private boolean onSound = true; 
    private boolean onSoundDeparture = true;

    private boolean arrival = true;

    private String SOUND = "sound";
    private String SOUNDOFF = "soundoff";

    String type = SOUND;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_sound_rule);
        setTitle("New Sound Rule");

        //place spinner
        arrivalSpinner = findViewById(R.id.arrival_sound_spinner);
        departureSpinner = findViewById(R.id.departure_anywhere_sound_spinner);
        departureAnywhereSpinner = findViewById(R.id.departure_sound_spinner);

        //on-of spinner
        onOfSoundSpinner = findViewById(R.id.sound_on_spinner_arrival);
        onOfSoundSpinnerDeparture = findViewById(R.id.sound_on_spinner_departure);

        mDb = AppDatabase.getInstance(getApplicationContext());

        setupTypeSpinner();
        setupPlacesViewModel();
    }


    public void onSaveSoundRuleClick(View view) {
        if (onSound) {
            type = SOUND;} else {
            type = SOUNDOFF;}

        arrival = true;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted
        if (android.os.Build.VERSION.SDK_INT >= 24 && (notificationManager!= null)&& !notificationManager.isNotificationPolicyAccessGranted()) {

            // Permission is not granted
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            }
            startActivity(intent);

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

            Intent intent = new Intent(this, SoundRulesActivity.class);
            startActivity(intent);
        }
    }

    public void onSaveSoundRuleDepartureClick(View view) {
        if (onSound) {
            type = SOUND;} else {
            type = SOUNDOFF;}

            arrival = false;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted
        if (android.os.Build.VERSION.SDK_INT >= 24 && (notificationManager!= null)&& !notificationManager.isNotificationPolicyAccessGranted()) {

            // Permission is not granted
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            }
            startActivity(intent);

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



    private void setupTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.on_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        // Apply the adapter to the spinner
        onOfSoundSpinner.setAdapter(adapter);
        onOfSoundSpinnerDeparture.setAdapter(adapter);

        onOfSoundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen ON
                        onSound = true;
                        break;
                    case 1:
                        // Chosen OFF
                        onSound = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                onSound = true;
            }
        });

        onOfSoundSpinnerDeparture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen ON
                        onSoundDeparture = true;
                        break;
                    case 1:
                        // Chosen OFF
                        onSoundDeparture = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                onSoundDeparture = true;
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
