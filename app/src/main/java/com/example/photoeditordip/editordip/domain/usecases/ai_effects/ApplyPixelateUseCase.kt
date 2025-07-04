package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyPixelateEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(file: File, blockSize: Int): Result<String> = repository.applyPixelateEffect(file, blockSize)
}