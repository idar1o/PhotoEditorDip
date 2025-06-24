package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.CannyEffectParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject


class ApplyCannyEffectUseCase @Inject constructor(
    private val repository: ImageRepository
): UseCase<CannyEffectParams, String> {
    override suspend operator fun invoke(params: CannyEffectParams): Result<String> = repository.applyCannyEffect(params.file, params.threshold1, params.threshold2)
}