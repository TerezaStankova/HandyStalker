package com.example.android.handystalker.geofencing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;

import android.media.AudioManager;
import android.net.Uri;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;

import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.model.Contact;
import com.example.android.handystalker.utilities.StalkerService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

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
    public void onReceive(final Context context, Intent intent) {
        // Get the Geofence Event from the Intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }


        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        // Check which transition type has triggered this event

        final List<Geofence> triggeringGeofence = geofencingEvent.getTriggeringGeofences();
        final String[] triggerIds = new String[triggeringGeofence.size()];

        for (int i = 0; i < triggerIds.length; i++) {
            triggerIds[i] = triggeringGeofence.get(i).getRequestId();
        }
        mDb = AppDatabase.getInstance(context);




        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("enter", "entered ");
            final List<String> placeName = new ArrayList<String>();
            final PendingResult pendingResult = goAsync();
            @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, List<String>> asyncTask = new AsyncTask<String, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(String... params){
                    final int arrivalId;

                    String requestId = triggeringGeofence.get(triggeringGeofence.size()-1).getRequestId();

                    Log.d(TAG, "requestId " + requestId);
                    arrivalId = mDb.placeDao().findIdByPlaceId(requestId);
                    Log.d(TAG, "triggeringGeofence.size()" + triggeringGeofence.size() + " " + arrivalId);
                    String namePlace = mDb.placeDao().findPlaceNameById(arrivalId);
                    List<RuleEntry> rulesForThisPlace = mDb.ruleDao().findRulesForArrivalPlace(arrivalId);
                    List<String> phoneNumbers = new ArrayList<String>();
                    ArrayList<Contact> contactsForThisPlace = new ArrayList<Contact>();
                    List<String> contactNames = new ArrayList<String>();


                    if (rulesForThisPlace != null && rulesForThisPlace.size() != 0 ) {
                        Log.d(TAG, "rulesForThisPlace.size= " + rulesForThisPlace.size());
                        List<Integer> rulesIdForThisPlace = mDb.ruleDao().findRulesById(arrivalId);

                        Log.d(TAG, "rulesIdForThisPlace.size= " + rulesIdForThisPlace.size());

                        for (int i = 0; i < rulesForThisPlace.size(); i++) {
                            if (rulesForThisPlace.get(i).getType().equals("sms")) {

                                if (rulesForThisPlace.get(i).getDepartureId() != null){
                                boolean active = rulesForThisPlace.get(i).getActive();
                                if (active){
                                    rulesForThisPlace.get(i).setActive(false);
                                    mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                    placeName.add(mDb.placeDao().findPlaceNameById(arrivalId));
                                    phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));}
                                } else {
                                    placeName.add(mDb.placeDao().findPlaceNameById(arrivalId));
                                    phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                                }
                            } else if (rulesForThisPlace.get(i).getType().equals("notify")){
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                Integer contactId = (Integer) rulesForThisPlace.get(i).getContactId();
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                contactNames.add(mDb.contactDao().findNameForContactId(rulesForThisPlace.get(i).getContactId()));
                                ContactsEntry contactsEntry = mDb.contactDao().findContactsEntryfromContactId(contactId);
                                contactsForThisPlace.add(new Contact(0, contactsEntry.getPhone(), contactsEntry.getName(), null));
                            } else if (rulesForThisPlace.get(i).getType().equals("wifi")) {
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                setWiFi(context, true);
                            } else if (rulesForThisPlace.get(i).getType().equals("wifioff")) {
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                setWiFi(context, false);
                            } else if (rulesForThisPlace.get(i).getType().equals("sound")) {
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                setSound(context, 2);}
                         else if (rulesForThisPlace.get(i).getType().equals("soundoff")) {
                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                            setSound(context, 0);}
                        }
                    }

                    Log.d(TAG, "async finished");
                    // Must call finish() so the BroadcastReceiver can be recycled.
                    //pendingResult.finish();
                    StalkerService.startActionAddEvents(context, namePlace, contactsForThisPlace);
                    sendNotification(context, contactNames, true);
                    return phoneNumbers;
                }

                @Override
                protected void onPostExecute(List<String> phoneNumbers) {
                    if (phoneNumbers != null && phoneNumbers.size() != 0) {
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            sendSMS(context, phoneNumbers.get(i), placeName.get(i));
                        }
                    }
                    pendingResult.finish();
                }
            };
            asyncTask.execute();

            Log.d("enter", "finished");



        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d("exit", "exited");
            final List<String> placeName = new ArrayList<String>();


            final PendingResult pendingResult = goAsync();
            @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, List<String>> asyncTask = new AsyncTask<String, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(String... params){
                    final int departureId;

                    String requestId = triggeringGeofence.get(triggeringGeofence.size()-1).getRequestId();

                    Log.d(TAG, "requestId " + requestId);
                    departureId = mDb.placeDao().findIdByPlaceId(requestId);
                    Log.d(TAG, "triggeringGeofence.size()" + triggeringGeofence.size() + " " + departureId);
                    List<RuleEntry> rulesForThisPlace = mDb.ruleDao().findRulesForDeparturePlace(departureId);
                    List<String> phoneNumbers = new ArrayList<String>();
                    List<String> contactNames = new ArrayList<String>();

                    if (rulesForThisPlace != null && rulesForThisPlace.size() != 0 ) {


                        Log.d(TAG, "rulesForThisPlace.size= " + rulesForThisPlace.size());

                        List<Integer> rulesIdForThisPlace = mDb.ruleDao().findRulesById(departureId);

                        Log.d(TAG, "rulesIdForThisPlace.size= " + rulesIdForThisPlace.size());

                        for (int i = 0; i < rulesForThisPlace.size(); i++) {
                            if (rulesForThisPlace.get(i).getType().equals("sms")) {
                                if (rulesForThisPlace.get(i).getArrivalId() != null){
                                rulesForThisPlace.get(i).setActive(true);
                                mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                } else {
                                    placeName.add(mDb.placeDao().findPlaceNameById(departureId));
                                    phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                                }
                            } else if (rulesForThisPlace.get(i).getType().equals("notify")){
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                contactNames.add(mDb.contactDao().findNameForContactId(rulesForThisPlace.get(i).getContactId()));
                            }  else if (rulesForThisPlace.get(i).getType().equals("wifi")) {
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                setWiFi(context, false);
                            } else if (rulesForThisPlace.get(i).getType().equals("wifioff")) {
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                setWiFi(context, true);
                            } else if (rulesForThisPlace.get(i).getType().equals("sound")) {
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                setSound(context, 0);}
                            else if (rulesForThisPlace.get(i).getType().equals("soundoff")) {
                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                setSound(context, 2);}
                        }
                    }
                    sendNotification(context, contactNames, false);
                    return phoneNumbers;
                }

                @Override
                protected void onPostExecute(List<String> phoneNumbers) {
                    if (phoneNumbers != null && phoneNumbers.size() != 0) {
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            sendDeparturingSMS(context, phoneNumbers.get(i), placeName.get(i));
                        }
                    }
                    // Must call finish() so the BroadcastReceiver can be recycled.
                    pendingResult.finish();
                }
            };
            asyncTask.execute();

            Log.d("enter", "exited " + context);

        } else {
            // Log the error.
            // No need to do anything else
            Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
        }
    }



    //Sends an SMS to the number stated

    protected void sendSMS(Context context, String phoneNumber, String placeName) {

        Log.d("sentsms", "exited " + context);
        Log.d("sentsms", "Number " + phoneNumber);
        if (android.os.Build.VERSION.SDK_INT < 24 ||
                (android.os.Build.VERSION.SDK_INT >= 24 && (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED))) {
            try {
                Log.d("sentsms", "now send " + context);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, "Hi! I just reached " + placeName + " safely! Have a wonderful day!", null, null);
                //  smsManager.sendTextMessage(number,null,matn,null,null);
            } catch (Exception e) {
                return;
            }
        }
    }

    protected void sendDeparturingSMS(Context context, String phoneNumber, String placeName) {

        Log.d("sentsms", "exited " + context);
        Log.d("sentsms", "Number " + phoneNumber);
        if (android.os.Build.VERSION.SDK_INT < 24 ||
                (android.os.Build.VERSION.SDK_INT >= 24 && (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED))) {
            try {
                Log.d("sentsms", "now send " + context);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, "Hi! I just departured from " + placeName, null, null);
            } catch (Exception e) {
                return;
            }
        }
    }

    private void setSound(Context context, int mode) {

        //RINGER_MODE_SILENT - Ringer mode that will be silent and will not vibrate. (This overrides the vibrate setting.)
        //Constant Value: 0 (0x00000000)
        //RINGER_MODE_NORMAL - Ringer mode that may be audible and may vibrate. It will be audible if the volume before changing out of this mode was audible. It will vibrate if the vibrate setting is on.
        //Constant Value: 2 (0x00000002)
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Check for DND permissions for API 24+
        if (android.os.Build.VERSION.SDK_INT < 24 ||
                (android.os.Build.VERSION.SDK_INT >= 24 && !notificationManager.isNotificationPolicyAccessGranted())) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }


    private void sendNotification(Context context, List<String> contactNames, boolean arrival) {
        if (contactNames != null && contactNames.size()!= 0) {
            StringBuilder nameList = new StringBuilder(contactNames.get(0));
            for (int i = 1; i < contactNames.size(); i++) {
                nameList.append(", ").append(contactNames.get(i));
            }

            if (arrival) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_place_green_24dp)
                        .setContentTitle("You arrived!")
                        .setContentText("Safely there!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Let " + nameList + " know that you are safe."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                // Dismiss notification once the user touches it.
                mBuilder.setAutoCancel(true);

                // Get an instance of the Notification manager
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Issue the notification
                if (mNotificationManager != null) {
                    mNotificationManager.notify(0, mBuilder.build());
                }

            } else {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_place_green_24dp)
                        .setContentTitle("You just departured!")
                        .setContentText("Safely on my way!")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Let " + nameList + " know."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                // Dismiss notification once the user touches it.
                mBuilder.setAutoCancel(true);

                // Get an instance of the Notification manager
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Issue the notification
                if (mNotificationManager != null) {
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }
        }
    }


    private void setWiFi(Context context, boolean mode) {
        if (android.os.Build.VERSION.SDK_INT < 24 ||
                (android.os.Build.VERSION.SDK_INT >= 24 && (ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED))) {
            try {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                wifiManager.setWifiEnabled(mode);}
            } catch (Exception e) {
            }
        }
   }

}