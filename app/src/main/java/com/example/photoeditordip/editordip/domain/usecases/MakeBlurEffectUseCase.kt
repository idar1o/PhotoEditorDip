// domain/usecases/ApplyBlurUseCase.kt
package com.example.photoeditordip.domain.usecase

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyBlurUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(file: File): Result<String> {
        return repository.applyBlurEffect(file)
    }
}