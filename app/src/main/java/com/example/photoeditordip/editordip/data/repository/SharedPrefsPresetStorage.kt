package com.example.photoeditordip.editordip.data.repository

import android.content.SharedPreferences
import com.example.photoeditordip.editordip.domain.repository.PresetStorage
import javax.inject.Inject

class SharedPrefsPresetStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PresetStorage {

    private val key = "presets_json"

    override fun savePresets(json: String) {
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun loadPresets(): String? {
        return sharedPreferences.getString(key, null)
    }
}
