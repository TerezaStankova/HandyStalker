package com.app.android.handystalker.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.ContactsEntry;
import com.app.android.handystalker.model.Contact;
import com.app.android.handystalker.utilities.AppExecutors;

public class NewContactActivity extends AppCompatActivity {

    // Member variable for the Database
    private AppDatabase mDb;

    //Edit texts
    EditText nameEditText;
    EditText phoneEditText;
    Button saveButton;

    final int RESULT_PICK_CONTACT = 789;
    private static final int MY_PERMISSIONS_REQUEST_CONTACT = 452;
    private Contact mContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact);

        nameEditText = findViewById(R.id.name_editText);
        phoneEditText = findViewById(R.id.phone_editText);
        saveButton = findViewById(R.id.save_contact_button);

        String CONTACTS = "contacts";
        if (getIntent().getParcelableExtra(CONTACTS) != null){
            mContact = getIntent().getParcelableExtra(CONTACTS);
            saveButton.setText(R.string.update_contact);
            nameEditText.setText(mContact.getName());
            phoneEditText.setText(mContact.getPhone());
            saveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onUpdateContactClick(v);
                }
            });
        }

        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    //After Save button is clicked save the contact
    public void onSaveContactClick(View view) {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        if (name == null || name.length() < 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.type_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (phone == null || phone.length() < 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.type_phone), Toast.LENGTH_LONG).show();
            return;
        }


            final ContactsEntry contactEntry = new ContactsEntry(name, phone);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert new contact
                    mDb.contactDao().insertContact(contactEntry);

                }
            });
            Toast.makeText(getApplicationContext(), R.string.new_stalker_toast, Toast.LENGTH_LONG).show();


        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);

    }

    //After Update button is clicked update the contact
    public void onUpdateContactClick(View view) {

        final String name = nameEditText.getText().toString();
        final String phone = phoneEditText.getText().toString();

        if (name == null || name.length() < 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.type_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (phone == null || phone.length() < 1) {
            Toast.makeText(getApplicationContext(), getString(R.string.type_phone), Toast.LENGTH_LONG).show();
            return;
        }


            final Integer contactsId = mContact.getContactId();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Update contact
                    ContactsEntry contactEntry = mDb.contactDao().findContactsEntryfromContactId(contactsId);
                    contactEntry.setName(name);
                    contactEntry.setPhone(phone);
                    mDb.contactDao().updateContact(contactEntry);
                }
            });
            Toast.makeText(getApplicationContext(), R.string.stalker_update_toast, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
    }


    public void onAddFromContactsClicked(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                // No explanation needed
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_CONTACT);
            }
        } else {
            // Permission has already been granted
          addFromContacts();
        }
    }


    private void addFromContacts(){
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    addFromContacts();
                }
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    try {
                        Uri uri = data.getData();
                        if (uri != null){
                            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME));
                                String phoneNumber = cursor.getString(phoneIndex);
                                nameEditText.setText(name);
                                phoneEditText.setText(phoneNumber);
                                cursor.close();}}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_contact), Toast.LENGTH_LONG).show();
        }
    }
}