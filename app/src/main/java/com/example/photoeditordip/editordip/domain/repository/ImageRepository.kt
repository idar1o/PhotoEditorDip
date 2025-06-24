package com.example.photoeditordip.editordip.domain.repository

import com.example.photoeditordip.data.repository.ImageRepositoryImpl
import com.example.photoeditordip.data.repository.ImageRepositoryImpl_Factory
import java.io.File

//interface Effect<T>{
//    suspend fun applyEffect(imageFile: File): Result<File>
//}
//
//class ApplyBlurEffect(private val param: Double) : Effect<Double> {
//    override suspend fun applyEffect(imageFile: File): Result<File> {
//        return imageFile.applyBlur(imageFile, param);
//    }
//}
//
//class ApplyStyleTransferEffect : Effect<Pair<File, File>> {
//
//    var effects = mutableListOf<Effect<*>>()
//
//    override suspend fun applyEffect(imageFile: File, param: Pair<File, File>): Result<File> {
//          effects.add(ApplyBlurEffect(100.0))
//          effects.add(ApplyBlurEffect(720, 960))
//          effects.add(ApplyBlurEffect())
//
//        effects.forEach { it.applyEffect(imageFile) }
//
//    }
//}

interface ImageRepository {
    suspend fun applyBlurEffect(imageFile: File): Result<String>
    suspend fun applyStyleTransferEffect(contentFile: File, styleFile: File): Result<String>
    suspend fun applyBlurBackgroundEffect(imageFile: File, blurIntensity: Int): Result<String>
    suspend fun applyWarmColdEffect(imageFile: File, warmth: Double, coldness: Double): Result<String>
    suspend fun applyCartoonifyEffect(imageFile: File): Result<String>
    suspend fun applyPencilColorEffect(imageFile: File): Result<String>
    suspend fun applyOilPaintingEffect(imageFile: File): Result<String>
    suspend fun applyWaterColorEffect(imageFile: File): Result<String>
    suspend fun applyPencilEffect(imageFile: File): Result<String>
    suspend fun applyContrastEffect(imageFile: File, contrast: Int): Result<String>
    suspend fun applyBrightnessEffect(imageFile: File, brightness: Int): Result<String>
    suspend fun invertColors(imageFile: File): Result<String>
    suspend fun applyGrayScaleEffect(imageFile: File): Result<String>
    suspend fun applyNegativeEffect(imageFile: File): Result<String>
    suspend fun applyTintEffect(imageFile: File, r: Int, g: Int, b: Int): Result<String>
    suspend fun applySolarizeEffect(imageFile: File, threshold: Int): Result<String>
    suspend fun applyBWNegativeEffect(imageFile: File): Result<String>
    suspend fun applyHDREffect(imageFile: File): Result<String>
    suspend fun applyPixelateEffect(imageFile: File, blockSize: Int): Result<String>
    suspend fun applyVignetteEffect(imageFile: File, strength: Int): Result<String>
    suspend fun applyFilmGrainEffect(imageFile: File, intensity: Double): Result<String>
    suspend fun removeBackground(imageFile: File): Result<String>
    suspend fun applyCannyEffect(imageFile: File, threshold1: Int, threshold2: Int): Result<String>
    suspend fun applySobelEffect(imageFile: File, kernelSize: Int): Result<String>
    suspend fun applyLaplacianEffect(imageFile: File): Result<String>
    suspend fun applyContoursEffect(imageFile: File): Result<String>
    suspend fun applySharpenEffect(imageFile: File, strength: Double): Result<String>
    suspend fun applyReduceNoiseEffect(imageFile: File, noiseStrength: Int): Result<String>
    suspend fun applyEnhanceDetailsEffect(imageFile: File): Result<String>
    suspend fun applyEqualizeExposureEffect(imageFile: File): Result<String>
    suspend fun applyClaheEffect(imageFile: File, clipLimit: Int): Result<String>
}