package com.example.android.handystalker.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;

import java.util.List;

public class PlaceRepository {

    private PlaceDao mPlaceDao;
    private LiveData<List<PlaceEntry>> mAllPlaces;

    private ContactDao mContactDao;
    private LiveData<List<ContactsEntry>> mAllContacts;

    public PlaceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mPlaceDao = db.placeDao();
        mAllPlaces = mPlaceDao.loadAllPlaces();
        mContactDao = db.contactDao();
        mAllContacts = mContactDao.loadAllContacts();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<PlaceEntry>> getAllPlaces() {
        return mAllPlaces;
    }

    public LiveData<List<ContactsEntry>> getmAllContacts() {
        return mAllContacts;
    }
}



