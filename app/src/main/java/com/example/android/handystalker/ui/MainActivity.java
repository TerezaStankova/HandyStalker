package com.example.android.handystalker.ui;


/*import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

//import com.example.android.handystalker.utilities.StalkerJobService;


import com.example.android.handystalker.R;
import com.example.android.handystalker.geofencing.GeofenceStorage;
import com.example.android.handystalker.geofencing.Geofencing;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean mIsEnabled;
    private GeofenceStorage mGeofenceStorage;
    private static Geofencing mGeofencing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(this);

        mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);
        Log.d("Preference","getPref" + mIsEnabled);

        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(getApplicationContext());
        mGeofencing = new Geofencing(getApplicationContext(), mGeoClient);


        // Initialize the switch state and Handle enable/disable switch change
        Switch onOffSwitch = findViewById(R.id.enable_switch2);
        setCheckedPrivacy(onOffSwitch);


    }

    private void setCheckedPrivacy(Switch onOffSwitch){
        onOffSwitch.setChecked(mIsEnabled);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.setting_enabled), isChecked);
                mIsEnabled = isChecked;

                //TODO: is it right?
                //AddingGeofencesService.setmIsEnabled(mIsEnabled);
                mGeofenceStorage.setIsEnabled(mIsEnabled);

                editor.apply();
                if (isChecked) mGeofencing.registerAllGeofences();
                else mGeofencing.unRegisterAllGeofences();
            }});
    }

   /* public void scheduleJob(View v) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            ComponentName componentName = new ComponentName(this, StalkerJobService.class);
            JobInfo info = null;
            info = new JobInfo.Builder(1000, componentName)
                    .setRequiresCharging(false)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPersisted(true)
                    .build();

            JobScheduler scheduler = null;
            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

            int resultCode = 0;
            resultCode = scheduler.schedule(info);

            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job scheduled");
            } else {
                Log.d(TAG, "Job scheduling failed");
            }
        }
    }

    /* Id of the job can be any integer, it will identify the job*/
   /*
    public void cancelJob(View v) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(1000);
            Log.d(TAG, "Job cancelled");
        }
    } */

      /** Called when the user taps the Place button */
    public void onPlaceButtonClicked(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Contacts button
    public void onContactsButtonClicked(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }*/

    /** Called when the user taps the Rules button */
    public void onRulesButtonClicked(View view) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }
}
