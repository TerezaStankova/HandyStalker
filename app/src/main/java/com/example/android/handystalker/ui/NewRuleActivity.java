package com.example.android.handystalker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.RuleEntry;
import com.example.android.handystalker.utilities.AppExecutors;

public class NewRuleActivity  extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Member variable for the Database
    private AppDatabase mDb;

    //edit texts
    Spinner typeSpinner;
    Spinner contactNameSpinner;
    Spinner arrivalSpinner;
    Spinner departureSpinner;

    int arrivalId;
    int departureId;
    int contactId;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_rule);

        typeSpinner = (Spinner) findViewById(R.id.type_spinner);
        contactNameSpinner = (Spinner) findViewById(R.id.name_spinner);
        arrivalSpinner = (Spinner) findViewById(R.id.arrival_spinner);
        departureSpinner = (Spinner) findViewById(R.id.departure_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(this);



        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    public void onSaveRuleClick(View view) {

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

    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        switch (position) {
            case 0:
                // Order by popularity when the popularity menu item is clicked
                type = "sms";
                break;
            case 1:
                // Order by top rated when the top rated menu item is clicked
                type = "email";
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        type = "sms";
    }

}