package com.example.android.handystalker.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.PlaceRepository;
import com.example.android.handystalker.database.RuleEntry;

import java.util.List;

public class RulesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = RulesViewModel.class.getSimpleName();

    private PlaceRepository mRepository;
    private LiveData<List<RuleEntry>> rules;


    public RulesViewModel(Application application) {
        super(application);
        mRepository = new PlaceRepository(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        rules = mRepository.getmAllRules();
    }

    public LiveData<List<RuleEntry>> getRules() {
        return rules;
    }
}
