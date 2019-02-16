package com.example.android.handystalker.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.PlaceBufferResponse;
//import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private boolean mIsEnabled;
    private GeofenceStorage mGeofenceStorage;
    private static Geofencing mGeofencing;


    private PlacesClient placesClient;
    private int count;
    private Switch onOffSwitch;
    private static final int  REQUEST_CHECK_SETTINGS = 15;


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
        onOffSwitch = findViewById(R.id.enable_switch2);
        setCheckedPrivacy(onOffSwitch);
    }

    private void setCheckedPrivacy(Switch onOffSwitch){
        onOffSwitch.setChecked(mIsEnabled && checkHighAccuracyLocationMode(getApplicationContext()));
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;
                mGeofenceStorage.setIsEnabled(mIsEnabled);
                final List<Place> places = new ArrayList<Place>();

                editor.apply();
                if (isChecked) {

                    if (!checkHighAccuracyLocationMode(getApplicationContext())) {

                        Toast.makeText(MainActivity.this, getString(R.string.location_off), Toast.LENGTH_SHORT).show();
                        createLocationRequest();
                        return;
                    }

                    final List<String> placeIds = mGeofenceStorage.getGeofenceIds();
                    if (placeIds == null || placeIds.size() < 1) {return;}
                    if (placeIds.size() > 100) {Toast.makeText(MainActivity.this, getString(R.string.only_100), Toast.LENGTH_SHORT).show(); return;}

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

    public static boolean checkHighAccuracyLocationMode(Context context) {
        int locationMode = 0;
        String locationProviders;
        Log.i(TAG, "Mode loc: check ");

        if (Build.VERSION.SDK_INT >= 28) {
            Log.i(TAG, "Mode loc: 28");
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = lm.isLocationEnabled();
            boolean GPSProviderEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean NetProviderEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return GPSProviderEnabled && NetProviderEnabled;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Log.i(TAG, "Mode loc: 27");
            //Equal or higher than API 19/KitKat
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                Log.i(TAG, "Mode loc: " + locationMode);
                if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                    return true;
                }
            } catch (Settings.SettingNotFoundException e) {
                Log.i(TAG, "Mode loc: ");
                e.printStackTrace();
            }
        }else{
            //Lower than API 19
            Log.i(TAG, "Mode loc: 19");
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)){
                return true;
            }
        }
        Log.i(TAG, "Mode loc: false ");
        return false;
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied.
                setCheckedPrivacy(onOffSwitch);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        //setCheckedPrivacy(onOffSwitch);
                        Toast.makeText(MainActivity.this, getString(R.string.enable_high_accuracy), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==  REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, getString(R.string.geo_enabled), Toast.LENGTH_SHORT).show();
                setCheckedPrivacy(onOffSwitch);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(MainActivity.this, getString(R.string.not_working), Toast.LENGTH_SHORT).show();
                onOffSwitch.setChecked(false);
            }
        }

    }
}
