package com.example.android.handystalker.geofencing;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;

import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;

import com.example.android.handystalker.ui.MainActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    // Member variable for the Database
    private AppDatabase mDb;
    public final String CHANNEL_ID = "default";

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
            sendSMS(context);
            sendNotification(context);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {


        } else {
            // Log the error.
            Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
            // No need to do anything else
            return;
        }
    }

    //Sends an SMS to the number stated

    protected void sendSMS(Context context) {

        if (android.os.Build.VERSION.SDK_INT < 24 ||
                (android.os.Build.VERSION.SDK_INT >= 24 && (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED))) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+420739007933", null, "Dorazila jsem", null, null);
                //  smsManager.sendTextMessage(number,null,matn,null,null);
            } catch (Exception e) {
            }
        }
    }

    public void openWhatsApp(){
        try {
            String text = "This is a test";// Replace with your message.

            String toNumber = "+420736604152"; // Replace with mobile phone number without +Sign or leading zeros.


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
            //startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendNotification(Context context) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_place_green_24dp)
                .setContentTitle("You arrived!")
                .setContentText("Let your beloved ones know that you are safe.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Safely there"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // Dismiss notification once the user touches it.
        mBuilder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, mBuilder.build());
    }
}