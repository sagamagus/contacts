package com.alfredoguerrero.contacts.framework.data

import android.media.Image
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.alfredoguerrero.contacts.ContactsDatabase
import contacts.ContactsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class ContactsDS( db: ContactsDatabase): ContactsDataSource {

    private val queries = db.contactsQueries

    override suspend fun getContactsById(id: Long): ContactsEntity? {
        return withContext(Dispatchers.IO) {
            queries.getContactById(id).executeAsOneOrNull()
        }
    }

    override fun getAllContactsFlow(): Flow<List<ContactsEntity>> {
        return queries.getAllContacts().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getAllContacts(): List<ContactsEntity> {
        return queries.getAllContacts().executeAsList()
    }

    override suspend fun deletePersonById(id: Long) {
        withContext(Dispatchers.IO){
            queries.deleteContactById(id)
        }
    }

    override suspend fun insertContact(
        image: String,
        name: String,
        lastName: String,
        phoneNumber: Long,
        email: String,
        notes: String,
        id: Long?
    ) {
        withContext(Dispatchers.IO){
            queries.insertContact(id, image, name, lastName, phoneNumber, email, notes)
        }
    }
}