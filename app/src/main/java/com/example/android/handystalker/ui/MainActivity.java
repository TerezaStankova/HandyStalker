package com.example.android.handystalker.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.Switch;

import com.example.android.handystalker.geofencing.Geofencing;
import com.example.android.handystalker.R;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private boolean mIsEnabled;
    private Geofencing mGeofencing;

    private GeofencingClient mGeoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mPlacesButton = (Button)findViewById(R.id.places_button);
        Button mContactsButton = (Button)findViewById(R.id.contacts_button);
        Button mRulesButton = (Button)findViewById(R.id.rules_button);

        // Initialize the switch state and Handle enable/disable switch change
        Switch onOffSwitch = (Switch) findViewById(R.id.enable_switch);
        mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;
                editor.commit();
                if (isChecked) mGeofencing.registerAllGeofences();
                else mGeofencing.unRegisterAllGeofences();
            }
        });

        mGeoClient = LocationServices.getGeofencingClient(this);

        mGeofencing = new Geofencing(this, mGeoClient);
    }

      /** Called when the user taps the Place button */
    public void onPlaceButtonClicked(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Place button */
    public void onContactsButtonClicked(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Place button */
    public void onRulesButtonClicked(View view) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

}
