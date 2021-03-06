package com.app.android.handystalker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.app.android.handystalker.R;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);
    }

    public void onSMSButtonClicked(View view) {
        Intent intent = new Intent(this, TextRulesActivity.class);
        startActivity(intent);
    }

    public void onNotificationButtonClicked(View view) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

    public void onSoundSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, SoundRulesActivity.class);
        startActivity(intent);
    }

    public void onWifiButtonClicked(View view) {
        Intent intent = new Intent(this, WifiRulesActivity.class);
        startActivity(intent);
    }
}