package com.alfredoguerrero.contacts.domain.usecases

import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import contacts.ContactsEntity

class ReadContacts(private val local: ContactsDataSource): UseCase<String, List<ContactsEntity>>()  {

    override suspend fun execute(input: String): List<ContactsEntity> {
        return local.getAllContacts().filter { it ->
            it.name.contains(input, ignoreCase = true) ||
            it.lastName.contains(input, ignoreCase = true) ||
            it.email.contains(input, ignoreCase = true) ||
            it.phoneNumber.toString().contains(input, ignoreCase = true) ||
            it.notes.contains(input, ignoreCase = true)
        }
    }

}