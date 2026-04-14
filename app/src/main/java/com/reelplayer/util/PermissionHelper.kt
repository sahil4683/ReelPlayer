package com.reelplayer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionHelper {

    /**
     * Returns the correct storage permission based on Android version.
     */
    fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO   // Android 13+
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE // Android 12 and below
        }
    }

    fun hasStoragePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            getRequiredPermission()
        ) == PackageManager.PERMISSION_GRANTED
    }
}
