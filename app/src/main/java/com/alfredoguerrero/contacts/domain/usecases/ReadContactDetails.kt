package com.alfredoguerrero.contacts.domain.usecases

import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import contacts.ContactsEntity

class ReadContactDetails(private val local: ContactsDataSource): UseCase<Long, ContactsEntity?>() {
    override suspend fun execute(input: Long): ContactsEntity? {
        return local.getContactsById(input)
    }
}