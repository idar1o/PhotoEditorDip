package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.BlurEffectParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyBlurBackgroundUseCase @Inject constructor(
    private val repository: ImageRepository
): UseCase<BlurEffectParams, String> {
    override suspend operator fun invoke(params: BlurEffectParams): Result<String> {
        return repository.applyBlurBackgroundEffect(params.file, params.blurIntensity)
    }
}