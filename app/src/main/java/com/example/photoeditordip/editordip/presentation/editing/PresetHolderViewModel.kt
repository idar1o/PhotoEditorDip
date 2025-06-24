package com.example.photoeditordip.editordip.presentation.editing

import androidx.lifecycle.ViewModel
import com.example.photoeditordip.editordip.domain.models.Preset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PresetHolderViewModel @Inject constructor(): ViewModel(){

    private val _selectedPreset = MutableStateFlow<Preset?>(null)
    val selectedPreset: StateFlow<Preset?> = _selectedPreset

    fun setSelectedPreset(preset: Preset) {
        _selectedPreset.value = preset
    }

}