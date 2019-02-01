package com.example.android.handystalker.ui;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.geofencing.GeofenceStorage;
import com.example.android.handystalker.geofencing.Geofencing;
import com.example.android.handystalker.R;
import com.example.android.handystalker.ui.Adapters.PlacesAdapter;
import com.example.android.handystalker.utilities.AppExecutors;
import com.example.android.handystalker.utilities.PlacesViewModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;


import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.PlaceBufferResponse;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

public class PlacesActivity extends AppCompatActivity {

    // Constants
    public static final String TAG = PlacesActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 23456;

    // Member variables
    private PlacesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean mIsEnabled;
    //private GeoDataClient mGeoDataClient;
    private static Geofencing mGeofencing;
    private String placeIdfromPicker;
    private String AddressfromPicker;
    private GeofenceStorage mGeofenceStorage;

    //Variable to save position in the list
    private Parcelable mListState;
    private LinearLayoutManager layoutManager;
    private static final String LIST_STATE_KEY = "list_state";

    // Member variable for the Database
    private AppDatabase mDb;
    private int b;
    private final int d = 0;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places);



        // Retrieve a PlacesClient (previously initialized - see MainActivity)
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
    //TODO: register Geofences boolean
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
                                        mGeofencing.updateGeofencesList(places);
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





    /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example it's place name and place ID).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                if (place == null) {
                    Log.i(TAG, "No place selected");
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    AddressfromPicker = Objects.requireNonNull(place.getAddress()).toString();
                } else {
                    if (place.getAddress() != null) AddressfromPicker = place.getAddress().toString();
                }
                placeIdfromPicker = place.getId();
                buildDialog();


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /***
     * Button Click event handler to handle clicking the "Add new location" Button
     */
    public void onAddPlaceButtonClicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            // Start a new Activity for the Place Picker API, this will trigger {@code #onActivityResult}
            // when a place is selected or the user cancels.


            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


            /*PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);*/
        } catch (Exception e) {
            Log.e(TAG, String.format("AutoComplete Exception: %s", e.getMessage()));
        }
    }


    /*
     * Called when the Place Picker Activity returns back with a selected place (or after canceling)
     *
     * @param requestCode The request code passed when calling startActivityForResult
     * @param resultCode  The result code specified by the second activity
     * @param data        The Intent that carries the result data.

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = getPlace(this, data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }

            // Extract the place information from the API
            //String placeName = place.getName().toString();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AddressfromPicker = Objects.requireNonNull(place.getAddress()).toString();
            } else {
                if (place.getAddress() != null) AddressfromPicker = place.getAddress().toString();
            }
            placeIdfromPicker = place.getId();
            buildDialog();
        }
    }*/


    public void buildDialog(){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View placeLayout = inflater.inflate(R.layout.place_name_dialog, null);
        TextView address = placeLayout.findViewById(R.id.address_textView);
        final EditText nameEdit= placeLayout.findViewById(R.id.my_place_name);
        address.setText(AddressfromPicker);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(R.string.set_place_name)
                .setView(placeLayout)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        String name = nameEdit.getText().toString();
                        final PlaceEntry placeEntry = new PlaceEntry(placeIdfromPicker, name);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                // insert new task
                                mDb.placeDao().insertPlace(placeEntry);
                            }
                        });
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();

        /*
        Intent serviceIntent = new Intent(AddingGeofencesService.class.getName());
        serviceIntent.putExtra("UserID", (Parcelable) mGeofencing);
        this.startService(serviceIntent);*/

    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialize location permissions checkbox
        CheckBox locationPermissions = findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(PlacesActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissions.setChecked(false);
        } else {
            locationPermissions.setChecked(true);
            locationPermissions.setEnabled(false);
        }

        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }

    private void restoreLayoutManagerPosition() {
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }

    public void onLocationPermissionClicked(View view) {
        ActivityCompat.requestPermissions(PlacesActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);
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
}



