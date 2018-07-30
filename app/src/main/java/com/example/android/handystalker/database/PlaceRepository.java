package com.example.android.handystalker.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;

import java.util.List;

public class PlaceRepository {

    private PlaceDao mWordDao;
    private LiveData<List<PlaceEntry>> mAllWords;
    private String title;

    public PlaceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mWordDao = db.movieDao();
        mAllWords = mWordDao.loadAllMovies();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<PlaceEntry>> getAllMovies() {
        return mAllWords;
    }
}

