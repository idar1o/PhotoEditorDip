package com.example.photoeditordip.editordip.domain.usecases.ai_effects

import com.example.photoeditordip.editordip.domain.models.UseCase
import com.example.photoeditordip.editordip.domain.models.effects.CartoonifyParams
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class ApplyCartoonifyUseCase @Inject constructor(
    private val repository: ImageRepository
) : UseCase<CartoonifyParams, String> {
    override suspend operator fun invoke(param: CartoonifyParams): Result<String> {
        return repository.applyCartoonifyEffect(param.file)
    }
}
