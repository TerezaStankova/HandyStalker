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
import android.widget.Toast;

import com.example.android.handystalker.R;
import com.example.android.handystalker.geofencing.GeofenceStorage;
import com.example.android.handystalker.geofencing.Geofencing;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.PlaceBufferResponse;
//import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean mIsEnabled;
    private GeofenceStorage mGeofenceStorage;
    private static Geofencing mGeofencing;
    //private GeoDataClient mGeoDataClient;


    private PlacesClient placesClient;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = getString(R.string.GOOGLE_PLACES_ANDROID_API_KEY);

        if (apiKey.equals("")) {
            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this);

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(this);
        //mGeoDataClient = Places.getGeoDataClient(this);

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
                final List<Place> places = new ArrayList<Place>();

                editor.apply();
                if (isChecked) {

                    final List<String> placeIds = mGeofenceStorage.getGeofenceIds();
                    if (placeIds == null || placeIds.size() < 1) {return;}

                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    count = 1;

                    for (int i = 0; i < placeIds.size(); i++){

                        // Construct a request object, passing the place ID and fields array.
                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeIds.get(i), placeFields)
                                .build();

                        placesClient.fetchPlace(request).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<FetchPlaceResponse> response) {
                                if (response.isSuccessful()) {
                                    Place place = response.getResult().getPlace();

                                    Log.i(TAG, "Place found: " + place.getName() + places.size());
                                    places.add(place);
                                    Log.i(TAG, "Place size: " + places.size());

                                    if (count == placeIds.size()) {
                                        Log.i(TAG, "Place size2: " + places.size());
                                        mGeofencing.updateGeofencesList(places);
                                        if (mIsEnabled) mGeofencing.registerAllGeofences();
                                    }
                                } else {
                                    Log.i(TAG, "Place size: " + places.size());
                                    Log.e(TAG, "Place not found.");
                                }
                                count += 1;
                            }
                        });
                    }
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
