package com.app.android.handystalker.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.app.android.handystalker.database.AppDatabase;
import com.app.android.handystalker.database.PlaceRepository;
import com.app.android.handystalker.database.RuleEntry;

import java.util.List;

public class NotificationRulesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = NotificationRulesViewModel.class.getSimpleName();

    private LiveData<List<RuleEntry>> rules;


    public NotificationRulesViewModel(Application application) {
        super(application);
        PlaceRepository mRepository = new PlaceRepository(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the rules from the DataBase");
        rules = mRepository.getmNotificationRules();
    }

    public LiveData<List<RuleEntry>> getRules() {
        return rules;
    }
}
