package com.app.android.handystalker.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class ProviderChangedBroadcastReceiver extends BroadcastReceiver {
    boolean isGpsEnabled;
    boolean isNetworkEnabled;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
        {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null){

            //  gps –> (GPS, AGPS)
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //network –> (AGPS, CellID, WiFi MACID)
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}

            //ToDO: Try it only for NetworkEnabled

            if (isGpsEnabled || isNetworkEnabled) {
                AddingGeofencesService.enqueueWork(context, new Intent());
                Log.d("GPS or Network", "onConnected");
            }
        }
    }
}