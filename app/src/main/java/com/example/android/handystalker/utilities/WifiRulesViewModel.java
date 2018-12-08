package com.example.android.handystalker.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.PlaceRepository;
import com.example.android.handystalker.database.RuleEntry;

import java.util.List;

public class WifiRulesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = WifiRulesViewModel.class.getSimpleName();

    private LiveData<List<RuleEntry>> rules;


    public WifiRulesViewModel(Application application) {
        super(application);
        PlaceRepository mRepository = new PlaceRepository(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the rules from the DataBase");
        rules = mRepository.getmWifiRules();
    }

    public LiveData<List<RuleEntry>> getRules() {
        return rules;
    }
}
