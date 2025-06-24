package com.example.photoeditordip.editordip.presentation.preview

import androidx.lifecycle.ViewModel
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyBlurUseCase
import com.example.photoeditordip.editordip.domain.usecases.presets.SavePresetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewScreenViewModel @Inject constructor(
    private val savePresetUseCase: SavePresetUseCase,
): ViewModel() {

}