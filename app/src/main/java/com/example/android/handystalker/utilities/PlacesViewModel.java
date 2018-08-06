package com.example.android.handystalker.utilities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.handystalker.database.AppDatabase;
import com.example.android.handystalker.database.PlaceEntry;
import com.example.android.handystalker.database.PlaceRepository;

import java.util.List;

public class PlacesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = PlacesViewModel.class.getSimpleName();

    private PlaceRepository mRepository;
    private LiveData<List<PlaceEntry>> places;


    public PlacesViewModel(Application application) {
        super(application);
        mRepository = new PlaceRepository(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the places from the DataBase");
        places = mRepository.getAllPlaces();
    }

    public LiveData<List<PlaceEntry>> getPlaces() {
        return places;
    }
}

