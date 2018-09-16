package com.example.android.handystalker.geofencing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.handystalker.utilities.GeofenceTransitionsIntentService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/*
* Special THANK YOU! belongs to the creator of The ShushMe project which was used to get better understanding of Geofences
* https://github.com/udacity/AdvancedAndroid_Shushme
* Big thanks for the answers on https://stackoverflow.com/questions/48686772/geofencingapi-is-deprecated
* More info on geofencing: https://developer.android.com/training/location/geofencing#java
*/

public class Geofencing {

    // Constants
    public static final String TAG = Geofencing.class.getSimpleName();

    private static final float GEOFENCE_RADIUS = 300; // 300 meters
    //for getting best results from your geofences set a minimum radius of 100 meters

    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000 * 365 * 10; // 10 years

    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GeofencingClient mGeoClient;
    private Context mContext;

    public Geofencing(Context context, GeofencingClient client) {
        mContext = context;
        mGeoClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    /***
     * Registers the list of Geofences specified in mGeofenceList with Google Place Services
     * when the Geofence is triggered     *
     */
    public void registerAllGeofences() {
        // Check that the list has Geofences in it
        if (mGeoClient == null || mGeofenceList == null || mGeofenceList.size() == 0) {
            Log.d("noList","fail" + mGeofenceList.size());
            return;
        }
            try {
                mGeoClient.addGeofences(

                        //Get the list of Geofences to be registered
                        getGeofencingRequest(),

                        //Get the pending intent to launch the IntentService
                        getGeofencePendingIntent()
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("register","success"+ mGeofenceList.size());
                        // your success code
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // your fail code;
                                Log.d("register","fail"+ mGeofenceList.size());
                            }
                        });
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                Log.e(TAG, securityException.getMessage());
            }
    }

    /***
     * Unregisters all the Geofences
     */
    public void unRegisterAllGeofences() {
        // Check that there is GeoClient
        if (mGeoClient == null) {
            return;}


        try {
            Log.d("GeofencesRemove", "begin" + mGeofenceList.size());
            mGeoClient.removeGeofences(
                    // This is the same pending intent that was used in registerGeofences
                    getGeofencePendingIntent()
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Geofences removed
                    Log.d("GeofencesRemove", "success" + mGeofenceList.size());
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to remove geofences
                            Log.d("GeofencesRemove", "fail");
                        }
                    });
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, securityException.getMessage());
        }
    }


    /***
     * Updates the local ArrayList of Geofences
     * Uses the Place ID defined by the API as the Geofence object Id
     *
     * @param places the PlaceBufferResponse result of the getPlaceById call
     */
    public void updateGeofencesList(PlaceBufferResponse places) {
        mGeofenceList = new ArrayList<>();
        if (places == null || places.getCount() == 0) return;
        for (Place place : places) {
            // Read the place information from the DB cursor
            String placeUID = place.getId();
            Log.d("updateGeofenceList", "regId" + placeUID);
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;
            // Build a Geofence object
            Geofence geofence = new Geofence.Builder()
                    /***
                     *   Set the request ID of the geofence.
                     * This is a string to identify this
                     * geofence.
                    */
                    .setRequestId(placeUID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            // Add it to the list
            mGeofenceList.add(geofence);
        }
    }

    /***
     * Specify the geofences to monitor and to set how related geofence events are triggered
     *
     * INITIAL_TRIGGER_ENTER is used to initialize an action immediately.
     * INITIAL_TRIGGER_DWELL would trigger events only when the user
     * stops for a defined duration within a geofence.
     *
     * @return the GeofencingRequest object
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    /***
     * Creates a PendingIntent object used
     * to register and unregister Geofences
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().

        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }
}
