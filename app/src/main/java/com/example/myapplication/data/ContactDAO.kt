package com.example.myapplication.data

import androidx.room.*

@Dao
interface ContactDAO {

    @Query("SELECT * FROM Contact")
    fun getAllContacts(): List<Contact>

    @Query("SELECT number FROM Contact")
    fun getOnlyNumbers(): List<String>

    @Query("SELECT * FROM Contact WHERE number = :number")
    fun getContact(number: String): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: Contact)
}