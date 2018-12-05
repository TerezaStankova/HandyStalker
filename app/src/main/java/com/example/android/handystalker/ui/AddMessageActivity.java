package com.example.android.handystalker.ui;

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

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.ContactsEntry;
import com.example.android.handystalker.database.MessagesEntry;
import com.example.android.handystalker.ui.Adapters.ContactsAdapter;
import com.example.android.handystalker.ui.Adapters.MessagesAdapter;
import com.example.android.handystalker.utilities.ContactsViewModel;
import com.example.android.handystalker.utilities.MessagesViewModel;

import java.util.List;

public class AddMessageActivity extends AppCompatActivity {

    // Member variables
    private MessagesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);


        // Set up the recycler view
        mRecyclerView = findViewById(R.id.texts_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessagesAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
        mAdapter.setDatabase(mDb);

        //setupViewModel();
    }

    private void showContactsDataView() {
        /* Then, make sure the contacts data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void setupViewModel() {
        showContactsDataView();
        MessagesViewModel viewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        viewModel.getMessages().observe(this, new Observer<List<MessagesEntry>>() {
            @Override
            public void onChanged(@Nullable List<MessagesEntry> messagesEntries) {
                mAdapter.setContactsFromDatabase(messagesEntries);
                if (messagesEntries != null){
                    Log.d("message", "Updating list of contacts from LiveData in ViewModel"  + messagesEntries.size() );
                }
            }

        });
    }


    public void onAddContactButtonClicked(View view) {
        Intent intent = new Intent(this, NewContactActivity.class);
        startActivity(intent);
    }

    public void onSaveMessageButtonClick(View view) {
    }
}