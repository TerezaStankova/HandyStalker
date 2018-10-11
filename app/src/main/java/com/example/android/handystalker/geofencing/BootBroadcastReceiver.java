package com.example.android.handystalker.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, AddingGeofencesService.class);
        context.startService(startServiceIntent);
    }
}
