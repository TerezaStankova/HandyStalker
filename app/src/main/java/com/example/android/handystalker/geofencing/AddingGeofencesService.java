package com.example.android.handystalker.geofencing;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

/**import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
 **/

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

public class AddingGeofencesService extends IntentService implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "AddingGeofencesService";

    private Geofencing mGeofencing;
    //private GeoDataClient mGeoDataClient;

    private PlacesClient placesClient;

    private static boolean mIsEnabled;
    // Persistent storage for geofences.
    private GeofenceStorage mGeofenceStorage;

    public AddingGeofencesService() {
        super(TAG);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void setmIsEnabled(boolean isEnabled){
        mIsEnabled = isEnabled;
    }


    //TODO: resolve this

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

        //Intent intent1;
        //intent1 = intent.getParcelableExtra("UserID");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //mGeoDataClient = Places.getGeoDataClient(this);
        placesClient = Places.createClient(this);

        Log.d(TAG, "handling service" + mIsEnabled);
        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(getApplicationContext());
        mGeofencing = new Geofencing(getApplicationContext(), mGeoClient);

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(this);

        List<String> placeIds;
        placeIds = mGeofenceStorage.getGeofenceIds();

        mIsEnabled = mGeofenceStorage.getIsEnabled();
        Log.d(TAG, "handling service" + mIsEnabled);
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        if (placeIds != null && placeIds.size() > 0) {

            /*mGeoDataClient.getPlaceById(placeIds.toArray(new String[placeIds.size()])).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        mGeofencing.updateGeofencesList(places);
                        if (mIsEnabled) mGeofencing.registerAllGeofences();
                        places.release(); // release in Geofencing? Adapter not responding - changed Adapter no Adress
                    } else {
                        Log.e(TAG, "Place not found.");
                    }
                }
            });*/


            for (int i = 0; i < placeIds.size(); i++){

                // Construct a request object, passing the place ID and fields array.
                FetchPlaceRequest request = FetchPlaceRequest.builder(placeIds.get(i), placeFields)
                        .build();

                placesClient.fetchPlace(request).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FetchPlaceResponse> response) {
                        if (response.isSuccessful()) {
                            Place place = response.getResult().getPlace();

                            Log.i(TAG, "Place found: " + place.getName());
                            mGeofencing.updateGeofencesList(place);
                            if (mIsEnabled) mGeofencing.registerAllGeofences();
                            //places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });


            }

                   /* placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();
                        Log.i(TAG, "Place found: " + place.getName());
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    });*/


        }
    }



    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        //com.example.android.handystalker.ui.PlacesActivity
        //Add geofences
      /*  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mIsEnabled = prefs.getBoolean(getString(R.string.setting_enabled), false);
        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(this);
        mGeofencing = new Geofencing(this, mGeoClient);
        if (mIsEnabled) mGeofencing.registerAllGeofences();*/

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.d(TAG, "Connection Suspended");

    }
}