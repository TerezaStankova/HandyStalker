package com.example.android.handystalker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.android.handystalker.R;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);
    }

    public void onSMSButtonClicked(View view) {
        Intent intent = new Intent(this, SmsRulesActivity.class);
        startActivity(intent);
    }

    public void onEmailButtonClicked(View view) {
    }

    public void onNotificationButtonClicked(View view) {
    }
}