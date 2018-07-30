package com.example.android.handystalker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.telephony.SmsManager;
import android.util.Log;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     *
     * @param context
     * @param intent
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the Geofence Event from the Intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        // Check which transition type has triggered this event
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            sendSMS();
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
        } else {
            // Log the error.
            Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
            // No need to do anything else
            return;
        }
    }

    //Sends an SMS to the number stated

    protected void sendSMS() {

        //Edittext
        //phoneNo = txtphoneNo.getText().toString();
        //message = txtMessage.getText().toString();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+420739007933", null, "Dorazila jsem", null, null);
    }
}