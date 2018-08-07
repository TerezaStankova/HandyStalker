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

    private RuleDao mRuleDao;
    private LiveData<List<RuleEntry>> mAllRules;
    private LiveData<List<RuleEntry>> mHandyRules;
    private LiveData<List<RuleEntry>> mStalkerRules;

    public PlaceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mPlaceDao = db.placeDao();
        mAllPlaces = mPlaceDao.loadAllPlaces();
        mContactDao = db.contactDao();
        mAllContacts = mContactDao.loadAllContacts();
        mRuleDao = db.ruleDao();
        mAllRules = mRuleDao.loadAllRules();
        mHandyRules = mRuleDao.loadHandyRules("wifi", "net", "sound", "wifioff", "netoff", "soundoff");
        mStalkerRules = mRuleDao.loadSendingRules("sms", "notify");
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<PlaceEntry>> getAllPlaces() {
        return mAllPlaces;
    }

    public LiveData<List<ContactsEntry>> getmAllContacts() {
        return mAllContacts;
    }

    public LiveData<List<RuleEntry>> getmAllRules() {
        return mAllRules;
    }

    public LiveData<List<RuleEntry>> getmHandyRules() {
        return mHandyRules;
    }

    public LiveData<List<RuleEntry>> getmStalkerRules() {
        return mStalkerRules;
    }
}



