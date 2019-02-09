package com.example.android.handystalker.geofencing;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

public class AddingGeofencesService extends JobIntentService {

    private static final String TAG = "AddingGeofencesService";

    private Geofencing mGeofencing;
    //private GeoDataClient mGeoDataClient;

    private PlacesClient placesClient;
    private int count;

    private static boolean mIsEnabled;
    // Persistent storage for geofences.
    private GeofenceStorage mGeofenceStorage;

    /*public AddingGeofencesService() {
        super(TAG);
    }/*/

    public static final int JOB_ID = 222;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AddingGeofencesService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //mGeoDataClient = Places.getGeoDataClient(this);
        placesClient = Places.createClient(this);

        Log.d(TAG, "handling service" + mIsEnabled);
        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(getApplicationContext());
        mGeofencing = new Geofencing(getApplicationContext(), mGeoClient);

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(this);

        final List<String> placeIds;
        placeIds = mGeofenceStorage.getGeofenceIds();

        mIsEnabled = mGeofenceStorage.getIsEnabled();
        Log.d(TAG, "handling service" + mIsEnabled);


        if (placeIds != null && placeIds.size() > 0) {

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            final List<Place> places = new ArrayList<Place>();
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
    }
}