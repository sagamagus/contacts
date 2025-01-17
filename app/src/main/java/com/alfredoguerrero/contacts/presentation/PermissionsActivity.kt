package com.alfredoguerrero.contacts.presentation

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext

class PermissionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            RequestPermissionScreen(
                permission = Manifest.permission.CAMERA,
                onPermissionGranted = {
                    Toast.makeText(
                        LocalContext.current, "Permiso concedido", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}