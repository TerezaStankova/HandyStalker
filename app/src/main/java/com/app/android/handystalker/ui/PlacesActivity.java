package com.app.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.PlaceEntry;
import com.app.android.handystalker.geofencing.GeofenceStorage;
import com.app.android.handystalker.geofencing.Geofencing;
import com.app.android.handystalker.ui.Adapters.PlacesAdapter;
import com.app.android.handystalker.utilities.AppExecutors;
import com.app.android.handystalker.utilities.PlacesViewModel;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlacesActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = PlacesActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PERMISSIONS_REQUEST_STORAGE = 161;

    // Member variables
    private PlacesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean mIsEnabled;

    private Geofencing mGeofencing;
    private GeofenceStorage mGeofenceStorage;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;
    private static final String LIST_STATE_KEY = "list_state";

    // Member variable for the Database
    private AppDatabase mDb;
    private int b;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places);

        String apiKey = getString(R.string.GOOGLE_PLACES_ANDROID_API_KEY);

        if (apiKey.equals("")) {
            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Retrieve a PlacesClient
        placesClient = Places.createClient(this);

        // Instantiate a new geofence storage area.
        mGeofenceStorage = new GeofenceStorage(this);

        // Set up the recycler view
        mRecyclerView = findViewById(R.id.places_list_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        restoreLayoutManagerPosition();
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PlacesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        GeofencingClient mGeoClient = LocationServices.getGeofencingClient(this);

        mGeofencing = new Geofencing(this, mGeoClient);
        //mGeoDataClient = Places.getGeoDataClient(this);

        mIsEnabled = getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.setting_enabled), false);

        Log.d("Preference","getPref" + mIsEnabled);

        mIsEnabled = mGeofenceStorage.getIsEnabled();

        Log.d("Preference","getPref" + mIsEnabled);




        mDb = AppDatabase.getInstance(getApplicationContext());
        mAdapter.setDatabase(mDb);
        setupViewModel();
    }

    //TODO: Loading bar

    private void showPlacesDataView() {
        /* Then, make sure the data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hidePlacesDataView() {
        /* Then, make sure the data is invisible */
        mRecyclerView.setVisibility(View.GONE);
    }


    //TODO: coarse location in manifest

    private void setupViewModel() {
        showPlacesDataView();
        PlacesViewModel viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.getPlaces().observe(this, new Observer<List<PlaceEntry>>() {
            @Override
            public void onChanged(@Nullable List<PlaceEntry> placeEntries) {
                if (placeEntries != null){
                Log.d("message", "Updating list of places from LiveData in ViewModel"  + placeEntries.size() );
                if (placeEntries.size() == 0) {
                    hidePlacesDataView();
                    return;
                }
                    showPlacesDataView();
                    final List<String> placeIds = new ArrayList<String>();
                    final List<String> placeNames = new ArrayList<String>();

                    for (int i = 0; i < placeEntries.size(); i++) {
                        placeIds.add(placeEntries.get(i).getPlaceId());
                        System.out.println("Ids" + placeEntries.get(i).getId());
                        System.out.println("placeIds" + i + placeIds.get(i));
                        placeNames.add(placeEntries.get(i).getPlaceName());
                        System.out.println("placeNames" + i + placeNames.get(i));
                    }

                    Log.d("PreferenceView","success" + mIsEnabled);
                    mAdapter.refreshPlaces(placeIds, placeNames);

                    mGeofenceStorage.setGeofence(placeIds);

                    // Specify the fields to return.
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    final List<Place> places = new ArrayList<Place>();
                    b = 1;

                    for (int i = 0; i < placeIds.size(); i++){

                        // Construct a request object, passing the place ID and fields array.
                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeIds.get(i), placeFields)
                                .build();

                        placesClient.fetchPlace(request).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<FetchPlaceResponse> response) {
                                if (response.isSuccessful()) {
                                    Place place = response.getResult().getPlace();

                                    Log.i(TAG, "Place found: " + place.getName() + places.size());
                                    places.add(place);
                                    Log.i(TAG, "Place size: " + places.size());

                                    if (b == placeIds.size()) {
                                        Log.i(TAG, "Place size2: " + places.size());
                                        //mGeofencing.updateGeofencesList(places);
                                        mGeofencing.updateGeofencesList(places, mIsEnabled);
                                        if (mIsEnabled) mGeofencing.registerAllGeofences();
                                    }
                                } else {
                                    Log.i(TAG, "Place size: " + places.size());
                                    Log.e(TAG, "Place not found.");
                                }
                                b += 1;
                            }
                        });
                    }
                }}
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        // Initialize location permissions checkbox
        /*CheckBox locationPermissions = findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(PlacesActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissions.setChecked(false);
        } else {
            locationPermissions.setChecked(true);
            locationPermissions.setEnabled(false);
        }*/

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }

    private void restoreLayoutManagerPosition() {
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        // Save list state
        mListState = layoutManager.onSaveInstanceState();
        savedInstanceState.putParcelable(LIST_STATE_KEY, mListState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    /** Called when the user taps the Place button */
    public void OnMapButtonClicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(PlacesActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, getString(R.string.storage), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(PlacesActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_STORAGE);
            return;
        }

        goToMap();
    }

    public void controlCount(final int i){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (i > 29) {
                    Toast.makeText(getApplicationContext(), getString(R.string.only_30), Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }

            }
        });


    }

    public void goToMap(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(PlacesActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(PlacesActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_STORAGE);
            return;
        }

        final int[] count = new int[1];

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                count[0] = mDb.placeDao().countPlaceIds();
                controlCount(count[0]);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    goToMap();
                }
            }
            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    goToMap();
                }
            }
        }
    }
}



