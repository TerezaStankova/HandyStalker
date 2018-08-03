package com.example.android.handystalker.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.Switch;

import com.example.android.handystalker.geofencing.Geofencing;
import com.example.android.handystalker.R;
import com.example.android.handystalker.model.Place;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

      /** Called when the user taps the Place button */
    public void onPlaceButtonClicked(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Place button */
    public void onContactsButtonClicked(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Place button */
    public void onRulesButtonClicked(View view) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }


}
