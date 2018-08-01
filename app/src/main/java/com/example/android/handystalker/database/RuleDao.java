package com.example.android.handystalker.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RuleDao {
    @Query("SELECT * FROM rule ORDER BY id")
    LiveData<List<RuleEntry>> loadAllRules();

    //It will ignore the transaction if the same ruleId already exists in DB
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRule(RuleEntry ruleEntry);

    @Query("DELETE FROM rule WHERE id = :ruleId")
    void deleteByRuleId(int ruleId);

    @Query("SELECT * FROM rule WHERE arrival_id=:arrivalId")
    List<RuleEntry> findRulesForArrivalPlace(final int arrivalId);

}