package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.BWNegativeEffectParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyBWNegativeEffectUseCase @Inject constructor(
    private val repository: ImageRepository
): UseCase<BWNegativeEffectParams, String> {
    override suspend operator fun invoke(params: BWNegativeEffectParams): Result<String> {
        return repository.applyBWNegativeEffect(params.file)
    }
}
