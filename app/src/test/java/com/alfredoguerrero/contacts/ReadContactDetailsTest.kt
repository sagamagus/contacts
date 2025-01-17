package com.alfredoguerrero.contacts

import com.alfredoguerrero.contacts.domain.usecases.ReadContactDetails
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import contacts.ContactsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class ReadContactDetailsTest {

    // Mock del DataSource
    private lateinit var mockDataSource: ContactsDataSource

    // Caso de uso bajo prueba
    private lateinit var readContactDetails: ReadContactDetails

    @Before
    fun setup() {
        // Inicializa el mock
        mockDataSource = mock()

        // Inicializa el caso de uso con el mock
        readContactDetails = ReadContactDetails(mockDataSource)
    }

    @Test
    fun `should return contact details when id is valid`() = runTest {
        // Datos simulados
        val contactId = 1L
        val expectedContact = ContactsEntity(
            id = 1L,
            image = "",
            name = "John",
            lastName = "Doe",
            phoneNumber = 123456789,
            email = "john.doe@example.com",
            notes = "Friend from work"
        )

        // Configura el mock para que devuelva el contacto esperado
        whenever(mockDataSource.getContactsById(contactId)).thenReturn(expectedContact)

        // Ejecuta el caso de uso
        val result = readContactDetails(contactId)

        // Verifica que el resultado coincide con lo esperado
        assertEquals(expectedContact, result)
    }

    @Test
    fun `should return null when id is invalid`() = runTest {
        // Datos simulados
        val invalidContactId = 999L

        // Configura el mock para devolver `null` cuando el ID no existe
        whenever(mockDataSource.getContactsById(invalidContactId)).thenReturn(null)

        // Ejecuta el caso de uso
        val result = readContactDetails(invalidContactId)

        // Verifica que el resultado sea `null`
        assertEquals(null, result)
    }
}