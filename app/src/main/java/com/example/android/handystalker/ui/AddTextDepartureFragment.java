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
import android.widget.Toast;

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

public class AddTextDepartureFragment extends Fragment {

    List<Integer> mContactsId = newArrayList();
    List<String> mContactsName = newArrayList();
    List<Integer> placeIds = new ArrayList<Integer>();
    List<String> placeNames = new ArrayList<String>();
    List<Integer> mMessagesIds = newArrayList();
    List<String> mMessagesTexts = newArrayList();

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2222;

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner contactNameSpinner;
    Spinner departureSpinner;
    Spinner messageTextSpinner;
    Integer departureId = null;
    Integer contactId = null;
    Integer messageId = null;
    Button saveRuleButton;
    String type = "sms";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_text_departure_fragment, container, false);

        //SMS for arrival spinners
        contactNameSpinner = rootView.findViewById(R.id.name_departure_spinner);
        departureSpinner = rootView.findViewById(R.id.departure_place_rule_spinner);
        messageTextSpinner = rootView.findViewById(R.id.message_departure_spinner);
        saveRuleButton = rootView.findViewById(R.id.departure_rule_button);
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
            Log.d("rules entred", "r " + departureId + contactId + messageId + type);

            if (name != null && departureId != null) {
                final RuleEntry ruleEntry = new RuleEntry(null, departureId, contactId, messageId, type, false);
                Log.d("rules entred", "r " + departureId + contactId + messageId + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Insert new rule
                        mDb.ruleDao().insertRule(ruleEntry);

                    }
                });
            }

            Intent intent = new Intent(getActivity(), TextRulesActivity.class);
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
            Log.d("rules entred", "r " + departureId + contactId + type);
            String name = (String) contactNameSpinner.getSelectedItem();
            if (name != null && departureId != null) {
                final RuleEntry ruleEntry = new RuleEntry(null, departureId, contactId, messageId, type, false);
                Log.d("rules entred", "r " + departureId + contactId + messageId + type);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Insert new rule
                        mDb.ruleDao().insertRule(ruleEntry);

                    }
                });
                Toast.makeText(getContext(), R.string.rule_saved_toast, Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(getActivity(), TextRulesActivity.class);
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

                    for (int i = 0; i < placeEntries.size(); i++) {
                        placeIds.add(placeEntries.get(i).getId());
                        System.out.println("placeIds" + i + placeIds.get(i));
                        placeNames.add(placeEntries.get(i).getPlaceName());
                        System.out.println("placeNames" + i + placeNames.get(i));
                    }


                    Log.d("placeNames", " " + placeNames.get(0));



                    ArrayAdapter<String> adapterPlace = new ArrayAdapter<String>(
                            getContext(),
                            R.layout.spinner_item,
                            placeNames
                    );

                    // Specify the layout to use when the list of choices appears
                    adapterPlace.setDropDownViewResource(R.layout.spinner_item);
                    // Apply the adapter to the spinner

                    departureSpinner.setAdapter(adapterPlace);
                    departureSpinner.setOnItemSelectedListener(new DepartureSpinnerClass());
                }


            }
        });
    }


    class DepartureSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {  departureId = placeIds.get(position);
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
                    mMessagesTexts.add(0, getString(R.string.default_message));
                }

                if (messagesEntries != null || messagesEntries.size() != 0) {
                    mMessagesIds.clear();
                    mMessagesTexts.clear();

                    for (int i = 0; i < messagesEntries.size(); i++) {
                        System.out.println("mMessageEntry" + i + messagesEntries.get(i).getText());

                        mMessagesTexts.add(messagesEntries.get(i).getText());
                        mMessagesIds.add(messagesEntries.get(i).getId());
                    }

                    mMessagesTexts.add(0, getString(R.string.default_message));
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


                messageTextSpinner.setOnItemSelectedListener(new MessageSpinnerClass());
            }

        });
    }

    class MessageSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        { if (position == 0){
            messageId = null;
        } else {
            messageId = mMessagesIds.get(position - 1);}
        }
        public void onNothingSelected(AdapterView<?> parent) {
            messageId = null;
        }
    }
}
