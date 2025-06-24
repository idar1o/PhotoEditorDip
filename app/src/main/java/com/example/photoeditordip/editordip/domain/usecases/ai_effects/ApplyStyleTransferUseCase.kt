package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyStyleTransferUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(contentFile: File, styleFile: File): Result<String> {
        return repository.applyStyleTransferEffect(contentFile, styleFile)
    }
}