package com.example.photoeditordip.editordip.domain.usecases

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplySolarizeEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(file: File, threshold: Int): Result<String> {
        return repository.applySolarizeEffect(file, threshold)
    }
}
