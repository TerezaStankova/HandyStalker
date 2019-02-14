package com.example.android.handystalker.geofencing;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GPSGeofencing {

    private static final String TAG = "GPSGeofencing";
    private Context mContext;

    private Geofencing mGeofencing;
    //private GeoDataClient mGeoDataClient;

    private PlacesClient placesClient;
    private int count;

    private static boolean mIsEnabled;
    // Persistent storage for geofences.
    private GeofenceStorage mGeofenceStorage;
    private List<SimpleGeofence> mSimpleGeofenceList;

    public GPSGeofencing(Context context) {
        mContext = context;
        mSimpleGeofenceList = new ArrayList<>();
    }

    public void isGeofenceTrigerred(Location currentLocation){

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(mContext);

        mIsEnabled = mGeofenceStorage.getIsEnabled();
        if (!mIsEnabled) {
            Log.d("GPS", "handling service" + mIsEnabled);
        return;
        }


        Log.d("GPS", "handling service" + mIsEnabled);

        final List<String> placeIds;
        placeIds = mGeofenceStorage.getGeofenceIds();

        //Latitutes for LatLng
        final List<String> placeLatitudes;
        placeLatitudes = mGeofenceStorage.getTemporaryLatitudes();

        final List<String> placeLongitudes;
        placeLongitudes = mGeofenceStorage.getTemporaryLongitudes();


        if (placeIds != null && placeLongitudes != null && placeLatitudes != null && placeIds.size() > 0) {

            if (placeIds.size() != placeLongitudes.size() || placeLongitudes.size() != placeLatitudes.size()) {
                Log.d("GPS", "handling service gps not same size" + mIsEnabled);
                return;
            }

            String triggeredGeofenceId;

            for (int i = 0; i < placeIds.size(); i++){

                float[] dist = new float[1];
                String[] latlong =  placeLatitudes.get(0).split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);

                Location.distanceBetween(currentLocation.getLatitude(),currentLocation.getLongitude(), latitude, longitude, dist);
                if(dist[0] <= 150){
                    //here your code or alert box for inside 150 m radius area
                    triggeredGeofenceId = placeIds.get(i);
                }


            }
        }
    }

}
