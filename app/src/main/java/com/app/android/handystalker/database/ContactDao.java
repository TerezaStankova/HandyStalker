package com.app.android.handystalker.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name")
    LiveData<List<ContactsEntry>> loadAllContacts();

    //It will ignore the transaction if the same contact already exists in DB
    //New Contact will be created
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertContact(ContactsEntry contactsEntry);

    //It will replace the contact if the same contactId already exists in DB
    //Contact will be updated
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateContact(ContactsEntry contactsEntry);

    @Query("DELETE FROM contacts WHERE id = :contactId")
    void deleteByContactId(Integer contactId);

    @Query("SELECT name FROM contacts WHERE id=:contactId")
    String findNameForContactId(final Integer contactId);

    @Query("SELECT phone FROM contacts WHERE id=:contactId")
    String findPhoneForContactId(final Integer contactId);

    @Query("SELECT COUNT(*) FROM contacts")
    Integer countContacts();

    @Query("SELECT * FROM contacts WHERE id=:contactId")
    ContactsEntry findContactsEntryfromContactId(final Integer contactId);
}