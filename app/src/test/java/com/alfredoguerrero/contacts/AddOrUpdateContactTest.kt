package com.alfredoguerrero.contacts

import com.alfredoguerrero.contacts.domain.entities.Contact
import com.alfredoguerrero.contacts.domain.usecases.AddOrUpdateContact
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.times

@OptIn(ExperimentalCoroutinesApi::class)
class AddOrUpdateContactTest {

    private lateinit var mockDataSource: ContactsDataSource
    private lateinit var addOrUpdateContact: AddOrUpdateContact

    @Before
    fun setup() {
        // Crear un mock del DataSource
        mockDataSource = mock()

        // Inicializar el caso de uso con el mock
        addOrUpdateContact = AddOrUpdateContact(mockDataSource)
    }

    @Test
    fun `should call insertContact with correct parameters`() = runTest {
        // Crear un contacto de prueba
        val testContact = Contact(
            id = 1L,
            image = "image_path",
            name = "John",
            lastName = "Doe",
            phoneNumber = 123456789,
            email = "john.doe@example.com",
            notes = "Friend"
        )

        // Ejecutar el caso de uso con el contacto de prueba
        addOrUpdateContact(testContact)

        // Verificar que insertContact se llamó con los parámetros correctos
        verify(mockDataSource, times(1)).insertContact(
            image = testContact.image,
            name = testContact.name,
            lastName = testContact.lastName,
            phoneNumber = testContact.phoneNumber,
            email = testContact.email,
            notes = testContact.notes,
            id = testContact.id
        )
    }

}
