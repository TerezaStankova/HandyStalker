package com.example.android.handystalker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.android.handystalker.R;
import com.example.android.handystalker.geofencing.GeofenceStorage;
import com.example.android.handystalker.geofencing.Geofencing;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean mIsEnabled;
    private GeofenceStorage mGeofenceStorage;
    private static Geofencing mGeofencing;
    private GeoDataClient mGeoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(this);
        mGeoDataClient = Places.getGeoDataClient(this);

        mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
        Log.d("Preference","getPref" + mIsEnabled);

        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(getApplicationContext());
        mGeofencing = new Geofencing(getApplicationContext(), mGeoClient);


        // Initialize the switch state and Handle enable/disable switch change
        Switch onOffSwitch = findViewById(R.id.enable_switch2);
        setCheckedPrivacy(onOffSwitch);
    }

    private void setCheckedPrivacy(Switch onOffSwitch){
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;

                //TODO: is it right?
                //AddingGeofencesService.setmIsEnabled(mIsEnabled);
                mGeofenceStorage.setIsEnabled(mIsEnabled);

                editor.apply();
                if (isChecked) {
                    List<String> placeIds = mGeofenceStorage.getGeofenceIds();

                    mGeoDataClient.getPlaceById(placeIds.toArray(new String[placeIds.size()])).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                            if (task.isSuccessful()) {
                                PlaceBufferResponse places = task.getResult();
                                mGeofencing.updateGeofencesList(places);
                                mGeofencing.registerAllGeofences();
                                places.release();
                            } else {
                                Log.e(TAG, "Place not found.");
                            }
                        }
                    });
                }
                else mGeofencing.unRegisterAllGeofences();
            }});
    }

      /** Called when the user taps the Place button */
    public void onPlaceButtonClicked(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }


    /** Called when the user taps the Rules button */
    public void onRulesButtonClicked(View view) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }
}
