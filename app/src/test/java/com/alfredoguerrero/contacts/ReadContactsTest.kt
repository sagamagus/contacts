package com.alfredoguerrero.contacts

import com.alfredoguerrero.contacts.domain.usecases.ReadContacts
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import contacts.ContactsEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ReadContactsTest {

    private lateinit var mockDataSource: ContactsDataSource
    private lateinit var readContacts: ReadContacts

    @Before
    fun setup() {
        // Crear un mock del DataSource
        mockDataSource = mock()

        // Inicializar el caso de uso con el mock
        readContacts = ReadContacts(mockDataSource)
    }

    @Test
    fun `should return filtered contacts based on input`() = runTest {
        // Lista de contactos simulada
        val contacts = listOf(
            ContactsEntity(1L, "", "John", "Doe", 123456789, "john.doe@example.com", "Friend"),
            ContactsEntity(2L, "","Jane", "Smith", 987654321, "jane.smith@example.com", "Colleague"),
            ContactsEntity(3L, "","Alice", "Johnson", 456789123, "alice.j@example.com", "Neighbor")
        )

        // Configurar el mock para devolver la lista de contactos
        whenever(mockDataSource.getAllContacts()).thenReturn(contacts)

        // Ejecutar el caso de uso con un filtro que coincida con "Jane"
        val result = readContacts("Jane")

        // Verificar el resultado esperado
        assertEquals(1, result.size)
        assertEquals("Jane", result.first().name)
    }

    @Test
    fun `should return empty list when no contacts match`() = runTest {
        // Lista de contactos simulada
        val contacts = listOf(
            ContactsEntity(1L, "","John", "Doe", 123456789, "john.doe@example.com", "Friend"),
            ContactsEntity(2L, "","Jane", "Smith", 987654321, "jane.smith@example.com", "Colleague")
        )

        // Configurar el mock para devolver la lista de contactos
        whenever(mockDataSource.getAllContacts()).thenReturn(contacts)

        // Ejecutar el caso de uso con un filtro que no coincida
        val result = readContacts("NotFound")

        // Verificar que el resultado es una lista vacía
        assertEquals(0, result.size)
    }

    @Test
    fun `should handle empty contacts list`() = runTest {
        // Configurar el mock para devolver una lista vacía
        whenever(mockDataSource.getAllContacts()).thenReturn(emptyList())

        // Ejecutar el caso de uso con cualquier filtro
        val result = readContacts("Any")

        // Verificar que el resultado es una lista vacía
        assertEquals(0, result.size)
    }
}
