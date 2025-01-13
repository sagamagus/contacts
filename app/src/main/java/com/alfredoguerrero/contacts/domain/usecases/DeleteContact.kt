package com.alfredoguerrero.contacts.domain.usecases

import com.alfredoguerrero.contacts.domain.entities.Contact
import com.alfredoguerrero.contacts.framework.data.ContactsDS

class DeleteContact(val local: ContactsDS): UseCase<Long, Unit>()  {
    override suspend fun execute(input: Long) {
        local.deletePersonById(input)
    }
}