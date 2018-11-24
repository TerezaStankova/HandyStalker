package com.example.android.handystalker.geofencing;
//https://github.com/googlesamples/android-Geofencing/blob/master/Application/src/main/java/com/example/android/wearable/geofencing/SimpleGeofenceStore.java


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class GeofenceStorage {

    List<String> placeIds;

    // The SharedPreferences object in which geofences are stored.
    private final SharedPreferences mPrefs;
    // The name of the SharedPreferences.
    private static final String SHARED_PREFERENCES = "SharedPreferences";
    private static final String ISENABLED = "isEnabled";

    /**
     * Create the SharedPreferences storage with private access only.
     */
    public GeofenceStorage(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Returns a stored geofence by its id, or returns null if it's not found.
     * @return A SimpleGeofence defined by its center and radius, or null if the ID is invalid.
     */
    public List<String> getGeofenceIds() {
        // Get the latitude for the geofence identified by id, or INVALID_FLOAT_VALUE if it doesn't
        // exist (similarly for the other values that follow).

        List<String> placeIds;
        Set<String> placeIdsSet;

        placeIdsSet = mPrefs.getStringSet("B", null);


        if (placeIdsSet != null) {
            placeIds = new ArrayList<>(placeIdsSet);
            return placeIds;
        }
        else return null;
    }

    /**
     * Save a geofence.
     * *
     */



    public boolean getIsEnabled() {
        // Get isEnabled, which indicates if the user allows the GeoFences to work

        boolean isEnabled;
        isEnabled = mPrefs.getBoolean(ISENABLED, false);
        return isEnabled;
    }

    public void setIsEnabled(boolean mIsEnabled) {
        // Get isEnabled, which indicates if the user allows the GeoFences to work
        SharedPreferences.Editor prefs = mPrefs.edit();
        prefs.putBoolean(ISENABLED, mIsEnabled);
        prefs.apply();
    }

    public void setGeofence(List<String> placeIds) {
        // Get a SharedPreferences editor instance. Among other things, SharedPreferences
        // ensures that updates are atomic and non-concurrent.
        SharedPreferences.Editor prefs = mPrefs.edit();

        // Creating a hash set using constructor
        //Set<String> placeIdshSet = new HashSet<String>(placeIds);
        Set<String> placeIdsSet = new TreeSet<String>(placeIds);


        // Converting to set using stream
        //Set<String> set = placeIds.stream().collect(Collectors.toSet());
        prefs.remove("B");

        prefs.putStringSet("B", placeIdsSet);

        /*for (int i = 0; i < placeIds.size(); i++) {
            prefs.putString("A", placeIds.get(i));
        }*/

        // Commit the changes.
        prefs.apply();
    }

    /**
     * Remove a flattened geofence object from storage by removing all of its keys.

    public void clearGeofence(String id) {
        SharedPreferences.Editor prefs = mPrefs.edit();
        //prefs.remove(getGeofenceFieldKey(id, KEY_LATITUDE));
        prefs.commit();
    }

    /**
     * Given a Geofence object's ID and the name of a field (for example, KEY_LATITUDE), return
     * the key name of the object's values in SharedPreferences.
     * @param id The ID of a Geofence object.
     * @param fieldName The field represented by the key.
     * @return The full key name of a value in SharedPreferences.
     */
    /*private String getGeofenceFieldKey(String id, String fieldName) {
        //return KEY_PREFIX + "_" + id + "_" + fieldName;
    }*/

}
