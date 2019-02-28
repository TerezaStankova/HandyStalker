package com.example.android.handystalker.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY id")
    LiveData<List<MessagesEntry>> loadAllMessages();

    //It will ignore the transaction if the same MessageText already exists in DB
    //New Message will be created
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMessage(MessagesEntry messagesEntry);

    //It will ignore the transaction if the same MessageText already exists in DB
    //Message will be updated
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMessage(MessagesEntry messagesEntry);

    @Query("DELETE FROM messages WHERE id = :messageId")
    void deleteByMessageId(Integer messageId);

    @Query("SELECT text FROM messages WHERE id=:messageId")
    String findTextForMessageId(final Integer messageId);

    @Query("SELECT * FROM messages WHERE id=:messageId")
    MessagesEntry findMessagesEntryFromMessageId(final Integer messageId);
}
