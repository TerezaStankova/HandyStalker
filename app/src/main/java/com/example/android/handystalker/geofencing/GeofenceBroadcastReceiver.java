package com.example.android.handystalker.geofencing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;

import android.graphics.Movie;
import android.net.Uri;

import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;

import com.example.android.handystalker.database.RuleEntry;
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
            sendNotification(context, 0);


            final PendingResult pendingResult = goAsync();
            @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, List<String>> asyncTask = new AsyncTask<String, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(String... params){
                    final int arrivalId;

                    String requestId = triggeringGeofence.get(triggeringGeofence.size()-1).getRequestId();

                    Log.d(TAG, "requestId " + requestId);
                    arrivalId = mDb.placeDao().findIdByPlaceId(requestId);
                    Log.d(TAG, "triggeringGeofence.size()" + triggeringGeofence.size() + " " + arrivalId);
                    List<RuleEntry> rulesForThisPlace = mDb.ruleDao().findRulesForArrivalPlace(arrivalId);
                    List<String> phoneNumbers = new ArrayList<String>();

                    Log.d(TAG, "rulesForThisPlace.size= " + rulesForThisPlace.size());

                    List<Integer> rulesIdForThisPlace = mDb.ruleDao().findRulesById(arrivalId);

                    Log.d(TAG, "rulesIdForThisPlace.size= " + rulesIdForThisPlace.size() + rulesIdForThisPlace.get(0));

                    for (int i = 0; i < rulesForThisPlace.size(); i++) {
                        if (rulesForThisPlace.get(i).getType().equals("sms")) {
                            phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                        } else if (rulesForThisPlace.get(i).getType().equals("notify")){
                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                            sendNotification(context, rulesForThisPlace.get(i).getContactId());
                        }
                    }

                    Log.d(TAG, "async finished");
                    // Must call finish() so the BroadcastReceiver can be recycled.
                    //pendingResult.finish();
                    return phoneNumbers;
                }

                @Override
                protected void onPostExecute(List<String> phoneNumbers) {
                    if (phoneNumbers.size() != 0) {
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            sendSMS(context, phoneNumbers.get(i));
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


                    Log.d(TAG, "rulesForThisPlace.size= " + rulesForThisPlace.size());

                    List<Integer> rulesIdForThisPlace = mDb.ruleDao().findRulesById(departureId);

                    Log.d(TAG, "rulesIdForThisPlace.size= " + rulesIdForThisPlace.size() + rulesIdForThisPlace.get(0));

                    for (int i = 0; i < rulesForThisPlace.size(); i++) {
                        if (rulesForThisPlace.get(i).getType().equals("sms")) {
                            phoneNumbers.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                            placeName.add(mDb.contactDao().findPhoneForContactId(rulesForThisPlace.get(i).getContactId()));
                        } else if (rulesForThisPlace.get(i).getType().equals("notify")){
                            Log.d(TAG, "rulesForThisPlace.get(i).getType()" + rulesForThisPlace.get(i).getType());
                            sendNotification(context, rulesForThisPlace.get(i).getContactId());
                        }
                    }

                    Log.d(TAG, "async finished");
                    // Must call finish() so the BroadcastReceiver can be recycled.
                    //pendingResult.finish();
                    return phoneNumbers;
                }

                @Override
                protected void onPostExecute(List<String> phoneNumbers) {
                    if (phoneNumbers.size() != 0) {
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            sendDeparturingSMS(context, phoneNumbers.get(i), placeName.get(i));
                        }
                    }
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

    /*class FetchRulesTask extends AsyncTask<String, Void, Movie[]> {


        @Override
        protected Movie[] doInBackground(String... params) {


            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                return JSONUtils.getMovieDataFromJson(MainActivity.this, jsonMovieResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                movies = new Movie[movieData.length];
                movies = movieData;
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
            pendingResult.finish();
            return data;
        }
    }*/



    //Sends an SMS to the number stated

    protected void sendSMS(Context context, String phoneNumber) {

        Log.d("sentsms", "exited " + context);
        Log.d("sentsms", "Number " + phoneNumber);
        if (android.os.Build.VERSION.SDK_INT < 24 ||
                (android.os.Build.VERSION.SDK_INT >= 24 && (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED))) {
            try {
                Log.d("sentsms", "now send " + context);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, "Dorazila jsem", null, null);
                //  smsManager.sendTextMessage(number,null,matn,null,null);
            } catch (Exception e) {
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
                smsManager.sendTextMessage(phoneNumber, null, "Ahoj! Vyrazila jsem z místa" + placeName, null, null);
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


    private void sendNotification(Context context, int contactId) {

        Log.d("notify", "now" + context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_place_green_24dp)
                .setContentTitle("You arrived!")
                .setContentText("Let your beloved ones know that you are safe.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Safely there" + contactId))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // Dismiss notification once the user touches it.
        mBuilder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, mBuilder.build());
    }

    /*protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }*/



    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    /*public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }*/




}