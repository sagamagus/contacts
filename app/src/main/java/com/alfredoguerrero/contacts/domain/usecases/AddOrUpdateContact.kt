package com.alfredoguerrero.contacts.domain.usecases

import com.alfredoguerrero.contacts.domain.entities.Contact
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource

class AddOrUpdateContact(private val local: ContactsDataSource): UseCase<Contact, Unit>()  {
    override suspend fun execute(input: Contact) {
        local.insertContact(input.image, input.name, input.lastName, input.phoneNumber, input.email, input.notes, input.id)
    }

}