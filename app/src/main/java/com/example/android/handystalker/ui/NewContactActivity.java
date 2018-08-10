package com.example.android.handystalker.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;

import com.example.android.handystalker.model.Contact;
import com.example.android.handystalker.utilities.AppExecutors;

public class NewContactActivity extends AppCompatActivity {

    // Member variable for the Database
    private AppDatabase mDb;

    //Edit texts
    EditText nameEditText;
    EditText phoneEditText;
    Button saveButton;

    final int RESULT_PICK_CONTACT = 789;
    private Contact mComtact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact);

        nameEditText = findViewById(R.id.name_editText);
        phoneEditText = findViewById(R.id.phone_editText);
        saveButton = findViewById(R.id.save_contact_button);

        String CONTACTS = "contacts";
        if (getIntent().getParcelableExtra(CONTACTS) != null){
            mComtact = getIntent().getParcelableExtra(CONTACTS);
            saveButton.setText(R.string.update_contact);
            nameEditText.setText(mComtact.getName());
            phoneEditText.setText(mComtact.getPhone());
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

        if (name != null) {
            final ContactsEntry contactEntry = new ContactsEntry(name, phone, null);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // insert new contact
                    mDb.contactDao().insertContact(contactEntry);

                }
            });
            Toast.makeText(getApplicationContext(), R.string.new_stalker_toast, Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);

    }

    //After Update button is clicked update the contact
    public void onUpdateContactClick(View view) {

        final String name = nameEditText.getText().toString();
        final String phone = phoneEditText.getText().toString();


        if (name != null) {
            final Integer contactsId = mComtact.getContactId();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // update contact
                    ContactsEntry contactEntry = mDb.contactDao().findContactsEntryfromContactId(contactsId);
                    contactEntry.setName(name);
                    contactEntry.setPhone(phone);
                    mDb.contactDao().updateContact(contactEntry);
                }
            });
            Toast.makeText(getApplicationContext(), R.string.stalker_update_toast, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);

    }
    }


    public void onAddFromContactsClicked(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Cursor cursor = null;
                    try {
                        String phoneNumber = null ;
                        String name = null;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME));
                        phoneNumber = cursor.getString(phoneIndex);
                        nameEditText.setText(name);
                        phoneEditText.setText(phoneNumber);
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("NewContact", "No contact added");
        }
    }


}