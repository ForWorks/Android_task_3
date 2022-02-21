package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ContactsAdapter
import com.example.myapplication.controllers.Controller
import com.example.myapplication.data.Constants
import com.example.myapplication.data.Contact
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        private var contactsList: MutableList<Contact> = mutableListOf()
        private var info: String? = null
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: AlertDialog
    private lateinit var listView: ListView
    private lateinit var adapter: ContactsAdapter
    private lateinit var controller: Controller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = Controller(this)
        listView = ListView(this)
        dialog = controller.makeDialog(listView)
        controller.init()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            controller.createNotificationChannel()

        init()
        setListeners()
    }

    private fun init() {
        binding.contacts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ContactsAdapter(contactsList) {
            CoroutineScope(Dispatchers.IO).launch {
                controller.insertContact(contactsList[it])
            }
            Toast.makeText(this, "Сохранение успешно", Toast.LENGTH_LONG).show()
        }
        binding.contacts.adapter = adapter
        binding.contactInfo.text = info
    }

    private fun setListeners() {
        binding.notificationBtn.setOnClickListener {
            val number = controller.checkSharedPreferences()
            if(number != null)
                CoroutineScope(Dispatchers.IO).launch {
                    controller.showNotification(controller.getContact(number)!!)
                }
            else
                Toast.makeText(this, "Пусто", Toast.LENGTH_LONG).show()
        }

        binding.showSPButton.setOnClickListener {
            val number = controller.checkSharedPreferences()
            if(number != null)
                Snackbar.make(binding.root, number, Snackbar.LENGTH_LONG)
                    .setAction("Закрыть") {}
                    .show()
            else
                Toast.makeText(this, "Пусто", Toast.LENGTH_LONG).show()
        }

        binding.chooseBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(controller.checkPermissions(Manifest.permission.READ_CONTACTS, this)) {
                    fillRecycler()
                }
            } else
                fillRecycler()
        }

        binding.showBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val numbersList = controller.getOnlyNumbers()
                runOnUiThread {
                    listView.adapter = ArrayAdapter(applicationContext, R.layout.custom_list_view, numbersList)
                    dialog.show()
                }
            }
        }

        listView.setOnItemClickListener { _, _, i, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                val contacts = controller.getAllContacts()
                runOnUiThread {
                    info = controller.getInfo(contacts[i])
                    binding.contactInfo.text = info
                    controller.addPreferences(contacts[i].getNumber())
                    dialog.hide()
                }
            }
        }
    }

    private fun fillRecycler() {
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = controller.getContacts()
            runOnUiThread {
                contactsList.clear()
                contactsList.addAll(contacts)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == Constants.PERMISSION_CODE)
            fillRecycler()
    }
}

