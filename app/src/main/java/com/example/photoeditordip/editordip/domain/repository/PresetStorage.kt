package com.example.photoeditordip.editordip.domain.repository

interface PresetStorage {
    fun savePresets(json: String)
    fun loadPresets(): String?
}
