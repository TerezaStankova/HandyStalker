package com.example.android.handystalker.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.handystalker.model.Contact;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY id")
    LiveData<List<ContactsEntry>> loadAllContacts();

    //It will ignore the transaction if the same placeId already exists in DB
    //New Contact will be created
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertContact(ContactsEntry contactsEntry);

    //It will ignore the transaction if the same placeId already exists in DB
    //Contact will be updated
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateContact(ContactsEntry contactsEntry);

    @Query("DELETE FROM contacts WHERE id = :contactId")
    void deleteByContactId(int contactId);

    @Query("SELECT name FROM contacts WHERE id=:contactId")
    String findNameForContactId(final int contactId);

    @Query("SELECT phone FROM contacts WHERE id=:contactId")
    String findPhoneForContactId(final int contactId);

    @Query("SELECT email FROM contacts WHERE id=:contactId")
    String findEmailForContactId(final int contactId);

    @Query("SELECT * FROM contacts WHERE id=:contactId")
    ContactsEntry findContactsEntryfromContactId(final int contactId);
}