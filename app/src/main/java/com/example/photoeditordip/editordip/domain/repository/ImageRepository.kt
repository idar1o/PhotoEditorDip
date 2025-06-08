package com.example.photoeditordip.editordip.domain.repository

import java.io.File

interface ImageRepository {
    suspend fun applyBlurEffect(imageFile: File): Result<String>
    suspend fun applyBlurBackgroundEffect(imageFile: File): Result<String>
}