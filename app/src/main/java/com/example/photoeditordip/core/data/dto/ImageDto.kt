package com.example.photoeditordip.core.data.dto

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

// core/data/dto/ImageDto.kt
data class ImageDto(
    val file: String, // Баз64 строка изображения
    val blur_type: String? = "gaussian",
    val kernel_size: Int? = 15,
    val diameter: Int? = 9,
    val sigma_color: Int? = 75,
    val sigma_space: Int? = 75
)

fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
    val file = File(context.getExternalFilesDir(null), fileName)
    FileOutputStream(file).use { outputStream ->
        // PNG сохраняет без потерь и без деформации
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
    }
    return file
}
