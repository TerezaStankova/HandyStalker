package com.example.android.handystalker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.android.handystalker.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);

        /*AdView mAdView = findViewById(R.id.adViewRules);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
    }

    public void onSMSButtonClicked(View view) {
        Intent intent = new Intent(this, SmsRulesActivity.class);
        startActivity(intent);
    }

    public void onNotificationButtonClicked(View view) {
        Intent intent = new Intent(this, HandyRulesActivity.class);
        startActivity(intent);
    }

    public void onSoundSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, HandyRulesActivity.class);
        startActivity(intent);
    }

    public void onWifiButtonClicked(View view) {
        Intent intent = new Intent(this, HandyRulesActivity.class);
        startActivity(intent);
    }
}