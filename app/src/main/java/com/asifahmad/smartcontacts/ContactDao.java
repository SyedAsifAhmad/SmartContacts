package com.asifahmad.smartcontacts;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {

    @Insert
    long insert(Contact contact);

    @Query("SELECT * FROM contacts")
    List<Contact> getAllContacts();

    @Query("DELETE FROM contacts WHERE id = :contactId")
    void deleteById(int contactId);

    @Query("SELECT * FROM contacts WHERE id = :contactId LIMIT 1")
    Contact getContactById(int contactId);

    @Delete
    void delete(Contact contact);
}
