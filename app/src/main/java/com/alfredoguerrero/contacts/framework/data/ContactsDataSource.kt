package com.alfredoguerrero.contacts.framework.data

import contacts.ContactsEntity
import kotlinx.coroutines.flow.Flow

interface ContactsDataSource {
    suspend fun getContactsById(id: Long): ContactsEntity?

    fun getAllContactsFlow(): Flow<List<ContactsEntity>>

    fun getAllContacts(): List<ContactsEntity>

    suspend fun deletePersonById(id: Long)

    suspend fun insertContact(image: String, name: String, lastName: String, phoneNumber: Long, email: String, notes: String, id: Long? = null)
}