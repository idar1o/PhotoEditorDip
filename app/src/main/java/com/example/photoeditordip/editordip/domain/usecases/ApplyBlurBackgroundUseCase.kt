package com.example.photoeditordip.editordip.domain.usecases

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyBlurBackgroundUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(file: File): Result<String> {
        return repository.applyBlurBackgroundEffect(file)
    }
}