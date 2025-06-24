package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.EqualizeExposureParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyEqualizeExposureEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) : UseCase<EqualizeExposureParams, String> {
    override suspend operator fun invoke(param: EqualizeExposureParams): Result<String> = repository.applyEqualizeExposureEffect(param.file)
}