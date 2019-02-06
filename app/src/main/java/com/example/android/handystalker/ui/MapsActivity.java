package com.example.android.handystalker.ui;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.utilities.AppExecutors;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.handystalker.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.android.handystalker.BuildConfig.API_KEY;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;

    // Member variable for the Database
    private AppDatabase mDb;

    private String placeIdfromPicker;
    private String AddressfromPicker;

    private AutocompleteSupportFragment autocompleteFragment;
    private final String TAG = "MapActivity";
    private boolean mLocationPermissionGranted;
    private CameraPosition mCameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(50.084430, 14.441220);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 111;


    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 20;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private String[] mLikelyPlaceIds;
    private PlacesClient placesClient;
    private ActionMenuItemView nearPlacesItem;
    private MenuItem nearPlacesMenuItem;
    private static final int  REQUEST_CHECK_SETTINGS = 15;



    private Marker marker;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //nearPlacesItem = findViewById(R.id.option_get_place);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mDb = AppDatabase.getInstance(getApplicationContext());

        /**
         * Initialize Places.
         */

        if (!Places.isInitialized()) {
            Log.d(TAG, "initialized");
            Places.initialize(getApplicationContext(), API_KEY);
        }

        isConnected();


        android.support.v7.app.ActionBar tb = getSupportActionBar();
        tb.setDisplayHomeAsUpEnabled(true);



        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(this);

        Log.i(TAG, "I do this: " + 1);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                if (place.getLatLng() == null || place.getName() == null) {
                    return;
                }
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                final LatLng latLngLoc = place.getLatLng();

                if (marker != null) {
                    marker.remove();
                }
                Log.i(TAG, "I do this: " + 2);
                marker = mMap.addMarker(new MarkerOptions().position(latLngLoc).title(place.getName()).draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLoc, DEFAULT_ZOOM));
                Log.i(TAG, "I do this: " + 3);

                if (place.getAddress() != null) {AddressfromPicker = place.getAddress();
                } else AddressfromPicker = null;
                placeIdfromPicker = place.getId();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buildDialog();
                    }
                }, 3000);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(MapsActivity.this, "" + status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Log.i(TAG, "I do this: " + 4);
        //createLocationRequest();

    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                updateLocationUI();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }


    public boolean isLocationEnabled()
    {
        /*if (Build.VERSION.SDK_INT >= 28) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = lm.isLocationEnabled();
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {*/

            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            if (mode != Settings.Secure.LOCATION_MODE_OFF) {
                return true;
            }
            else {
                Toast.makeText(MapsActivity.this, "Location is off.", Toast.LENGTH_SHORT).show();

                //createLocationRequest();

                return false;}

        //}
    }

    public boolean isConnected()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {return true; } else {
            Toast.makeText(MapsActivity.this, "You are not connected to the Internet. Connect and add new places.", Toast.LENGTH_SHORT).show();
            return false;}

    }

    public void buildDialog() {
        // Use the Builder class for convenient dialog construction

        if (placeIdfromPicker.length() > 100) {
            Toast.makeText(MapsActivity.this, "This place can not be monitored. Try more specific address.", Toast.LENGTH_SHORT).show();

            return;}
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View placeLayout = inflater.inflate(R.layout.place_name_dialog, null);
        TextView address = placeLayout.findViewById(R.id.address_textView);
        final EditText nameEdit = placeLayout.findViewById(R.id.my_place_name);
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
                        Intent intent = new Intent(getApplicationContext(), PlacesActivity.class);
                        startActivity(intent);
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
    public boolean onMarkerClick(Marker marker) {
        //LatLng position=marker.getPosition();
        Log.i(TAG, "I do this: markert cklicke " + 4);
        buildDialog();
        return false;
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        nearPlacesMenuItem = menu.findItem(R.id.option_get_place);
        if (nearPlacesMenuItem != null && isLocationEnabled()){
            if (mLastKnownLocation != null) { nearPlacesMenuItem.setVisible(true);Log.d(TAG, "show current on prepare" + 12);
            } else {nearPlacesMenuItem.setVisible(false); Log.d(TAG, "show current on prepare" + 13);}
        }
        return true;
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "I do this: on create menu" + 5);
        getMenuInflater().inflate(R.menu.current_place_menu, menu);

        menu.findItem(R.id.option_get_place);

        nearPlacesMenuItem = (MenuItem) menu.findItem(R.id.option_get_place);

        if (nearPlacesMenuItem != null && isLocationEnabled()){
            if (mLastKnownLocation != null) { nearPlacesMenuItem.setVisible(true);Log.d(TAG, "show current on prepare" + 16);
            } else {nearPlacesMenuItem.setVisible(false); Log.d(TAG, "show current on prepare" + 17);}
        }

        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            Log.i(TAG, "show current" + 1);
            if (!isConnected()) {return true;}
            else {showCurrentPlace();}
        } else if ( item.getItemId() == android.R.id.home)
        {onBackPressed();}

        return true;
    }


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        Log.i(TAG, "I do this: Map ready " + 1);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation();

        //mMap.setOnMarkerDragListener((GoogleMap.OnMarkerDragListener) this);

        mMap.setOnMarkerClickListener(this);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {

        Log.i(TAG, "I do this: get device");
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }

                        //Show MenuItem if last location is available
                        if (nearPlacesMenuItem != null){
                            if (mLastKnownLocation != null) { nearPlacesMenuItem.setVisible(true);}
                            else {nearPlacesMenuItem.setVisible(false);}}
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            invalidateOptionsMenu();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {

        Log.i(TAG, "I do this: permission" + 1);
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */

    private void showCurrentPlace() {
        if (mMap == null) {
            Log.i(TAG, "I do this: showcurret null" + 1);
            return;
        }
        Log.i(TAG, "I do this: showcurret" + 1);



            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // The user has not granted permission.
                Log.i(TAG, "The user did not grant location permission.");

                // Add a default marker, because the user hasn't selected a place.
                marker = mMap.addMarker(new MarkerOptions()
                        .title(getString(R.string.default_info_title))
                        .position(mDefaultLocation)
                        .snippet(getString(R.string.default_info_snippet)));

                getLocationPermission();
                return;
            }

            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

            placesClient.findCurrentPlace(request).addOnSuccessListener((new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse response) {

                    // Set the count, handling cases where less than 5 entries are returned.
                    int count;
                    List<PlaceLikelihood> likelyPlaces = response.getPlaceLikelihoods();
                    if (likelyPlaces.size() == 0) {
                        return;
                    }

                    if (likelyPlaces.size() < M_MAX_ENTRIES) {
                        count = likelyPlaces.size();
                    } else {
                        count = M_MAX_ENTRIES;
                    }

                    mLikelyPlaceNames = new String[count];
                    mLikelyPlaceAddresses = new String[count];
                    mLikelyPlaceLatLngs = new LatLng[count];
                    mLikelyPlaceIds = new String[count];
                    mLikelyPlaceAttributions = new String[count];

                    int i = 0;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Build a list of likely places to show the user.
                        mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                        mLikelyPlaceAddresses[i] = placeLikelihood.getPlace()
                                .getAddress();
                        mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
                        mLikelyPlaceIds[i] = placeLikelihood.getPlace().getId();
                        if (placeLikelihood.getPlace().getAttributions() != null) mLikelyPlaceAttributions[i] = placeLikelihood.getPlace().getAttributions().get(0);

                        i++;
                        if (i > (count - 1)) {
                            break;
                        }
                    }
                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    openPlacesDialog();
                }
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Toast.makeText(MapsActivity.this, "No Places of interest found around.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No Places of interest found around. Try to press ´Nearby´ again after your location is found and blue circle appears." + apiException.getStatusCode() + apiException.getMessage());
                    }
                }
            });
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        Log.i(TAG, "I do this: dialog" + 1);
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                AddressfromPicker = mLikelyPlaceAddresses[which];
                placeIdfromPicker =  mLikelyPlaceIds[which];

                if(marker!=null){
                    marker.remove();
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                marker = mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .draggable(true)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buildDialog();
                    }
                }, 4000);

            }
        };

        // Display the dialog.
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setCancelable(true)
                .setItems(mLikelyPlaceNames, listener);
        dialog.show();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted && isLocationEnabled()) {

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==  REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}