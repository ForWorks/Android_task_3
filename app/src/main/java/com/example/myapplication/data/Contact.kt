package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact")
class Contact(@PrimaryKey private val id: Int, private val firstName: String, private val lastName: String, private val number: String, private val email: String)
{
    fun getId(): Int { return id }
    fun getFirstName(): String { return firstName }
    fun getLastName(): String { return lastName }
    fun getNumber(): String { return number }
    fun getEmail(): String { return email }
}