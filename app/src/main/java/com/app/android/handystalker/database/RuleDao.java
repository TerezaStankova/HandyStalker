package com.app.android.handystalker.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RuleDao {
    @Query("SELECT * FROM rule ORDER BY id")
    LiveData<List<RuleEntry>> loadAllRules();

    @Query("SELECT * FROM rule  WHERE type=:wifi OR type=:sound OR type=:wifioff OR type=:soundoff ORDER BY id")
    LiveData<List<RuleEntry>> loadHandyRules(String wifi, String sound, String wifioff, String soundoff);

    @Query("SELECT * FROM rule  WHERE type=:sms OR type=:notify ORDER BY id")
    LiveData<List<RuleEntry>> loadSendingRules(String sms, String notify);

    @Query("SELECT * FROM rule  WHERE type=:sms ORDER BY id")
    LiveData<List<RuleEntry>> loadTextRules(String sms);

    @Query("SELECT * FROM rule  WHERE type=:notify ORDER BY id")
    LiveData<List<RuleEntry>> loadNotifyRules(String notify);

    @Query("SELECT * FROM rule  WHERE type=:wifi OR type=:wifioff ORDER BY id")
    LiveData<List<RuleEntry>> loadWifiRules(String wifi, String wifioff);

    @Query("SELECT * FROM rule  WHERE type=:sound OR type=:soundoff ORDER BY id")
    LiveData<List<RuleEntry>> loadSoundRules(String sound, String soundoff);


    //It will ignore the transaction if the same ruleId already exists in DB
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRule(RuleEntry ruleEntry);

    @Query("DELETE FROM rule WHERE id = :ruleId")
    void deleteByRuleId(int ruleId);

    @Query("SELECT * FROM rule WHERE arrival_id=:arrivalId")
    List<RuleEntry> findRulesForArrivalPlace(final Integer arrivalId);

    @Query("SELECT * FROM rule WHERE departure_id=:departureId")
    List<RuleEntry> findRulesForDeparturePlace(final Integer departureId);

    @Query("SELECT id FROM rule WHERE departure_id=:departureId")
    List<Integer> findRulesById(final Integer departureId);

    @Query("SELECT type FROM rule WHERE id=:ruleId")
    String findTypeByRuleId(int ruleId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRule(RuleEntry ruleEntry);

}