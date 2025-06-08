package com.example.photoeditordip.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Creates a temporary file from a Uri
 * @param context Android context
 * @param imageUri Uri of the image
 * @return File or null if couldn't create file
 */
fun createTempFile(context: Context, imageUri: Uri?): File? {
    if (imageUri == null) return null

    return try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Extension to decode a Base64 String to Bitmap
 */
fun String.decodeBase64(): android.graphics.Bitmap? {
    return try {
        val decodedBytes = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
        android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}