package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.EnhanceDetailsParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyEnhanceDetailsEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) : UseCase<EnhanceDetailsParams, String> {
    override suspend operator fun invoke(param: EnhanceDetailsParams): Result<String> = repository.applyEnhanceDetailsEffect(param.file)
}