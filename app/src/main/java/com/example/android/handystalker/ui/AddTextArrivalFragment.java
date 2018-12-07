package com.example.android.handystalker.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.MessagesEntry;
import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.utilities.AppExecutors;
import com.example.android.handystalker.utilities.ContactsViewModel;
import com.example.android.handystalker.utilities.MessagesViewModel;
import com.example.android.handystalker.utilities.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.util.ArrayUtils.newArrayList;

public class AddTextArrivalFragment extends Fragment {

    List<Integer> mContactsId = newArrayList();
    List<String> mContactsName = newArrayList();
    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();
    List<String> placeNamesAnywhere = new ArrayList<String>();
    List<Integer> mMessgesIds = newArrayList();
    List<String> mMessagesTexts = newArrayList();

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2222;

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner contactNameSpinner;
    Spinner arrivalSpinner;
    Spinner departureSpinner;
    Spinner messageTextSpinner;
    Integer arrivalId = null;
    Integer departureId = null;
    Integer departureId2 = null;
    Integer contactId = null;
    Integer messageId = null;

    Button saveRuleButton;

    String type = "sms";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_text_arrival_fragment, container, false);

        //SMS for arrival spinners
        contactNameSpinner = rootView.findViewById(R.id.name_spinner);
        arrivalSpinner = rootView.findViewById(R.id.arrival_spinner);
        departureSpinner = rootView.findViewById(R.id.departure_spinner);
        messageTextSpinner = rootView.findViewById(R.id.message_spinner);
        saveRuleButton = rootView.findViewById(R.id.arrival_save_button);
        saveRuleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSaveSendingRuleClick(saveRuleButton);
            }
        });

        mDb = AppDatabase.getInstance(getContext());
        setupPlacesViewModel();
        setupContactsViewModel();
        setupMessagesViewModel();

        return rootView;
    }

    public void onSaveSendingRuleClick(View view) {
        type = "sms";

        //There is a library out there that helps you taking permissions.
        //https://github.com/googlesamples/easypermissions

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {
                // Show an explanation to the user *asynchronously*
            } else {
                // No explanation needed
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            // Permission has already been granted
            String name = (String) contactNameSpinner.getSelectedItem();

            if (name != null && arrivalId != null) {
                final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureId, contactId, messageId, type, false);
                Log.d("rules entred", "r " + arrivalId + departureId + contactId + messageId + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Insert new rule
                        mDb.ruleDao().insertRule(ruleEntry);

                    }
                });
            }

            Intent intent = new Intent(getActivity(), SmsRulesActivity.class);
            startActivity(intent);
        }
    }


    public void saveRule(){

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.SEND_SMS)) {
                } else {
                    // No explanation needed
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            } else {
                // Permission has already been granted
                Log.d("rules entred", "r " + arrivalId + departureId + contactId + type);
                String name = (String) contactNameSpinner.getSelectedItem();
                if (name != null && arrivalId != null) {
                    final RuleEntry ruleEntry = new RuleEntry(arrivalId, departureId, contactId, messageId, type, false);
                    Log.d("rules entred", "r " + arrivalId + departureId + contactId + messageId + type);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Insert new rule
                            mDb.ruleDao().insertRule(ruleEntry);

                        }
                    });
                }

                Intent intent = new Intent(getActivity(), SmsRulesActivity.class);
                startActivity(intent);
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



    private void setupPlacesViewModel() {
        PlacesViewModel viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.getPlaces().observe(this, new Observer<List<PlaceEntry>>() {
            @Override
            public void onChanged(@Nullable List<PlaceEntry> placeEntries) {
                if (placeEntries != null){
                    Log.d("message", "Updating list of places from LiveData in ViewModel"  + placeEntries.size() );
                    if (placeEntries.size() == 0) {
                        return;
                    }
                    placeEntries.size();
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
                            getContext(),
                            R.layout.spinner_item,
                            placeNamesAnywhere
                    );

                    ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(
                            getContext(),
                            R.layout.spinner_item,
                            placeNames
                    );


                    // Specify the layout to use when the list of choices appears
                    adapterPlace.setDropDownViewResource(R.layout.spinner_item);
                    adapterDepartureAnywherePlace.setDropDownViewResource(R.layout.spinner_item);
                    // Apply the adapter to the spinner
                    arrivalSpinner.setAdapter(adapterPlace);

                    departureSpinner.setAdapter(adapterDepartureAnywherePlace);

                    arrivalSpinner.setOnItemSelectedListener(new ArrivalSpinnerClass());
                    departureSpinner.setOnItemSelectedListener(new DepartureAnywhereSpinnerClass());


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
                            getContext(),
                            R.layout.spinner_item,
                            mContactsName
                    );
                    // Specify the layout to use when the list of choices appears
                    adapterContacts.setDropDownViewResource(R.layout.spinner_item);
                    // Apply the adapter to the spinner
                    contactNameSpinner.setAdapter(adapterContacts);


                    contactNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                            contactId = mContactsId.get(position);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            contactId = 0;
                        }
                    });


                }
            }

        });
    }

    private void setupMessagesViewModel() {
        MessagesViewModel viewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        viewModel.getMessages().observe(this, new Observer<List<MessagesEntry>>() {
            @Override
            public void onChanged(@Nullable List<MessagesEntry> messagesEntries) {
                Log.d("message", "Updating list of messages from LiveData in ViewModel"  + messagesEntries.size() );
                if (messagesEntries.size() == 0) {
                    return;
                }
                if (messagesEntries != null || messagesEntries.size() != 0) {
                    mMessgesIds.clear();
                    mMessagesTexts.clear();

                    for (int i = 0; i < messagesEntries.size(); i++) {
                        System.out.println("mPlaceEntry" + i + messagesEntries.get(i).getText());

                        mMessagesTexts.add(messagesEntries.get(i).getText());
                        mMessgesIds.add(messagesEntries.get(i).getId());
                    }


                    ArrayAdapter<String> adapterMessages = new ArrayAdapter<String>(
                            getContext(),
                            R.layout.spinner_item,
                            mMessagesTexts
                    );
                    // Specify the layout to use when the list of choices appears
                    adapterMessages.setDropDownViewResource(R.layout.spinner_item);
                    // Apply the adapter to the spinner
                    messageTextSpinner.setAdapter(adapterMessages);


                    messageTextSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                            messageId = mMessgesIds.get(position);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            messageId = 0;
                        }
                    });


                }
            }

        });
    }
}
