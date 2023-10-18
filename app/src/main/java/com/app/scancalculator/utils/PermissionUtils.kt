package com.app.scancalculator.utils

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.scancalculator.ui.MainActivity.Companion.RC_CAMERA
import com.app.scancalculator.ui.MainActivity.Companion.RC_MEDIA_IMAGE
import com.app.scancalculator.ui.MainActivity.Companion.RC_EXTERNAL_STORAGE

/**
 * permission utils to check permission status with callback parameter to trigger if granted
 * */
object PermissionUtils {

    fun checkCameraPermission(activity: Activity, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(activity, CAMERA) == PERMISSION_GRANTED) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(CAMERA), RC_CAMERA);
        }
    }

    fun checkExternalStoragePermission(activity: Activity, onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, READ_MEDIA_IMAGES) == PERMISSION_GRANTED) {
                onGranted()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(READ_MEDIA_IMAGES), RC_MEDIA_IMAGE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                onGranted()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(READ_EXTERNAL_STORAGE), RC_EXTERNAL_STORAGE);
            }
        }
    }
}