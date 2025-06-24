package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyWarmColdUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(file: File, warmth: Double, coldness: Double): Result<String> {
        return repository.applyWarmColdEffect(file, warmth, coldness)
    }
}
