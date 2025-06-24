package com.example.photoeditordip.editordip.domain.usecases.presets

import com.example.photoeditordip.editordip.domain.models.Preset
import com.example.photoeditordip.editordip.domain.repository.PresetRepository
import javax.inject.Inject

class GetAllPresetsUseCase@Inject constructor(private val repository: PresetRepository) {
    suspend operator fun invoke(): Result<List<Preset>> {
        return repository.getAllPresets()
    }
}
