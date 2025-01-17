package com.alfredoguerrero.contacts

import com.alfredoguerrero.contacts.domain.usecases.DeleteContact
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.times

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteContactTest {

    private lateinit var mockDataSource: ContactsDataSource
    private lateinit var deleteContact: DeleteContact

    @Before
    fun setup() {
        // Crear un mock del DataSource
        mockDataSource = mock()

        // Inicializar el caso de uso con el mock
        deleteContact = DeleteContact(mockDataSource)
    }

    @Test
    fun `should call deletePersonById with correct id`() = runTest {
        // ID del contacto a eliminar
        val contactId = 123L

        // Ejecutar el caso de uso
        deleteContact(contactId)

        // Verificar que se llamó al método deletePersonById con el ID correcto
        verify(mockDataSource, times(1)).deletePersonById(contactId)
    }
}
