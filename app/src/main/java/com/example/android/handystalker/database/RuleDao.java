package com.example.android.handystalker.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RuleDao {
    @Query("SELECT * FROM place ORDER BY id")
    LiveData<List<PlaceEntry>> loadAllPlaces();

    //It will ignore the transaction if the same placeId already exists in DB
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPlace(PlaceEntry placeEntry);

    @Query("DELETE FROM place WHERE place_id = :placeId")
    void deleteByPlaceId(String placeId);

    @Query("SELECT * FROM rule WHERE arrival_id=:arrivalId")
    List<RuleEntry> findRulesForArrivalPlace(final int arrivalId);
}