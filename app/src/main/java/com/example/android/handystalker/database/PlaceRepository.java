package com.example.android.handystalker.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class PlaceRepository {

    private LiveData<List<PlaceEntry>> mAllPlaces;
    private LiveData<List<ContactsEntry>> mAllContacts;
    private LiveData<List<RuleEntry>> mAllRules;
    private LiveData<List<RuleEntry>> mHandyRules;
    private LiveData<List<RuleEntry>> mWiFiRules;
    private LiveData<List<RuleEntry>> mSoundRules;
    private LiveData<List<RuleEntry>> mNotificationRules;
    private LiveData<List<RuleEntry>> mTextRules;
    private LiveData<List<RuleEntry>> mStalkerRules;
    private LiveData<List<MessagesEntry>> mAllMessages;

    public PlaceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        PlaceDao mPlaceDao = db.placeDao();
        mAllPlaces = mPlaceDao.loadAllPlaces();
        ContactDao mContactDao = db.contactDao();
        mAllContacts = mContactDao.loadAllContacts();
        RuleDao mRuleDao = db.ruleDao();
        mAllRules = mRuleDao.loadAllRules();
        mHandyRules = mRuleDao.loadHandyRules("wifi", "sound", "wifioff", "soundoff");
        mStalkerRules = mRuleDao.loadSendingRules("sms", "notify");
        mTextRules = mRuleDao.loadTextRules("sms");
        mNotificationRules = mRuleDao.loadNotifyRules("notify");
        mWiFiRules = mRuleDao.loadWifiRules("wifi", "wifioff");
        mSoundRules = mRuleDao.loadSoundRules("sound", "soundoff");
        MessageDao mMessageDao = db.messageDao();
        mAllMessages = mMessageDao.loadAllMessages();
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
    public LiveData<List<MessagesEntry>> getmAllMessages() {
        return mAllMessages;
    }

    //Returns only rules from Handy category
    public LiveData<List<RuleEntry>> getmHandyRules() {
        return mHandyRules;
    }

    public LiveData<List<RuleEntry>> getmWifiRules() {
        return mWiFiRules;
    }
    public LiveData<List<RuleEntry>> getmTextRules() {
        return mTextRules;
    }
    public LiveData<List<RuleEntry>> getmSoundRules() {
        return mSoundRules;
    }
    public LiveData<List<RuleEntry>> getmNotificationRules() {
        return mNotificationRules;
    }

    //Returns only Slaking rules - sms and notifications
    public LiveData<List<RuleEntry>> getmStalkerRules() {
        return mStalkerRules;
    }
}



