package com.alfredoguerrero.contacts.framework

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alfredoguerrero.contacts.R


class CaptureService {

    private var requestCode =3000
    private var fragment: Fragment? = null
    private var activity: Activity? = null

    @Suppress("UNUSED")
    private constructor()

    constructor(reference: Fragment) { fragment = reference }
    constructor(reference: Activity) { activity = reference }

    fun startCapture(){
        val pickIntent = Intent()
        pickIntent.setType("image/*")
        pickIntent.setAction(Intent.ACTION_GET_CONTENT)

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val pickTitle = when {
            fragment != null -> {
                fragment!!.requireActivity().resources!!.getString(R.string.choose_text)
            }
            activity != null -> {
                activity!!.resources!!.getString(R.string.choose_text)
            }

            else -> {""}
        }
        val chooserIntent = Intent.createChooser(pickIntent, pickTitle)
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            arrayOf(takePhotoIntent)
        )

        when {
            fragment != null -> {
                try {
                    fragment!!.startActivityForResult(chooserIntent, requestCode)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            activity != null -> {
                try {
                    activity!!.startActivityForResult(chooserIntent, requestCode)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun isCameraPermissionGranted(): Boolean {
        return when {
            fragment != null -> {
                ContextCompat.checkSelfPermission(fragment!!.requireContext(),
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            }
            activity != null -> {
                ContextCompat.checkSelfPermission(
                    activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            }
            else -> false
        }
    }

}