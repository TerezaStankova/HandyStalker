package com.example.android.handystalker.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.handystalker.utilities.GeofenceTransitionsIntentService;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent startServiceIntent = new Intent(context, AddingGeofencesService.class);
        //context.startService(startServiceIntent);
        // Enqueues a JobIntentService passing the context and intent as parameters
        AddingGeofencesService.enqueueWork(context, intent);
    }
}
