package com.example.photoeditordip.editordip.data.repository

import com.example.photoeditordip.editordip.domain.models.Preset
import com.example.photoeditordip.editordip.domain.repository.PresetRepository
import com.example.photoeditordip.editordip.domain.repository.PresetStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class PresetRepositoryImpl @Inject constructor(
    private val storage: PresetStorage,
    private val gson: Gson = Gson()
) : PresetRepository {

    override suspend fun savePreset(preset: Preset): Result<Unit> = runCatching {
        val presets = getAllPresets().getOrNull()?.toMutableList() ?: mutableListOf()
        presets.removeAll { it.name == preset.name }
        presets.add(preset)
        storage.savePresets(gson.toJson(presets))
    }

    override suspend fun getAllPresets(): Result<List<Preset>> = runCatching {
        val json = storage.loadPresets() ?: return@runCatching emptyList()
        val type = object : TypeToken<List<Preset>>() {}.type
        gson.fromJson(json, type)
    }

    override suspend fun getPresetByName(name: String): Result<Preset?> = runCatching {
        getAllPresets().getOrNull()?.find { it.name == name }
    }

    override suspend fun deletePreset(name: String): Result<Unit> = runCatching {
        val presets = getAllPresets().getOrNull()?.filterNot { it.name == name } ?: return@runCatching
        storage.savePresets(gson.toJson(presets))
    }
}
