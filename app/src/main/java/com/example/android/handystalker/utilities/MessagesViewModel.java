package com.example.android.handystalker.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.MessagesEntry;
import com.example.android.handystalker.database.PlaceRepository;

import java.util.List;

public class MessagesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MessagesViewModel.class.getSimpleName();

    private LiveData<List<MessagesEntry>> messages;


    public MessagesViewModel(Application application) {
        super(application);
        PlaceRepository mRepository = new PlaceRepository(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the rules from the DataBase");
        messages = mRepository.getmAllMessages();
    }

    public LiveData<List<MessagesEntry>> getMessages() {
        return messages;
    }
}
