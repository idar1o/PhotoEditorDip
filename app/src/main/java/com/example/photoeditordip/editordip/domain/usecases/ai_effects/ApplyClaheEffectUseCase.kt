package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.ClaheEffectParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject


class ApplyClaheEffectUseCase @Inject constructor(
    private val repository: ImageRepository
) : UseCase<ClaheEffectParams, String> {
    override suspend operator fun invoke(param: ClaheEffectParams): Result<String> = repository.applyClaheEffect(param.file, param.clipLimit)
}