package com.example.myapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Contact
import com.example.myapplication.databinding.ContactLayoutBinding

class ContactsAdapter(private val contacts: List<Contact>, private val recyclerItemClick: (Int) -> Unit): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false)
        return ViewHolder(inflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.binding) {
            name.text = "Full name: ${contact.getFirstName()} ${contact.getLastName()}"
            number.text = "Number: ${contact.getNumber()}"
            email.text = "Email: ${contact.getEmail()}"
            contactLayout.setOnClickListener { recyclerItemClick(position) }
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding = ContactLayoutBinding.bind(itemView)
    }
}