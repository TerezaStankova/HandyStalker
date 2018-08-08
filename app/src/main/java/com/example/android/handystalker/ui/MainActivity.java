package com.example.android.handystalker.ui;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import com.google.android.gms.ads.AdView;


import com.example.android.handystalker.R;
import com.google.android.gms.ads.AdRequest;


public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
