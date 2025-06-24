package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.BrightnessEffectParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyBrightnessEffectUseCase @Inject constructor(
    private val repository: ImageRepository
): UseCase<BrightnessEffectParams, String> {
    override suspend operator fun invoke(params: BrightnessEffectParams): Result<String> {
        return repository.applyBrightnessEffect(params.file, params.brightness)
    }
}
