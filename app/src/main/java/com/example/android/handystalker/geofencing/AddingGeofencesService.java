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
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;


public class AddingGeofencesService extends IntentService implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "AddingGeofencesService";

    private Geofencing mGeofencing;
    private GeoDataClient mGeoDataClient;
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
        mGeoDataClient = Places.getGeoDataClient(this);

        Log.d(TAG, "handling service" + mIsEnabled);
        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(getApplicationContext());
        mGeofencing = new Geofencing(getApplicationContext(), mGeoClient);

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(this);

        List<String> placeIds;
        placeIds = mGeofenceStorage.getGeofenceIds();

        mIsEnabled = mGeofenceStorage.getIsEnabled();
        Log.d(TAG, "handling service" + mIsEnabled);

        if (placeIds != null && placeIds.size() > 0) {

            mGeoDataClient.getPlaceById(placeIds.toArray(new String[placeIds.size()])).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
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
            });

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