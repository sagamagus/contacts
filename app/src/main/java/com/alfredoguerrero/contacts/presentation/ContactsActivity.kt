package com.alfredoguerrero.contacts.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alfredoguerrero.contacts.presentation.ui.theme.ContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContactsTheme {
                ContactsScreen()
            }
        }
    }
}
