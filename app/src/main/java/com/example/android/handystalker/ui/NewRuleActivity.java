package com.example.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Spinner;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;

import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.database.RuleEntry;

import com.example.android.handystalker.utilities.AppExecutors;
import com.example.android.handystalker.utilities.ContactsViewModel;
import com.example.android.handystalker.utilities.PlacesViewModel;


import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class NewRuleActivity  extends AppCompatActivity {

    List<Integer> mContactsId = newArrayList();
    List<String> mContactsName = newArrayList();
    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2222;
    private static final int MY_PERMISSIONS_REQUEST_SEND_NOTIFICATIONS = 1111;

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner typeSpinner;
    Spinner contactNameSpinner;
    Spinner arrivalSpinner;
    Spinner departureSpinner;

    Spinner contactNameSpinnerNotify;
    Spinner placeSpinner;

    int placeId = -1;
    int arrivalId = -1;
    int departureId = -1;
    int contactId = -1;
    int contactIdNot = -1;
    String type = "sms";
    boolean arrivalNotificationRule = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_rule);
        setTitle("New Stalking Rule");

        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        contactNameSpinner = (Spinner) findViewById(R.id.name_spinner);
        arrivalSpinner = (Spinner) findViewById(R.id.arrival_spinner);
        departureSpinner = (Spinner) findViewById(R.id.departure_spinner);

        contactNameSpinnerNotify = (Spinner) findViewById(R.id.name_spinner2);
        placeSpinner = (Spinner) findViewById(R.id.place_spinner);

        setupTypeSpinner();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupPlacesViewModel();
        setupContactsViewModel();
    }

    public void onSaveSendingRuleClick(View view) {
        type = "sms";

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);

                    // MY_PERMISSIONS_REQUEST_SEND_SMS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                String name = (String) contactNameSpinner.getSelectedItem();

                if (name != null) {
                    final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureId, contactId, type);
                    Log.d("rules entred", "r " + arrivalId + departureId + contactId + type);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // insert new contact
                            mDb.ruleDao().insertRule(ruleEntry);

                        }
                    });
                }

                Intent intent = new Intent(this, SmsRulesActivity.class);
                startActivity(intent);
            }
    }

    public void onSaveNotifyRuleClick(View view) {
        type = "notify";
        String name = (String) contactNameSpinner.getSelectedItem();

        if (arrivalNotificationRule){
            final RuleEntry ruleEntry = new RuleEntry(placeId, -1, contactIdNot, type);
            Log.d("rules entred notify", "r " + placeId + contactIdNot + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // insert new contact
                    mDb.ruleDao().insertRule(ruleEntry);

                }});
        } else {
            final RuleEntry ruleEntry = new RuleEntry(-1, placeId, contactIdNot, type);
            Log.d("rules entred notify", "r " + placeId + contactIdNot + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // insert new contact
                    mDb.ruleDao().insertRule(ruleEntry);

                }});
        }
        Intent intent = new Intent(this, SmsRulesActivity.class);
        startActivity(intent);
        }

    public void saveRule(){
        if (type.equals("sms")) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);

                    // MY_PERMISSIONS_REQUEST_SEND_SMS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                Log.d("rules entred", "r " + arrivalId + departureId + contactId + type);
                String name = (String) contactNameSpinner.getSelectedItem();

                if (name != null) {
                    final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureId, contactId, type);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // insert new contact
                            mDb.ruleDao().insertRule(ruleEntry);

                        }
                    });
                }

                Intent intent = new Intent(this, SmsRulesActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    saveRule();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Intent intent = new Intent(this, SmsRulesActivity.class);
                    //startActivity(intent);
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_SEND_NOTIFICATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    saveRule();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Intent intent = new Intent(this, SmsRulesActivity.class);
                    //startActivity(intent);
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void setupTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                switch (position) {
                    case 0:
                        // Chosen arrival
                        arrivalNotificationRule = true;
                        break;
                    case 1:
                        // Chosen departure
                        arrivalNotificationRule = false;
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
                type = "sms";
            }
        });
    }


    private void setupPlacesViewModel() {
        PlacesViewModel viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.getPlaces().observe(this, new Observer<List<PlaceEntry>>() {
            @Override
            public void onChanged(@Nullable List<PlaceEntry> placeEntries) {
                Log.d("message", "Updating list of places from LiveData in ViewModel"  + placeEntries.size() );
                if (placeEntries.size() == 0) {
                    return;
                }
                if (placeEntries != null || placeEntries.size() != 0) {
                    placeIds.clear();
                    placeNames.clear();

                    for (int i = 0; i < placeEntries.size(); i++) {
                        placeIds.add(placeEntries.get(i).getId());
                        System.out.println("placeIds" + i + placeIds.get(i));
                        placeNames.add(placeEntries.get(i).getPlaceName());
                        System.out.println("placeNames" + i + placeNames.get(i));
                    }

                    ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            placeNames
                    );
                    // Specify the layout to use when the list of choices appears
                    adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    arrivalSpinner.setAdapter(adapterPlace);
                    departureSpinner.setAdapter(adapterPlace);
                    placeSpinner.setAdapter(adapterPlace);
                }

                arrivalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                        arrivalId = placeIds.get(position);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        arrivalId = -1;
                    }
                });

                departureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                        departureId = placeIds.get(position);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        departureId = -1;
                    }
                });

                placeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                        placeId = placeIds.get(position);
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                        placeId = -1;
                    }
                });

            }
        });
    }

    private void setupContactsViewModel() {
        ContactsViewModel viewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        viewModel.getContacts().observe(this, new Observer<List<ContactsEntry>>() {
            @Override
            public void onChanged(@Nullable List<ContactsEntry> contactsEntries) {
                Log.d("message", "Updating list of places from LiveData in ViewModel"  + contactsEntries.size() );
                if (contactsEntries.size() == 0) {
                    return;
                }
                if (contactsEntries != null || contactsEntries.size() != 0) {
                    mContactsId.clear();
                    mContactsName.clear();

                    for (int i = 0; i < contactsEntries.size(); i++) {
                        System.out.println("mPlaceEntry" + i + contactsEntries.get(i).getName());

                        mContactsName.add(contactsEntries.get(i).getName());
                        mContactsId.add(contactsEntries.get(i).getId());
                    }


                ArrayAdapter<String> adapterContacts = new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        mContactsName
                );
                // Specify the layout to use when the list of choices appears
                adapterContacts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                contactNameSpinner.setAdapter(adapterContacts);

                contactNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                            contactId = mContactsId.get(position);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            contactId = -1;
                        }
                    });

                contactNameSpinnerNotify.setAdapter(adapterContacts);

                contactNameSpinnerNotify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                        contactIdNot = mContactsId.get(position);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            contactIdNot = -1;
                        }
                    });
            }
            }

        });
    }
}