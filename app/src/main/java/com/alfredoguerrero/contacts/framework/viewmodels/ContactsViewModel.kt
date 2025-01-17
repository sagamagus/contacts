package com.alfredoguerrero.contacts.framework.viewmodels

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfredoguerrero.contacts.domain.entities.Contact
import com.alfredoguerrero.contacts.domain.usecases.AddOrUpdateContact
import com.alfredoguerrero.contacts.domain.usecases.DeleteContact
import com.alfredoguerrero.contacts.domain.usecases.ReadContactDetails
import com.alfredoguerrero.contacts.domain.usecases.ReadContacts
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import contacts.ContactsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val dataSource: ContactsDataSource
) : ViewModel() {

    private val readContactsUC: ReadContacts by lazy {
        ReadContacts(dataSource)
    }

    private val addOrUpdateContactUC: AddOrUpdateContact by lazy {
        AddOrUpdateContact(dataSource)
    }

    private val deleteContactUC: DeleteContact by lazy {
        DeleteContact(dataSource)
    }

    private val readContactDetailsUC: ReadContactDetails by lazy {
        ReadContactDetails(dataSource)
    }

    var contactList = dataSource.getAllContactsFlow()
    val contacts: ArrayList<ContactsEntity> = arrayListOf()
    var contactDetails by mutableStateOf<ContactsEntity?>(null)
    var isFiltered by mutableStateOf(false)
    var image = ""

    var nameText by mutableStateOf("")
        public set
    var lastNameText by mutableStateOf("")
        public set
    var phoneText by mutableStateOf("")
        public set
    var emailText by mutableStateOf("")
        public set
    var notesText by mutableStateOf("")
        public set

    var searchText by mutableStateOf("")
        private set

    fun onNameChange(value: String){
        nameText = value
    }

    fun onLastNameChange(value: String){
        lastNameText = value
    }

    fun onPhoneChange(value: String){
        phoneText = value
    }

    fun onEmailChange(value: String){
        emailText = value
    }

    fun onNotesChange(value: String){
        notesText = value
    }

    fun onSearchChange(value: String){
        searchText = value
        if (value.equals("")){
            isFiltered = false
        }else{
            isFiltered = true
        }
        getContantcsBySearch()
    }

    fun onInsertClick(context: Context){
        //si alguno de los campos obligatorios no esta, regresamos
        if (nameText.isBlank() || lastNameText.isBlank() || phoneText.isBlank() || emailText.isBlank() || notesText.isBlank()){
            Toast.makeText(context,"Ingresa todos los datos de contacto", Toast.LENGTH_LONG).show()
            return
        }
        //Armamos un objeto Contact para insertar el nuevo contacto
        val contact = Contact(
            contactDetails?.id,
            image,
            nameText,
            lastNameText,
            phoneText.toLong(),
            emailText,
            notesText
        )
        viewModelScope.launch {
            addOrUpdateContactUC.invoke(contact)
            //borramos todos los campos despues de agregar el contacto nuevo
            nameText = ""
            lastNameText = ""
            phoneText = ""
            emailText = ""
            notesText = ""
            image = ""
            //si venimos de editar un contacto, lo volvemos a cargar en contactDetails
            contactDetails?.let { it ->
                getContactById(it.id)
            }
        }

    }

    fun onDeleteClick(id: Long){
        viewModelScope.launch {
            deleteContactUC.invoke(id)
        }
    }

    fun getContactById(id: Long){
        viewModelScope.launch {
            contactDetails = readContactDetailsUC.invoke(id)
        }
    }

    fun getContantcsBySearch(){
        viewModelScope.launch {
            //Eliminamos todos los elementos en la lista de contactos filtrados y añadimos los obtenidos en el caso de uso
            contacts.clear()
            contacts.addAll(readContactsUC.invoke(searchText))
        }
    }

    fun onContactDetailsDialogDismiss(){
        //volvemos  borrar contactDetails para quitar la pantalla de detalles
        contactDetails = null
    }

    fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            // Obtén el InputStream desde el Uri
            val inputStream = context.contentResolver.openInputStream(uri)

            // Crea un archivo único en el almacenamiento privado
            val file = createImageFile(context)

            // Copia el contenido del InputStream al archivo
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Retorna la ruta absoluta del archivo guardado
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) /*renombramos el archivo con el timestamp para evitar duplicidad*/
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) /*Toma la ruta de almacenamiento privado en la carpeta pictures*/
        return File.createTempFile(
            "IMG_${timeStamp}_", /* prefijo del nombre del archivo */
            ".jpg", /* sufijo */
            storageDir /* directorio */
        )
    }
}