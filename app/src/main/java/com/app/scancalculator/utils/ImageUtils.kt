package com.app.scancalculator.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.IOException

object ImageUtils {
    /**
     * handle image URI from gallery to covert to bitmap
     * */
    fun uriToBitmap(context: Context, uri: Uri?): Bitmap? {
        return try {
            val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}