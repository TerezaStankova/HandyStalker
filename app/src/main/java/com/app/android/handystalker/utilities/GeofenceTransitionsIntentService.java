package com.app.android.handystalker.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.RuleEntry;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsIntentService extends JobIntentService {
    // Member variable for the Database
    private AppDatabase mDb;
    public final String CHANNEL_ID = "default";
    private static final int JOB_ID = 111;

    public static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsIntentService.class, JOB_ID, intent);
    }


    protected void onHandleWork(@NonNull Intent intent) {

        final Context context = getApplicationContext();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            final List<Geofence> triggeringGeofence = geofencingEvent.getTriggeringGeofences();
            mDb = AppDatabase.getInstance(context);


            switch (geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER: {
                    Log.d("enter", "entered ");
                    final List<String> placeName = new ArrayList<String>();
                    final List<String> messagesForThisPlace = new ArrayList<String>();

                    @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, List<String>> asyncTask = new AsyncTask<String, Void, List<String>>() {
                        @Override
                        protected List<String> doInBackground(String... params) {
                            final int arrivalId;

                            String requestId = triggeringGeofence.get(triggeringGeofence.size() - 1).getRequestId();

                            Log.d(TAG, "requestId " + requestId);
                            arrivalId = mDb.placeDao().findIdByPlaceId(requestId);
                            Log.d(TAG, "triggeringGeofence.size()" + triggeringGeofence.size() + " " + arrivalId);
                            String namePlace = mDb.placeDao().findPlaceNameById(arrivalId);
                            List<RuleEntry> rulesForThisPlace = mDb.ruleDao().findRulesForArrivalPlace(arrivalId);
                            List<String> phoneNumbers = new ArrayList<String>();

                            if (rulesForThisPlace != null && rulesForThisPlace.size() != 0) {
                                Log.d(TAG, "rulesForThisPlace.size= " + rulesForThisPlace.size());

                                for (int i = 0; i < rulesForThisPlace.size(); i++) {
                                    if (rulesForThisPlace.get(i).getType().equals("sms")) {

                                        if (rulesForThisPlace.get(i).getDepartureId() != null) {
                                            boolean active = rulesForThisPlace.get(i).getActive();
                                            if (active) {
                                                rulesForThisPlace.get(i).setActive(false);
                                                mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                                placeName.add(mDb.placeDao().findPlaceNameById(arrivalId));
                                                phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                                                messagesForThisPlace.add(mDb.messageDao().findTextForMessageId(rulesForThisPlace.get(i).getMessageId()));
                                            }
                                        } else {
                                            placeName.add(mDb.placeDao().findPlaceNameById(arrivalId));
                                            phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                                            messagesForThisPlace.add(mDb.messageDao().findTextForMessageId(rulesForThisPlace.get(i).getMessageId()));
                                        }
                                    } else if (rulesForThisPlace.get(i).getType().equals("notify")) {
                                        if (rulesForThisPlace.get(i).getDepartureId() != null) {
                                            boolean active = rulesForThisPlace.get(i).getActive();
                                            if (active) {
                                                rulesForThisPlace.get(i).setActive(false);
                                                mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                                sendNotification(context, true);
                                            }
                                        } else {
                                            sendNotification(context, true);
                                        }

                                        Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                    } else if (rulesForThisPlace.get(i).getType().equals("wifi")) {

                                        if (rulesForThisPlace.get(i).getDepartureId() != null) {
                                            boolean active = rulesForThisPlace.get(i).getActive();
                                            if (active) {
                                                rulesForThisPlace.get(i).setActive(false);
                                                mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                                setWiFi(context, true);
                                            }
                                        } else {
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                            setWiFi(context, true);
                                        }

                                    } else if (rulesForThisPlace.get(i).getType().equals("wifioff")) {

                                        if (rulesForThisPlace.get(i).getDepartureId() != null) {
                                            boolean active = rulesForThisPlace.get(i).getActive();
                                            if (active) {
                                                rulesForThisPlace.get(i).setActive(false);
                                                mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                                setWiFi(context, false);
                                            }
                                        } else {
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                            setWiFi(context, false);
                                        }


                                    } else if (rulesForThisPlace.get(i).getType().equals("sound")) {

                                        if (rulesForThisPlace.get(i).getDepartureId() != null) {
                                            boolean active = rulesForThisPlace.get(i).getActive();
                                            if (active) {
                                                rulesForThisPlace.get(i).setActive(false);
                                                mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                                setSound(context, 2);
                                                Log.d(TAG, "setting 2 departuru " + rulesForThisPlace.get(i).getType() + rulesForThisPlace.get(i).getDepartureId());
                                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                            }
                                        } else {
                                            setSound(context, 2);
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                        }



                                    } else if (rulesForThisPlace.get(i).getType().equals("soundoff")) {

                                        if (rulesForThisPlace.get(i).getDepartureId() != null) {
                                            boolean active = rulesForThisPlace.get(i).getActive();
                                            if (active) {
                                                rulesForThisPlace.get(i).setActive(false);
                                                mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                                setSound(context, 0);
                                                Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                                Log.d(TAG, "setting 0 departuru " + rulesForThisPlace.get(i).getType() + rulesForThisPlace.get(i).getDepartureId());
                                            }
                                        } else {
                                            setSound(context, 0);
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                        }

                                    }
                                }
                            }

                            Log.d(TAG, "async finished");
                            // Must call finish() so the BroadcastReceiver can be recycled.
                            //pendingResult.finish();
                            //sendNotification(context, true);
                            return phoneNumbers;
                        }

                        @Override
                        protected void onPostExecute(List<String> phoneNumbers) {
                            if (phoneNumbers != null && phoneNumbers.size() != 0) {
                                for (int i = 0; i < phoneNumbers.size(); i++) {
                                    sendSMS(context, phoneNumbers.get(i), placeName.get(i), messagesForThisPlace.get(i));
                                }
                            }
                        }
                    };
                    asyncTask.execute();

                    Log.d("enter", "finished");


                    break;
                }
                case Geofence.GEOFENCE_TRANSITION_EXIT: {
                    Log.d("exit", "exited");
                    final List<String> placeName = new ArrayList<String>();
                    final List<String> messagesForThisPlace = new ArrayList<String>();


                    @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, List<String>> asyncTask = new AsyncTask<String, Void, List<String>>() {
                        @Override
                        protected List<String> doInBackground(String... params) {
                            final int departureId;

                            String requestId = triggeringGeofence.get(triggeringGeofence.size() - 1).getRequestId();

                            Log.d(TAG, "requestId " + requestId);
                            departureId = mDb.placeDao().findIdByPlaceId(requestId);
                            Log.d(TAG, "triggeringGeofence.size()" + triggeringGeofence.size() + " " + departureId);
                            List<RuleEntry> rulesForThisPlace = mDb.ruleDao().findRulesForDeparturePlace(departureId);
                            List<String> phoneNumbers = new ArrayList<String>();

                            if (rulesForThisPlace != null && rulesForThisPlace.size() != 0) {


                                Log.d(TAG, "rulesForThisPlace.size= " + rulesForThisPlace.size());

                                List<Integer> rulesIdForThisPlace = mDb.ruleDao().findRulesById(departureId);

                                Log.d(TAG, "rulesIdForThisPlace.size= " + rulesIdForThisPlace.size());

                                for (int i = 0; i < rulesForThisPlace.size(); i++) {
                                    if (rulesForThisPlace.get(i).getType().equals("sms")) {
                                        if (rulesForThisPlace.get(i).getArrivalId() != null) {
                                            rulesForThisPlace.get(i).setActive(true);
                                            mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                        } else {
                                            placeName.add(mDb.placeDao().findPlaceNameById(departureId));
                                            phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                                            messagesForThisPlace.add(mDb.messageDao().findTextForMessageId(rulesForThisPlace.get(i).getMessageId()));
                                        }
                                    } else if (rulesForThisPlace.get(i).getType().equals("notify")) {

                                        if (rulesForThisPlace.get(i).getArrivalId() != null) {
                                            rulesForThisPlace.get(i).setActive(true);
                                            mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                        } else {
                                            sendNotification(context, false);
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                        }


                                    } else if (rulesForThisPlace.get(i).getType().equals(getString(R.string.wifi))) {

                                        if (rulesForThisPlace.get(i).getArrivalId() != null) {
                                            rulesForThisPlace.get(i).setActive(true);
                                            mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                        } else {
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                            setWiFi(context, true);
                                        }

                                    } else if (rulesForThisPlace.get(i).getType().equals(getString(R.string.wifioff))) {

                                        if (rulesForThisPlace.get(i).getArrivalId() != null) {
                                            rulesForThisPlace.get(i).setActive(true);
                                            mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                        } else {
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                            setWiFi(context, false);
                                        }

                                    } else if (rulesForThisPlace.get(i).getType().equals(getString(R.string.soundon))) {

                                        if (rulesForThisPlace.get(i).getArrivalId() != null) {
                                            rulesForThisPlace.get(i).setActive(true);
                                            mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                        } else {
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                            setSound(context, 2);
                                        }


                                    } else if (rulesForThisPlace.get(i).getType().equals(getString(R.string.soundoff))) {
                                        if (rulesForThisPlace.get(i).getArrivalId() != null) {
                                            rulesForThisPlace.get(i).setActive(true);
                                            mDb.ruleDao().updateRule(rulesForThisPlace.get(i));
                                        } else {
                                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                                            setSound(context, 0);
                                        }


                                    }
                                }
                            }

                            return phoneNumbers;
                        }

                        @Override
                        protected void onPostExecute(List<String> phoneNumbers) {
                            if (phoneNumbers != null && phoneNumbers.size() != 0) {
                                for (int i = 0; i < phoneNumbers.size(); i++) {
                                    sendDeparturingSMS(context, phoneNumbers.get(i), placeName.get(i), messagesForThisPlace.get(i));
                                }
                            }
                            // Must call finish() so the BroadcastReceiver can be recycled.
                        }
                    };
                    asyncTask.execute();

                    Log.d("enter", "exited " + context);

                    break;
                }
                default:
                    // Log the error.
                    // No need to do anything else
                    Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
                    break;
            }

        } else {
            // Log the error.
            Log.e(TAG, "Invalid Transition");
        }
    }


    //Sends an SMS to the number stated

    protected void sendSMS(Context context, String phoneNumber, String placeName, String message) {

        Log.d("sentsms", "exited " + context);
        Log.d("sentsms", "Number " + phoneNumber);
        if (android.os.Build.VERSION.SDK_INT < 24 || ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                Log.d("sentsms", "now send " + context);
                String messageText;
                if (message == null || message.length() == 0) { messageText = getString(R.string.Hi_reaching) + " " + placeName + " " + getString(R.string.safely);}
                else {messageText = message;}

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, messageText, null, null);
            } catch (Exception e) {
            }
        }
    }

    protected void sendDeparturingSMS(Context context, String phoneNumber, String placeName, String message) {

        Log.d("sentsms", "exited " + context);
        Log.d("sentsms", "Number " + phoneNumber + message);
        if (android.os.Build.VERSION.SDK_INT < 24 || ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                Log.d("sentsms", "now send " + context);
                String messageText;
                if (message == null || message.length() == 0) { messageText = getString(R.string.Hi) + " " + placeName;}
                else {messageText = message;}

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, messageText, null, null);
            } catch (Exception e) {
            }
        }
    }

    private void setSound(Context context, int mode) {

        Log.d("turn", "sound to mode: " + mode);
        //RINGER_MODE_SILENT - Ringer mode that will be silent and will not vibrate. (This overrides the vibrate setting.)
        //Constant Value: 0 (0x00000000)
        //RINGER_MODE_NORMAL - Ringer mode that may be audible and may vibrate. It will be audible if the volume before changing out of this mode was audible. It will vibrate if the vibrate setting is on.
        //Constant Value: 2 (0x00000002)
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Check for DND permissions for API 24+
        if (notificationManager != null) {

            if (android.os.Build.VERSION.SDK_INT < 24 ||
                    (android.os.Build.VERSION.SDK_INT >= 24 && notificationManager.isNotificationPolicyAccessGranted())) {
                Log.d("sdk", "sound to mode: " + mode);
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager != null){
                    Log.d("turn audionot null", "sound to mode: " + mode);
                    audioManager.setRingerMode(mode);}
            }

        }

    }


    private void sendNotification(Context context, boolean arrival) {

        if (arrival) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_place_green_24dp)
                    .setContentTitle(getString(R.string.you_arrived))
                    .setContentText(getString(R.string.safely_there))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.let)))
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
                    .setContentTitle(getString(R.string.you_departured))
                    .setContentText("Safely on my way!")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.let)))
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


    private void setWiFi(Context context, boolean mode) {
        Log.d("turn", "wifi to mode: " + mode);
        if (android.os.Build.VERSION.SDK_INT < 24 || ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    wifiManager.setWifiEnabled(mode);}
            } catch (Exception e) {
                Log.d("wifi", "wifi not enabled");
            }
        }
    }
}