package com.example.android.handystalker.geofencing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.example.android.handystalker.R;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
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

public class AddingGeofencesService extends JobIntentService {

    private static final String TAG = "AddingGeofencesService";

    private Geofencing mGeofencing;
    //private GeoDataClient mGeoDataClient;

    private int count;

    private static boolean mIsEnabled;

    public static final int JOB_ID = 222;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AddingGeofencesService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String apiKey = getString(R.string.GOOGLE_PLACES_ANDROID_API_KEY);

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        //mGeoDataClient = Places.getGeoDataClient(this);
        PlacesClient placesClient = Places.createClient(this);

        Log.d(TAG, "handling service" + mIsEnabled);
        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(getApplicationContext());
        mGeofencing = new Geofencing(getApplicationContext(), mGeoClient);

        // Instantiate a new geofence storage area.
        // Persistent storage for geofences.
        GeofenceStorage mGeofenceStorage = new GeofenceStorage(this);

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