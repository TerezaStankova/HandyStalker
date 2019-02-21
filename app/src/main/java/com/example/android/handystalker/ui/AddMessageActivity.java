package com.example.android.handystalker.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.handystalker.R;
import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.MessagesEntry;
import com.example.android.handystalker.ui.Adapters.MessagesAdapter;
import com.example.android.handystalker.utilities.AppExecutors;
import com.example.android.handystalker.utilities.MessagesViewModel;

import java.util.List;

public class AddMessageActivity extends AppCompatActivity {

    // Member variables
    private MessagesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    
    // Member variable for the Database
    private AppDatabase mDb;

    //Edit texts
    EditText textEditText;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        textEditText = findViewById(R.id.messageText_editText);
        saveButton = findViewById(R.id.save_text_button);

        mDb = AppDatabase.getInstance(getApplicationContext());


        // Set up the recycler view
        mRecyclerView = findViewById(R.id.texts_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessagesAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
        mAdapter.setDatabase(mDb);

        setupViewModel();
    }

    private void showMessagesDataView() {
        /* Then, make sure the messages data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void setupViewModel() {
        showMessagesDataView();
        MessagesViewModel viewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        viewModel.getMessages().observe(this, new Observer<List<MessagesEntry>>() {
            @Override
            public void onChanged(@Nullable List<MessagesEntry> messagesEntries) {
                mAdapter.setContactsFromDatabase(messagesEntries);
                if (messagesEntries != null){
                    Log.d("message", "Updating list of messages from LiveData in ViewModel"  + messagesEntries.size() );
                }
            }

        });
    }


    //After Save button is clicked save the contact
    public void onSaveMessageButtonClick(View view) {

        String text = textEditText.getText().toString();
        
        
        if (text != null) {
            final MessagesEntry messagesEntry = new MessagesEntry(text);
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // insert new contact
                    mDb.messageDao().insertMessage(messagesEntry);

                }
            });
            Toast.makeText(getApplicationContext(), R.string.new_message_toast, Toast.LENGTH_SHORT).show();
        }

    }

}