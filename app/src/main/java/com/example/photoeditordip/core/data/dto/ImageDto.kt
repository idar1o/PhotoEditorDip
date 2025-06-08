package com.example.photoeditordip.core.data.dto

// core/data/dto/ImageDto.kt
data class ImageDto(
    val file: String, // Баз64 строка изображения
    val blur_type: String? = "gaussian",
    val kernel_size: Int? = 15,
    val diameter: Int? = 9,
    val sigma_color: Int? = 75,
    val sigma_space: Int? = 75
)