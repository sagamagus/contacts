package com.alfredoguerrero.contacts.presentation

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
import java.util.ArrayList
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
        private set
    var lastNameText by mutableStateOf("")
        private set
    var phoneText by mutableStateOf("")
        private set
    var emailText by mutableStateOf("")
        private set
    var notesText by mutableStateOf("")
        private set

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

    fun onInsertClick(){
        if (nameText.isBlank() || lastNameText.isBlank() || phoneText.isBlank() || emailText.isBlank() || notesText.isBlank()){
            return
        }
        val contact = Contact(null,image,nameText,lastNameText, phoneText.toLong(), emailText, notesText)
        viewModelScope.launch {
            addOrUpdateContactUC.invoke(contact)
            nameText = ""
            lastNameText = ""
            phoneText = ""
            emailText = ""
            notesText = ""
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
            contacts.clear()
            contacts.addAll(readContactsUC.invoke(searchText))
        }
    }

    fun onContactDetailsDialogDismiss(){
        contactDetails = null
    }

}