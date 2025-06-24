package com.example.photoeditordip.editordip.domain.models.effects

import java.io.File


data class ContrastEffectParams(val file: File, val contrast: Int)
data class BlurEffectParams(val file: File, val blurIntensity: Int)
data class BrightnessEffectParams(val file: File, val brightness: Int)
data class BWNegativeEffectParams(val file: File)
data class CannyEffectParams(val file: File,val threshold1: Int,val threshold2: Int)
data class CartoonifyParams(val file: File)
data class ClaheEffectParams(val file: File, val clipLimit: Int)
data class ContoursParams(val file: File)
data class EnhanceDetailsParams(val file: File)
data class EqualizeExposureParams(val file: File)
