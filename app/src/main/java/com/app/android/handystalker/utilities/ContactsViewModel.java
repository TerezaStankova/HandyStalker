package com.app.android.handystalker.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.ContactsEntry;
import com.app.android.handystalker.database.PlaceRepository;

import java.util.List;

public class ContactsViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = ContactsViewModel.class.getSimpleName();

    private LiveData<List<ContactsEntry>> contacts;


    public ContactsViewModel(Application application) {
        super(application);
        PlaceRepository mRepository = new PlaceRepository(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the contacts from the DataBase");
        contacts = mRepository.getmAllContacts();
    }

    public LiveData<List<ContactsEntry>> getContacts() {
        return contacts;
    }
}
