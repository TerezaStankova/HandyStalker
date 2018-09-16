package com.example.android.handystalker.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.handystalker.utilities.GeofenceTransitionsIntentService;

/**
 * Thank you for solving the issue with triggering events at https://github.com/googlesamples/android-play-location/commit/5f83047c8a462d7c619f6275b624e219b4622322
 * Receiver for geofence transition changes.
 * <p>
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a JobIntentService
 * that will handle the intent in the background.
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    /**
     * Receives incoming intents.
     *
     * @param context the application context.
     * @param intent  sent by Location Services. This Intent is provided to Location
     *                Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        GeofenceTransitionsIntentService.enqueueWork(context, intent);
    }
}