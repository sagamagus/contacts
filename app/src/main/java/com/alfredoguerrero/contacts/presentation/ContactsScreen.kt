package com.alfredoguerrero.contacts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.alfredoguerrero.contacts.domain.entities.Contact
import com.alfredoguerrero.contacts.framework.CaptureService
import contacts.ContactsEntity
import java.io.File

@Composable
fun ContactsScreen(
    captureService: CaptureService,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    viewModel.captureService = captureService
    val showAddDialog = remember{ mutableStateOf(false)}
    val isFiltered = remember{ mutableStateOf(false)}
    val contacts = viewModel.contactList
        .collectAsState(initial = emptyList())
        .value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp,46.dp,26.dp,16.dp)
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.searchText, onValueChange = viewModel::onSearchChange,
                    placeholder = { Text(text = "Buscar")})
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(contacts){contact ->
                    ContactItem(
                        contact = contact,
                        onItemClick = {
                            viewModel.getContactById(contact.id)
                        },
                        onDeleteClick = {
                            viewModel.onDeleteClick(contact.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {showAddDialog.value = true}) {
                    Icon(modifier = Modifier.size(50.dp),
                        imageVector = Icons.Outlined.AddCircle,
                        tint = Color.White,
                        contentDescription ="Añadir contacto")
                }
            }
        }
        if (showAddDialog.value){
            Dialog(onDismissRequest = {showAddDialog.value = false}) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Column {
                        Row {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(viewModel.image)
                                    .build(),
                                contentDescription = "icon",
                                contentScale = ContentScale.Inside,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Row {
                            IconButton(onClick = {viewModel.captureImage()}) {
                                Icon(imageVector = Icons.Outlined.AddCircle, contentDescription ="Elegir imagen")
                            }
                        }
                        Row {
                            TextField(value = viewModel.nameText, onValueChange = viewModel::onNameChange,
                                placeholder = { Text(text = "Nombre")})
                        }
                        Row {
                            TextField(value = viewModel.lastNameText, onValueChange = viewModel::onLastNameChange,
                                placeholder = { Text(text = "Apellido")})
                        }
                        Row {
                            TextField(value = viewModel.emailText, onValueChange = viewModel::onEmailChange,
                                placeholder = { Text(text = "Email")})
                        }
                        Row {
                            TextField(value = viewModel.phoneText, onValueChange = viewModel::onPhoneChange,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text(text = "Telefono")})
                        }
                        Row {
                            TextField(value = viewModel.notesText, onValueChange = viewModel::onNotesChange,
                                placeholder = { Text(text = "Notas")})
                        }
                        Row () {
                            IconButton(onClick = {viewModel.onInsertClick()}) {
                                Icon(imageVector = Icons.Outlined.AddCircle, contentDescription ="Añadir contacto")
                            }
                        }

                    }
                }
            }
        }
        viewModel.contactDetails?.let { details ->
            Dialog(onDismissRequest = viewModel::onContactDetailsDialogDismiss) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(details.image)
                        .build(),
                    contentDescription = "icon",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier.size(30.dp)
                    )
                    Text(text = "${details.name} ${details.lastName}")
                    Text(text = details.email)
                    Text(text = details.phoneNumber.toString())
                    Text(text = details.notes)
                }
            }
        }
    }
}


@Composable
fun ContactItem(
    contact: ContactsEntity,
    onItemClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier
){
    Row(
        modifier = modifier.clickable { onItemClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "${contact.name} ${contact.lastName}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        IconButton(onClick = onDeleteClick) {
            Icon(imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete contact",
                tint = Color.Gray)
        }
    }
}