package com.alfredoguerrero.contacts.domain.usecases

import com.alfredoguerrero.contacts.domain.entities.Contact
import com.alfredoguerrero.contacts.framework.data.ContactsDS
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource

class DeleteContact(val local: ContactsDataSource): UseCase<Long, Unit>()  {
    override suspend fun execute(input: Long) {
        local.deletePersonById(input)
    }
}