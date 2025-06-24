package com.example.photoeditordip.editordip.domain.repository

import com.example.photoeditordip.editordip.domain.models.Preset

interface PresetRepository {
    suspend fun savePreset(preset: Preset, ): Result<Unit>
    suspend fun getAllPresets(): Result<List<Preset>>
    suspend fun getPresetByName(name: String): Result<Preset?>
    suspend fun deletePreset(name: String): Result<Unit>
}
