package com.example.android.handystalker.geofencing;

import android.app.IntentService;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.ui.PlacesActivity;
import com.example.android.handystalker.utilities.PlacesViewModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AddingGeofencesService extends IntentService implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "AddingGeofencesService";

    private static Geofencing mGeofencing;

    public AddingGeofencesService() {
        super(TAG);
    }

    public static void setGeofencing(Geofencing geofencing) {
        mGeofencing = geofencing;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //mGeofencing.registerAllGeofences();
    }



    public void onConnected(Bundle bundle) {
        mGeofencing.registerAllGeofences();
        //Add geofences
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}