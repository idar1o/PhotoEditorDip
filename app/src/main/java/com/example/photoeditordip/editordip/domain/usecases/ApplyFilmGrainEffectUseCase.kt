package com.example.photoeditordip.editordip.domain.usecases

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyFilmGrainEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(file: File, intensity: Double): Result<String> = repository.applyFilmGrainEffect(file, intensity)
}
