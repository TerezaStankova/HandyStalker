package com.app.android.handystalker.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.app.android.handystalker.R;
import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.ContactsEntry;
import com.app.android.handystalker.ui.Adapters.ContactsAdapter;
import com.app.android.handystalker.utilities.ContactsViewModel;

import java.util.List;

public class ContactsActivity  extends AppCompatActivity {

    // Member variables
    private ContactsAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);


        // Set up the recycler view
        mRecyclerView = findViewById(R.id.contacts_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactsAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
        mAdapter.setDatabase(mDb);

        setupViewModel();
    }

    private void showContactsDataView() {
        /* Then, make sure the contacts data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void setupViewModel() {
        showContactsDataView();
        ContactsViewModel viewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        viewModel.getContacts().observe(this, new Observer<List<ContactsEntry>>() {
            @Override
            public void onChanged(@Nullable List<ContactsEntry> contactsEntries) {
                mAdapter.setContactsFromDatabase(contactsEntries);
                if (contactsEntries != null){
                Log.d("message", "Updating list of contacts from LiveData in ViewModel"  + contactsEntries.size() );
                }
                }

        });
    }


    public void onAddContactButtonClicked(View view) {
        Intent intent = new Intent(this, NewContactActivity.class);
        startActivity(intent);
    }
}