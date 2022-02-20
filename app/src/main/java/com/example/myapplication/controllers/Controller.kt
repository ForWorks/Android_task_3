package com.example.myapplication.controllers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.myapplication.data.Constants
import com.example.myapplication.R
import com.example.myapplication.data.Contact
import com.example.myapplication.data.ContactDAO
import com.example.myapplication.data.ContactDatabase

class Controller(private val context: Context) {

    private lateinit var sharedSettings: SharedPreferences
    private lateinit var sharedEditor: SharedPreferences.Editor
    private lateinit var contactDAO: ContactDAO
    private lateinit var notificationBuilder: NotificationCompat.Builder

    fun checkSharedPreferences(): String? {
        if(sharedSettings.contains(Constants.APP_PREFERENCES_NUMBER))
            return sharedSettings.getString(Constants.APP_PREFERENCES_NUMBER, "").toString()
        return null
    }

    fun checkPermissions(permission: String, activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, arrayOf(permission), Constants.PERMISSION_CODE)
        else
            return true
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL, Constants.NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun addPreferences(string: String) {
        sharedEditor.putString(Constants.APP_PREFERENCES_NUMBER, string).apply()
    }

    fun showNotification(contact: Contact) {
        notificationBuilder
            .setContentTitle(contact.getFirstName())
            .setContentText(contact.getLastName())
        NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_CHANNEL_ID, notificationBuilder.build())
    }

    @SuppressLint("CommitPrefEdits")
    fun init() {
        sharedSettings = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
        sharedEditor = sharedSettings.edit()
        contactDAO = Room.databaseBuilder(context, ContactDatabase::class.java, Constants.DATABASE_NAME).build().contactDAO()
        notificationBuilder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_phone)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    fun makeDialog(listView: ListView): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder
            .setCancelable(true)
            .setView(listView)
            .setTitle("Выберите контакт")
        return  builder.create()
    }

    fun getInfo(contact: Contact): String {
        return "FirstName: ${contact.getFirstName()}\n" +
               "LastName: ${contact.getLastName()}\n" +
               "Number: ${contact.getNumber()}\n" +
               "Email: ${contact.getEmail()}"
    }

    @SuppressLint("Range", "Recycle")
    fun getContacts(): List<Contact> {
        val resolver = context.contentResolver
        val list = mutableListOf<Contact>()
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null)
        while (cursor?.moveToNext() == true) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

            var firstName: String? = null
            var lastName: String? = null
            val nameCursor = resolver.query(
                ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = " + id,
                arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),null)
            if (nameCursor?.moveToNext() == true) with(nameCursor) {
                firstName = getString(getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                lastName = getString(getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                close()
            }

            var email: String? = null
            val emailCursor = resolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null)
            if (emailCursor?.moveToNext() == true) with(emailCursor) {
                email = getString(getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                close()
            }

            var number: String? = null
            val phoneCursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
            if (phoneCursor?.moveToNext() == true) with(phoneCursor) {
                number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                close()
            }
            list.add(Contact(id.toInt(), firstName.toString(), lastName.toString(), number.toString(), email.toString()))
        }
        return list
    }

    fun getOnlyNumbers(): List<String> {
        return contactDAO.getOnlyNumbers()
    }

    fun getAllContacts(): List<Contact> {
        return contactDAO.getAllContacts()
    }

    fun getContact(number: String): Contact? {
        return contactDAO.getContact(number)
    }

    fun insertContact(contact: Contact) {
        return contactDAO.insertContact(contact)
    }
}