package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

interface UseCase <IN, OUT> {
    suspend operator fun invoke(param: IN): Result<OUT>
}

data class ContrastEffectParams(val file: File, val contrast: Int)

class ApplyContrastEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) : UseCase<ContrastEffectParams, String> {
    override suspend operator fun invoke(param: ContrastEffectParams): Result<String> {
        return repository.applyContrastEffect(param.file, param.contrast)
    }
}
