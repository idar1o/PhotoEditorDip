package com.example.photoeditordip.editordip.domain.usecases.presets

import com.example.photoeditordip.editordip.domain.models.Preset
import com.example.photoeditordip.editordip.domain.repository.PresetRepository
import javax.inject.Inject

class SavePresetUseCase@Inject constructor(
    private val repository: PresetRepository) {
    suspend operator fun invoke(preset: Preset): Result<Unit> {
        return repository.savePreset(preset)
    }
}
