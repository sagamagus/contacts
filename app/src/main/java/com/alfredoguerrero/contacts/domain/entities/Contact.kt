package com.alfredoguerrero.contacts.domain.entities

data class Contact(val id: Long, val image:String, val name: String, val lastName: String, val phoneNumber: Long, val email: String, val notes: String)
