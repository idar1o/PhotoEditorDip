package com.example.photoeditordip.editordip.domain.usecases.presets

import com.example.photoeditordip.editordip.domain.repository.PresetRepository
import javax.inject.Inject

class DeletePresetUseCase@Inject constructor(private val repository: PresetRepository) {
    suspend operator fun invoke(name: String): Result<Unit> {
        return repository.deletePreset(name)
    }
}
