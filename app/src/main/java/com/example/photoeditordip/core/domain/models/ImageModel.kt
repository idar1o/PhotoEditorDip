package com.example.photoeditordip.core.domain.models
// domain/models/ImageModel.kt
data class ImageModel(
    val originalImage: String, // Баз64 строка оригинального изображения
    val editedImage: String? = null // Баз64 строка отредактированного изображения
)