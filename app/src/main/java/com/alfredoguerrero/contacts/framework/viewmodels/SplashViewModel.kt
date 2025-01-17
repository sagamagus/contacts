package com.alfredoguerrero.contacts.framework.viewmodels

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfredoguerrero.contacts.presentation.ContactsActivity
import com.alfredoguerrero.contacts.presentation.PermissionsActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(): ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000L)
            _isReady.value = true
        }
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun nextActivity(context: Context): Intent{
        return if (hasPermission(context, Manifest.permission.CAMERA)) Intent(context, ContactsActivity::class.java)
        else Intent(context, PermissionsActivity::class.java)

    }
}