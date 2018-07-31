package com.example.android.handystalker.ui;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;

import com.example.android.handystalker.utilities.AppExecutors;

public class NewContactActivity extends AppCompatActivity {

        // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    EditText nameEditText;
    EditText phoneEditText;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact);

        nameEditText = (EditText) findViewById(R.id.name_editText);
        phoneEditText = (EditText) findViewById(R.id.phone_editText);
        emailEditText = (EditText) findViewById(R.id.email_editText);

        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    public void onSaveContactClick(View view) {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (name != null) {
            final ContactsEntry contactEntry = new ContactsEntry(name, phone, email);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // insert new contact
                    mDb.contactDao().insertContact(contactEntry);

                }
            });
        }

        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);

    }
}