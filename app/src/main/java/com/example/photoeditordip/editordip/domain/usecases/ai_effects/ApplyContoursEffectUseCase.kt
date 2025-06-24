package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.ContoursParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyContoursEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) : UseCase<ContoursParams, String> {
    override suspend operator fun invoke(param: ContoursParams): Result<String> = repository.applyContoursEffect(param.file)
}