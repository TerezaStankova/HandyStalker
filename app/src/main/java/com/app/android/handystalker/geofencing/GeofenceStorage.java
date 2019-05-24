package com.app.android.handystalker.geofencing;
/**This code was inspired by
 * https://github.com/googlesamples/android-Geofencing/blob/master/Application/src/main/java/com/
 * example/android/wearable/geofencing/SimpleGeofenceStore.java
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class GeofenceStorage {

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


    public List<String> getGeofenceIds() {

        List<String> placeIds;
        Set<String> placeIdsSet;

        placeIdsSet = mPrefs.getStringSet("B", null);


        if (placeIdsSet != null) {
            placeIds = new ArrayList<>(placeIdsSet);
            return placeIds;
        }
        else return null;
    }



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
        // Get a SharedPreferences editor instance.
        SharedPreferences.Editor prefs = mPrefs.edit();
        Set<String> placeIdsSet = new TreeSet<String>(placeIds);
        prefs.remove("B");
        prefs.putStringSet("B", placeIdsSet);
        // Commit the changes.
        prefs.apply();
    }

}
