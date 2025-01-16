package com.alfredoguerrero.contacts.presentation

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import contacts.ContactsEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val showAddDialog = remember { mutableStateOf(false) }
    val contactDetails = viewModel.contactDetails // Observa este estado directamente
    val contacts =if (viewModel.isFiltered) viewModel.contacts else viewModel.contactList.collectAsState(initial = emptyList()).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 46.dp, 26.dp, 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.searchText,
                    onValueChange = viewModel::onSearchChange,
                    placeholder = { Text(text = "Buscar") }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(contacts) { contact ->
                    ContactItem(
                        contact = contact,
                        onItemClick = { viewModel.getContactById(contact.id) },
                        onDeleteClick = { viewModel.onDeleteClick(contact.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showAddDialog.value = true }) {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Icons.Outlined.AddCircle,
                        tint = Color.White,
                        contentDescription = "Añadir contacto"
                    )
                }
            }
        }
    }

    // Add Contact Dialog
    if (showAddDialog.value) {
        AddOrEditContactDialog(
            onDismiss = { showAddDialog.value = false },
            viewModel = viewModel
        )
    }

    // Contact Details Dialog
    contactDetails?.let { details ->
        ContactDetailsDialog(
            details = details,
            viewModel = viewModel
        )
    }
}

@Composable
fun AddOrEditContactDialog(
    onDismiss: () -> Unit,
    viewModel: ContactsViewModel
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    var showPhoto = remember { mutableStateOf(false) }

    if (viewModel.contactDetails != null) {
        if (!viewModel.contactDetails!!.image.equals("")){
            showPhoto.value = true
            viewModel.image = viewModel.contactDetails!!.image
        }
        viewModel.nameText = viewModel.contactDetails!!.name
        viewModel.lastNameText = viewModel.contactDetails!!.lastName
        viewModel.emailText = viewModel.contactDetails!!.email
        viewModel.phoneText = viewModel.contactDetails!!.phoneNumber.toString()
        viewModel.notesText = viewModel.contactDetails!!.notes
    }

    // Launcher for gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = copyUriToInternalStorage(context, it)
            viewModel.image = savedPath ?: ""
        }
        selectedImageUri = uri
        showPhoto.value = false
    }

    // Launcher for camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraImageUri.value
            showPhoto.value = false
        }
    }

    // Function to create a temporary file for the camera image
    val createImageUri = {
        val file = createImageFile(context)
        viewModel.image = file.absolutePath
        cameraImageUri.value = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        cameraImageUri.value
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                }

                if (showPhoto.value) {
                    viewModel.contactDetails?.let { it ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it.image)
                                .build(),
                            contentDescription = "Icono del contacto",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                IconTextButton(
                    text = "Seleccionar de galería",
                    icon = Icons.Outlined.Face,
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.padding(4.dp)
                )

                IconTextButton(
                    text = "Tomar foto",
                    icon = Icons.Outlined.Face,
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                    val uri = createImageUri()
                    uri?.let { cameraLauncher.launch(it) }
                })
                Spacer(modifier = Modifier.height(8.dp))
                IconTextField(
                    value = viewModel.nameText,
                    onValueChange = viewModel::onNameChange,
                    label = "Nombre",
                    isPassword = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    icon = Icons.Outlined.AccountCircle

                )
                Spacer(modifier = Modifier.height(8.dp))
                IconTextField(
                    value = viewModel.lastNameText,
                    onValueChange = viewModel::onLastNameChange,
                    label = "Apellido",
                    isPassword = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    icon = Icons.Outlined.AccountBox
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconTextField(
                    value = viewModel.emailText,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email",
                    isPassword = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    icon = Icons.Outlined.Email
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconTextField(
                    value = viewModel.phoneText,
                    onValueChange = viewModel::onPhoneChange,
                    label = "Teléfono",
                    isPassword = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    icon = Icons.Outlined.Phone
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconTextField(
                    value = viewModel.notesText,
                    onValueChange = viewModel::onNotesChange,
                    label = "Notas",
                    isPassword = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    icon = Icons.Outlined.Create
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconTextButton(
                    text = "Añadir contacto",
                    icon = Icons.Outlined.AddCircle,
                    onClick = {
                        viewModel.onInsertClick()
                        onDismiss()
                              },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "IMG_${timeStamp}_", /* prefijo del nombre del archivo */
        ".jpg", /* sufijo */
        storageDir /* directorio */
    )
}

fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        // Obtén el InputStream desde el Uri
        val inputStream = context.contentResolver.openInputStream(uri)

        // Crea un archivo único en el almacenamiento privado
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_${timeStamp}.jpg"
        val file = File(context.filesDir, fileName)

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

@Composable
fun ContactDetailsDialog(
    details: ContactsEntity,
    viewModel: ContactsViewModel
) {
    val showEditDialog = remember { mutableStateOf(false) }
    Dialog(onDismissRequest = viewModel::onContactDetailsDialogDismiss) {
        if (!showEditDialog.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    details.image.let { path ->
                        if (!path.equals("")) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(path)
                                    .build(),
                                contentDescription = "Icono del contacto",
                                contentScale = ContentScale.Inside,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${details.name} ${details.lastName}",
                    textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = details.email,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = details.phoneNumber.toString(),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = details.notes,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    IconTextButton(
                        text = "Editar contacto",
                        icon = Icons.Outlined.Create,
                        onClick = { showEditDialog.value = true },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        if (showEditDialog.value) {
            AddOrEditContactDialog(
                onDismiss = { showEditDialog.value = false },
                viewModel = viewModel
            )
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

@Composable
fun IconTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp)) // Espaciado entre el ícono y el texto
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun IconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .border(1.dp, if (isFocused) Color.Blue else Color.Gray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}