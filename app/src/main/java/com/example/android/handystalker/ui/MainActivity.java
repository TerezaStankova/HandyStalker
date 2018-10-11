package com.example.android.handystalker.ui;


/*import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;*/
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//import android.util.Log;
import android.view.View;

//import com.example.android.handystalker.utilities.StalkerJobService;
import com.google.android.gms.ads.AdView;


import com.example.android.handystalker.R;
import com.google.android.gms.ads.AdRequest;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

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

    /** Called when the user taps the Contacts button */
    public void onContactsButtonClicked(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Rules button */
    public void onRulesButtonClicked(View view) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }
}
