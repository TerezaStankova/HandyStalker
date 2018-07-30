package com.example.android.handystalker.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM place ORDER BY id")
    LiveData<List<PlaceEntry>> loadAllMovies();

    @Insert
    void insertMovie(PlaceEntry movieEntry);

    @Query("SELECT title FROM place WHERE id = :id")
    String titleById(int id);

    @Query("DELETE FROM place WHERE id = :id")
    void deleteById(int id);
}