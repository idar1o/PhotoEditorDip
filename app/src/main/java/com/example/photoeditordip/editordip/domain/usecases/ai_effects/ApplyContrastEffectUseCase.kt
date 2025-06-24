package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.ContrastEffectParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject


class ApplyContrastEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) : UseCase<ContrastEffectParams, String> {
    override suspend operator fun invoke(param: ContrastEffectParams): Result<String> {
        return repository.applyContrastEffect(param.file, param.contrast)
    }
}
