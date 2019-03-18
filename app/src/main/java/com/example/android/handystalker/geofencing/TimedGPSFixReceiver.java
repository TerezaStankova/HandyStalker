package com.example.android.handystalker.geofencing;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import com.example.android.handystalker.utilities.ToastLog;

import java.util.Locale;

//Credit: https://github.com/bsautermeister/GeoFencer/blob/master/app/src/main/java/de/bsautermeister/geofencer/geo/TimedGPSFixReceiver.java

public class TimedGPSFixReceiver extends BroadcastReceiver {
    private static final String TAG = "TimedGPSFixReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {

            pollUsingLocationManger(context);
    }

    private void notifyLocationUpdated(Location location, Context context) {
        Location home = location;
        double distance = -1;
        if (home != null)
            distance = home.distanceTo(location);

        String accuracyString = (location.hasAccuracy() ? String.valueOf(location.getAccuracy()) : "?");
        String message = String.format(Locale.getDefault(), "Accuracy: %s Distance: %.2f", accuracyString, distance);
        ToastLog.logLong(context, TAG, message);
    }


    private void pollUsingLocationManger(final Context context) {
        ToastLog.logShort(context, TAG, "Timed fix: LocationManager");

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    notifyLocationUpdated(location, context);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            }, null);
        } catch (SecurityException sex) {
            ToastLog.logLong(context, TAG, "SecurityException: " + sex.getMessage());
        }
    }

    public static void start(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimedGPSFixReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 60000L, 60000L, pi);
    }

    public static void stop(Context context)
    {
        Intent intent = new Intent(context, TimedGPSFixReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}