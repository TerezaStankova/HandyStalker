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

public class NewHandyRuleActivity extends AppCompatActivity {

    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_WIFI = 1234;
    private static final int MY_PERMISSIONS_REQUEST_SEND_NOTIFICATIONS = 1110;

    // Member variable for the Database
    private AppDatabase mDb;

    private String WIFI = "wifi";


    //edit texts
    Spinner onOfWifiSpinner;
    Spinner onOfSoundSpinner;
    Spinner soundPlaceSpinner;
    Spinner wifiPlaceSpinner;

    Integer soundPlaceId = null;
    Integer wifiPlaceId = null;
    Integer contactId = null;
    String type = WIFI;

    private boolean onWifi = true;
    private boolean onSound = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_handy_rule);
        setTitle("New Handy Rule");

        //on-of spinners
        onOfWifiSpinner = (Spinner) findViewById(R.id.on_wifi_spinner);
        onOfSoundSpinner = (Spinner) findViewById(R.id.sound_on_spinner);

        //place spinners
        soundPlaceSpinner = (Spinner) findViewById(R.id.place_sound_spinner);
        wifiPlaceSpinner = (Spinner) findViewById(R.id.place_wifi_spinner);

        setupTypeSpinner();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupPlacesViewModel();
    }

    public void onSaveWifiRuleClick(View view) {

        if (onWifi) {type = WIFI;} else {
            String WIFIOFF = "wifioff";
            type = WIFIOFF;}

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CHANGE_WIFI_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                        MY_PERMISSIONS_REQUEST_WIFI);

                // MY_PERMISSIONS_REQUEST_SEND_SMS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

                final RuleEntry ruleEntry = new RuleEntry(wifiPlaceId, wifiPlaceId, contactId, type, onWifi);
                Log.d("rules entred", "r " + soundPlaceId + wifiPlaceId + contactId + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // insert new contact
                        mDb.ruleDao().insertRule(ruleEntry);

                    }
                });

            Intent intent = new Intent(this, HandyRulesActivity.class);
            startActivity(intent);
        }
    }

    public void onSaveSoundRuleClick(View view) {
        if (onSound) {
            String SOUND = "sound";
            type = SOUND;} else {
            String SOUNDOFF = "soundoff";
            type = SOUNDOFF;}

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted
        if (android.os.Build.VERSION.SDK_INT >= 24 && !notificationManager.isNotificationPolicyAccessGranted()) {

            // Permission is not granted
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                }
                startActivity(intent);

        } else {
            // Permission has already been granted

            final RuleEntry ruleEntry = new RuleEntry(soundPlaceId, soundPlaceId, contactId, type, onSound);
            Log.d("rules entred", "r " + soundPlaceId + contactId + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // insert new contact
                    mDb.ruleDao().insertRule(ruleEntry);
                }
            });

            Intent intent = new Intent(this, HandyRulesActivity.class);
            startActivity(intent);
        }
    }



    public void saveRule(){
        if (type.equals("wifi")) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CHANGE_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CHANGE_WIFI_STATE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                            MY_PERMISSIONS_REQUEST_WIFI);

                    // MY_PERMISSIONS_REQUEST_SEND_SMS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                final RuleEntry ruleEntry = new RuleEntry(wifiPlaceId, wifiPlaceId, contactId, type, onWifi);
                Log.d("rules entred", "r " + soundPlaceId + wifiPlaceId + contactId + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // insert new contact
                        mDb.ruleDao().insertRule(ruleEntry);

                    }
                });

                Intent intent = new Intent(this, HandyRulesActivity.class);
                startActivity(intent);
            }
        } else if (type.equals("sound")) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Check if the API supports such permission change and check if permission is granted
        if (android.os.Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted()) {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            }
            startActivity(intent);
        } else {
            // Permission has already been granted
            final RuleEntry ruleEntry = new RuleEntry(soundPlaceId, soundPlaceId, contactId, type, onSound);
            Log.d("rules entred", "r " + soundPlaceId + contactId + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // insert new contact
                    mDb.ruleDao().insertRule(ruleEntry);

                }
            });

            Intent intent = new Intent(this, HandyRulesActivity.class);
            startActivity(intent);
        }
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
                    // permission was granted
                    saveRule();
                } else {
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void setupTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.on_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        onOfWifiSpinner.setAdapter(adapter);
        onOfSoundSpinner.setAdapter(adapter);


        onOfWifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen arrival
                        onWifi = true;
                        break;
                    case 1:
                        // Chosen departure
                        onWifi = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                onWifi = true;
            }
        });


        onOfSoundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen arrival
                        onSound = true;
                        break;
                    case 1:
                        // Chosen departure
                        onSound = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                onSound = true;
            }
        });
    }


    private void setupPlacesViewModel() {
        PlacesViewModel viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.getPlaces().observe(this, new Observer<List<PlaceEntry>>() {
            @Override
            public void onChanged(@Nullable List<PlaceEntry> placeEntries) {
                Log.d("message", "Updating list of places from LiveData in ViewModel"  + placeEntries.size() );
                if (placeEntries.size() == 0) {
                    return;
                }
                if (placeEntries != null && placeEntries.size() != 0) {
                    placeIds.clear();
                    placeNames.clear();

                    for (int i = 0; i < placeEntries.size(); i++) {
                        placeIds.add(placeEntries.get(i).getId());
                        System.out.println("placeIds" + i + placeIds.get(i));
                        placeNames.add(placeEntries.get(i).getPlaceName());
                        System.out.println("placeNames" + i + placeNames.get(i));
                    }

                    ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            placeNames
                    );
                    // Specify the layout to use when the list of choices appears
                    adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    soundPlaceSpinner.setAdapter(adapterPlace);
                    wifiPlaceSpinner.setAdapter(adapterPlace);
                }

                soundPlaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                        soundPlaceId = placeIds.get(position);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        soundPlaceId = null;
                    }
                });

                wifiPlaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        wifiPlaceId = placeIds.get(position);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        wifiPlaceId = null;
                    }
                });
            }
        });
    }
}
