package com.example.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

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
    List<String> placeNamesAnywhere = new ArrayList<String>();

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2222;
    private static final int MY_PERMISSIONS_REQUEST_SEND_NOTIFICATIONS = 1111;

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner typeSpinner;
    Spinner contactNameSpinner;
    Spinner contactNameDepSMSSpinner ;
    Spinner arrivalSpinner;
    Spinner departureSpinner;
    Spinner departureSMSPlaceSpinner;

    Spinner contactNameSpinnerNotify;
    Spinner placeNotificationSpinner;

    Integer placeId = null;
    Integer arrivalId = null;
    Integer departureId = null;
    Integer departureId2 = null;
    Integer contactId = null;
    Integer contactId2 = null;
    Integer contactIdNot = null;
    String type = "sms";
    private boolean arrivalNotificationRule = true;
    private boolean depRule = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_stalking_rule);
        setTitle("New Stalking Rule");

        //SMS for arrival spinners
        contactNameSpinner = findViewById(R.id.name_spinner);
        arrivalSpinner = findViewById(R.id.arrival_spinner);
        departureSpinner = findViewById(R.id.departure_spinner);

        //Departure SMS spinners
        departureSMSPlaceSpinner = findViewById(R.id.departure_place_rule_spinner);
        contactNameDepSMSSpinner = findViewById(R.id.name_departure_spinner);

        //Notification spinners
        typeSpinner = findViewById(R.id.type_spinner);
        contactNameSpinnerNotify = findViewById(R.id.name_spinner2);
        placeNotificationSpinner = findViewById(R.id.place_spinner);

        setupTypeSpinner();
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupPlacesViewModel();
        setupContactsViewModel();
    }

    public void onSaveSendingRuleClick(View view) {
        depRule = false;
        type = "sms";


        //There is a library out there that helps you taking permissions.
        //https://github.com/googlesamples/easypermissions

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously*
                } else {
                    // No explanation needed
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            } else {
                // Permission has already been granted
                String name = (String) contactNameSpinner.getSelectedItem();

                if (name != null) {
                    final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureId, contactId, type, false);
                    Log.d("rules entred", "r " + arrivalId + departureId + contactId + type);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Insert new rule
                            mDb.ruleDao().insertRule(ruleEntry);

                        }
                    });
                }

                Intent intent = new Intent(this, SmsRulesActivity.class);
                startActivity(intent);
            }
    }

    public void onSaveSendingRuleDepClick(View view) {
        depRule = true;
        type = "sms";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                // Show an explanation to the user
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            // Permission has already been granted
            String name = (String) contactNameDepSMSSpinner .getSelectedItem();

            if (name != null) {
                final RuleEntry ruleEntry = new RuleEntry(null, departureId2, contactId2, type, false);
                Log.d("rules entred", "r " + arrivalId + departureId + contactId2 + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Insert new rule
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
            final RuleEntry ruleEntry = new RuleEntry(placeId, null, contactIdNot, type, false);
            Log.d("rules entred notify", "r " + placeId + contactIdNot + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert new rule
                    mDb.ruleDao().insertRule(ruleEntry);

                }});
        } else {
            final RuleEntry ruleEntry = new RuleEntry(null, placeId, contactIdNot, type, false);
            Log.d("rules entred notify", "r " + placeId + contactIdNot + type);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert new rule
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                } else {
                    // No explanation needed
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            } else {
                // Permission has already been granted
                Log.d("rules entred", "r " + arrivalId + departureId + contactId + type);
                String name = (String) contactNameSpinner.getSelectedItem();
                final RuleEntry ruleEntry;

                if (!depRule){
                    ruleEntry = new RuleEntry(arrivalId, departureId, contactId, type, false);
                }else{
                    ruleEntry = new RuleEntry(null, departureId2, contactId, type, false);
                }

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Insert new rule
                            mDb.ruleDao().insertRule(ruleEntry);
                        }
                    });

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
                }
            }
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
                arrivalNotificationRule = true;
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
                if (placeEntries != null && placeEntries.size() != 0) {
                    placeIds.clear();
                    placeNames.clear();
                    placeNamesAnywhere.clear();

                    for (int i = 0; i < placeEntries.size(); i++) {
                        placeIds.add(placeEntries.get(i).getId());
                        System.out.println("placeIds" + i + placeIds.get(i));
                        placeNames.add(placeEntries.get(i).getPlaceName());
                        System.out.println("placeNames" + i + placeNames.get(i));
                    }


                    Log.d("placeNames", " " + placeNames.get(0));
                    placeNamesAnywhere.addAll(placeNames);
                    placeNamesAnywhere.add(0, "anywhere");
                    Log.d("placeNames", " " + placeNames.get(0));


                    ArrayAdapter<String> adapterDepartureAnywherePlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            placeNamesAnywhere
                    );

                    ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            placeNames
                    );



                    // Specify the layout to use when the list of choices appears
                    adapterPlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    adapterDepartureAnywherePlace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    arrivalSpinner.setAdapter(adapterPlace);
                    departureSMSPlaceSpinner.setAdapter(adapterPlace);
                    placeNotificationSpinner.setAdapter(adapterPlace);
                    departureSpinner.setAdapter(adapterDepartureAnywherePlace);

                    arrivalSpinner.setOnItemSelectedListener(new ArrivalSpinnerClass());
                    departureSpinner.setOnItemSelectedListener(new DepartureAnywhereSpinnerClass());
                    departureSMSPlaceSpinner.setOnItemSelectedListener(new DepartureSpinnerClass());
                    placeNotificationSpinner.setOnItemSelectedListener(new PlaceSpinnerClass());
                }


            }
        });
    }

    class ArrivalSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {  arrivalId = placeIds.get(position);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            arrivalId = null;
        }
    }

    class DepartureAnywhereSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        { if (position == 0){
            departureId = null;
        } else {
            departureId = placeIds.get(position - 1);}
        }
        public void onNothingSelected(AdapterView<?> parent) {
            departureId = null;
        }
    }

    class DepartureSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {  departureId2 = placeIds.get(position);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            departureId2 = null;
        }
    }

    class PlaceSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {   placeId = placeIds.get(position);
        }
            public void onNothingSelected(AdapterView<?> parent) {
            placeId = null;
        }
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
                contactNameDepSMSSpinner .setAdapter(adapterContacts);

                contactNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                            contactId = mContactsId.get(position);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            contactId = 0;
                        }
                    });

                contactNameDepSMSSpinner .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                            contactId2 = mContactsId.get(position);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            contactId = 0;
                        }
                });

                contactNameSpinnerNotify.setAdapter(adapterContacts);

                contactNameSpinnerNotify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                        contactIdNot = mContactsId.get(position);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            contactIdNot = 0;
                        }
                    });
            }
            }

        });
    }
}