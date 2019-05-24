package com.app.android.handystalker.geofencing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.ui.NewSoundRuleActivity;
import com.app.android.handystalker.utilities.AppExecutors;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/*
* Special THANK YOU! belongs to the creator of The ShushMe project which was used to get better understanding of Geofences
* https://github.com/udacity/AdvancedAndroid_Shushme
* Big thanks for the answers on https://stackoverflow.com/questions/48686772/geofencingapi-is-deprecated
* More info on geofencing: https://developer.android.com/training/location/geofencing#java
*/

public class Geofencing {

    // Constants
    public static final String TAG = Geofencing.class.getSimpleName();

    /*
    * 150 meters
    * for getting best results from your geofences set a minimum radius of 100 meters
    */

    private AppDatabase mDb;

    private static final float GEOFENCE_RADIUS = 150;
    // 10 years
    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000 * 365 * 10;

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
     * Registers the list of Geofences specified in mGeofenceList
     * when the Geofence is triggered     *
     */
    public void registerAllGeofences() {
        // Check that the list has Geofences in it
        if (mGeoClient == null || mGeofenceList == null || mGeofenceList.size() == 0) {
            if(mGeoClient == null) {Log.d("noClient","fail");}
            Log.d("noList","fail");
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
                        // success code
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // fail code
                                Log.d("register","fail "+ mGeofenceList.size());
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

    public void updateGeofencesList(List<Place> places) {
        mGeofenceList = new ArrayList<>();
        Log.d("updateGeofenceList", "regId" + places.size() + places.isEmpty());
        if (places == null || places.size() == 0) return;

        //Only 30 geofences allowed for the user (100 originally)
        if (places.size() > 30) {
            places = places.subList(0, 30);
            Toast.makeText(mContext, mContext.getString(R.string.only_30), Toast.LENGTH_LONG).show();
        }


        for (Place place1 : places) {
            // Read the place information from the DB cursor
            final String placeID = place1.getId();
            final Place place = place1;

            //Context context = getApplicationContext();
            mDb = AppDatabase.getInstance(mContext);

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {



                    String requestId = String.valueOf(mDb.placeDao().findIdByPlaceId(placeID));


                    Log.d("updateGeofenceList", "regId" + placeID);
                    double placeLat = place.getLatLng().latitude;
                    double placeLng = place.getLatLng().longitude;
                    // Build a Geofence object
                    Geofence geofence = new Geofence.Builder()
                            /***
                             *   Set the request ID of the geofence.
                             * This is a string to identify this
                             * geofence.
                             *
                             * RequestIs is ID from database.
                             */
                            .setRequestId(requestId)
                            .setExpirationDuration(GEOFENCE_TIMEOUT)
                            .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();
                    // Add it to the list
                    mGeofenceList.add(geofence);

                }
            });


        }
        //places.release();
    }



    public void updateGeofencesList(List<Place> places, boolean mIsEnabled) {
        mGeofenceList = new ArrayList<>();
        Log.d("updateGeofenceList", "regId" + places.size() + places.isEmpty());
        if (places == null || places.size() == 0) return;
        final boolean register = mIsEnabled;

        //Only 30 geofences allowed for the user (100 originally)
        if (places.size() > 30) {
            places = places.subList(0, 30);
            Toast.makeText(mContext, mContext.getString(R.string.only_30), Toast.LENGTH_LONG).show();
        }

        final Place lastPlace = places.get(places.size() - 1);


        for (Place place1 : places) {
            // Read the place information from the DB cursor
            final String placeID = place1.getId();
            final Place place = place1;




            //Context context = getApplicationContext();
            mDb = AppDatabase.getInstance(mContext);

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {



                    String requestId = String.valueOf(mDb.placeDao().findIdByPlaceId(placeID));


                    Log.d("updateGeofenceList", "regId" + placeID);
                    double placeLat = place.getLatLng().latitude;
                    double placeLng = place.getLatLng().longitude;
                    // Build a Geofence object
                    Geofence geofence = new Geofence.Builder()
                            /***
                             *   Set the request ID of the geofence.
                             * This is a string to identify this
                             * geofence.
                             *
                             * RequestIs is ID from database.
                             */
                            .setRequestId(requestId)
                            .setExpirationDuration(GEOFENCE_TIMEOUT)
                            .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();
                    // Add it to the list
                    mGeofenceList.add(geofence);


                    if (register && (place == lastPlace)) {
                        registerAllGeofences();
                        Log.d("lastOne", "regId" + placeID);
                    }


                }
            });


        }
        //places.release();
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
