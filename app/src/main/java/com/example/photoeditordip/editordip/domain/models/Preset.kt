package com.example.photoeditordip.editordip.domain.models

import android.net.Uri

data class Preset(
    val name: String,
    val effects: List<EffectData>,
    val imageUri: String
)
