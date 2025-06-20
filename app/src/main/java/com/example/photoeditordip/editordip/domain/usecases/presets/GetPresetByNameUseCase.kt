package com.example.photoeditordip.editordip.domain.usecases.presets

import com.example.photoeditordip.editordip.domain.models.Preset
import com.example.photoeditordip.editordip.domain.repository.PresetRepository

class GetPresetByNameUseCase(private val repository: PresetRepository) {
    suspend operator fun invoke(name: String): Result<Preset?> {
        return repository.getPresetByName(name)
    }
}
