package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject


class ApplyCannyEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(file: File, threshold1: Int, threshold2: Int): Result<String> = repository.applyCannyEffect(file, threshold1, threshold2)
}