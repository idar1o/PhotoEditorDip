package com.example.photoeditordip.editordip.domain.usecases.presets

import com.example.photoeditordip.editordip.domain.models.Preset
import com.example.photoeditordip.editordip.domain.repository.PresetRepository

class GetAllPresetsUseCase(private val repository: PresetRepository) {
    suspend operator fun invoke(): Result<List<Preset>> {
        return repository.getAllPresets()
    }
}
