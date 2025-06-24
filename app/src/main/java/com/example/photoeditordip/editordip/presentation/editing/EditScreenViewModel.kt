@file:OptIn(ExperimentalEncodingApi::class)

package com.example.photoeditordip.presentation.editing
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyBlurUseCase
import com.example.photoeditordip.editordip.domain.models.EffectData
import com.example.photoeditordip.editordip.domain.models.Preset
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyBlurBackgroundUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyBrightnessEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyBWNegativeEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyCannyEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyCartoonifyUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyClaheEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyContoursEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyContrastEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyEnhanceDetailsEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyEqualizeExposureEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyFilmGrainEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyGrayScaleUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyHDRUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyLaplacianEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyNegativeEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyOilPaintingUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyPencilColorUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyPencilEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyPixelateEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyReduceNoiseEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyRemoveBackgroundUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplySharpenEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplySobelEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplySolarizeEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyStyleTransferUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyTintEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyVignetteEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyWarmColdUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyWaterColorUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ApplyInvertColorsUseCase
import com.example.photoeditordip.editordip.domain.usecases.ai_effects.ContrastEffectParams
import com.example.photoeditordip.editordip.domain.usecases.presets.SavePresetUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.File
import javax.inject.Inject
import kotlin.io.encoding.ExperimentalEncodingApi
import org.opencv.core.Size

@HiltViewModel
class EditViewModel @Inject constructor(
    private val applyBlurUseCase: ApplyBlurUseCase,
    private val applyBlurBackgroundUseCase: ApplyBlurBackgroundUseCase,
    private val applyBrightnessEffectUseCase: ApplyBrightnessEffectUseCase,
    private val applyBWNegativeEffectUseCase: ApplyBWNegativeEffectUseCase,
    private val applyCannyEffectUseCase: ApplyCannyEffectUseCase,
    private val applyCartoonifyUseCase: ApplyCartoonifyUseCase,
    private val applyClaheEffectUseCase: ApplyClaheEffectUseCase,
    private val applyContoursEffectUseCase: ApplyContoursEffectUseCase,
    private val applyContrastEffectUseCase: ApplyContrastEffectUseCase,
    private val applyEnhanceDetailsEffectUseCase: ApplyEnhanceDetailsEffectUseCase,
    private val applyEqualizeExposureEffectUseCase: ApplyEqualizeExposureEffectUseCase,
    private val applyFilmGrainEffectUseCase: ApplyFilmGrainEffectUseCase,
    private val applyGrayScaleUseCase: ApplyGrayScaleUseCase,
    private val applyHDRUseCase: ApplyHDRUseCase,
    private val applyLaplacianEffectUseCase: ApplyLaplacianEffectUseCase,
    private val applyNegativeEffectUseCase: ApplyNegativeEffectUseCase,
    private val applyOilPaintingUseCase: ApplyOilPaintingUseCase,
    private val applyPencilColorUseCase: ApplyPencilColorUseCase,
    private val applyPencilEffectUseCase: ApplyPencilEffectUseCase,
    private val applyPixelateEffectUseCase: ApplyPixelateEffectUseCase,
    private val applyReduceNoiseEffectUseCase: ApplyReduceNoiseEffectUseCase,
    private val applyRemoveBackgroundUseCase: ApplyRemoveBackgroundUseCase,
    private val applySharpenEffectUseCase: ApplySharpenEffectUseCase,
    private val applySobelEffectUseCase: ApplySobelEffectUseCase,
    private val applySolarizeEffectUseCase: ApplySolarizeEffectUseCase,
    private val applyStyleTransferUseCase: ApplyStyleTransferUseCase,
    private val applyTintEffectUseCase: ApplyTintEffectUseCase,
    private val applyVignetteEffectUseCase: ApplyVignetteEffectUseCase,
    private val applyWarmColdUseCase: ApplyWarmColdUseCase,
    private val applyWaterColorUseCase: ApplyWaterColorUseCase,
    private val invertColorsUseCase: ApplyInvertColorsUseCase,
    //Preset methods
    private val savePresetUseCase: SavePresetUseCase,
    ) : ViewModel() {

    private val _uiState = MutableStateFlow<EditScreenState>(EditScreenState.Idle)
    val uiState = _uiState.asStateFlow()
    // История редактирования
    private val history = mutableListOf<Bitmap>()
    private val effectHistory = mutableListOf<EffectData?>()
    private var currentIndex = -1


    var currentEditingPicture : Bitmap? = null
        private set

    fun updateExternalBitmap(newBitmap: Bitmap) {
        currentEditingPicture = newBitmap
    }

    fun canUndo(): Boolean = currentIndex > 0
    fun canRedo(): Boolean = currentIndex < history.lastIndex
    fun addToHistory(bitmap: Bitmap, effect: EffectData?) {
        Log.d("EditViewModel", "addToHistory called. Effect: $effect, currentIndex: $currentIndex")

        if (currentIndex < history.lastIndex) {
            Log.d("EditViewModel", "Trimming history from index ${currentIndex + 1} to ${history.size}")
            history.subList(currentIndex + 1, history.size).clear()
            effectHistory.subList(currentIndex + 1, effectHistory.size).clear()
        }

        history.add(bitmap)
        effectHistory.add(effect)
        currentIndex++

        Log.d("EditViewModel", "EffectHistory after add: $effectHistory")
    }

    fun addToHistory(bitmap: Bitmap) {
        if (currentIndex < history.lastIndex) {
            history.subList(currentIndex + 1, history.size).clear()
            }

        history.add(bitmap)
        currentIndex++
    }

    fun undo() {
        if (canUndo()) {
            currentIndex--
            _uiState.value = EditScreenState.Result(history[currentIndex])
        }
    }

    fun redo() {
        if (canRedo()) {
            currentIndex++
            _uiState.value = EditScreenState.Result(history[currentIndex])
        }
    }

    fun clearFullHistory() {
        history.clear()
        effectHistory.clear()
        currentIndex = -1
    }
    fun savePreset(name: String, imageUri: Uri) {
        viewModelScope.launch {
            Log.d("EditViewModel", "Saving preset with name: $name")
            Log.d("EditViewModel", "Full effectHistory: $effectHistory")
            Log.d("EditViewModel", "Filtered effectHistory: ${effectHistory.filterNotNull()}")

            val preset = Preset(
                name = name,
                effects = effectHistory.filterNotNull(),
                imageUri = imageUri.toString()
            )

            savePresetUseCase.invoke(preset)
        }
    }

    fun applyPreset(preset: Preset) {
        Log.d("EditViewModel", "Applying preset: ${preset.effects.forEach{e -> e.type+" "+ e.params}}")
        viewModelScope.launch {
            preset.effects.forEach { effect ->
                when (effect.type) {
                    "Sepia" -> {
                        val intensity = effect.params["intensity"] ?: 50f
                        val editedBitmap = sepiaFilter(bitmap = currentEditingPicture!!, intensity = intensity)
                        updateExternalBitmap(editedBitmap)

                    }
                    "LocalBlur" -> {
                        val blur = effect.params["blurStrength"] ?: 1f
                        val editedBitmap = localBlurFilter(blurStrength = blur, bitmap = currentEditingPicture!!)
                        updateExternalBitmap(editedBitmap)

                    }
                    "Brightness" -> {
                        val value = effect.params["brightness"] ?: 50f
                        val editedBitmap = brightnessFilter(brightness = value, bitmap = currentEditingPicture!!)
                        updateExternalBitmap(editedBitmap)
                    }
                    "Contrast" -> {
                        val value = effect.params["contrast"] ?: 50f
                        val editedBitmap = contrastFilter(contrast = value, bitmap = currentEditingPicture!!)
                        updateExternalBitmap(editedBitmap)
                    }
                    "Saturation" -> {
                        val value = effect.params["saturation"] ?: 1f
                        val editedBitmap = saturationFilter(saturation = value, bitmap = currentEditingPicture!!)
                        updateExternalBitmap(editedBitmap)
                    }
                    "Rotation" -> {
                        val angle = effect.params["angle"] ?: 0f
                        val editedBitmap = rotationFilter(angle = angle, bitmap = currentEditingPicture!!)
                        updateExternalBitmap(editedBitmap)
                    }
                    "Crop" -> { // если решишь так назвать
                        val x = effect.params["x"]?.toInt() ?: 0
                        val y = effect.params["y"]?.toInt() ?: 0
                        val w = effect.params["width"]?.toInt() ?: 100
                        val h = effect.params["height"]?.toInt() ?: 100
                        val editedBitmap = bitmapCropping(x = x, y = y, width = w, height = h, source = currentEditingPicture!!)
                        updateExternalBitmap(editedBitmap)
                    }
                    else -> {
                        Log.w("applyPreset", "Неизвестный эффект: ${effect.type}")
                    }
                }
            }
            _uiState.value = EditScreenState.Result(currentEditingPicture!!)

        }
    }


    fun cropBitmap(x: Int, y: Int, width: Int, height: Int) {
        Log.d("cropBitmap", "x: $x, y: $y, width: $width, height: $height")
        viewModelScope.launch {
            try {
                val source = currentEditingPicture!!
                val result = bitmapCropping(source, x, y, width, height)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(
                    "x" to x.toFloat(),
                    "y" to y.toFloat(),
                    "width" to width.toFloat(),
                    "height" to height.toFloat()
                )
                addToHistory(result, EffectData("Crop", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply crop: ${e.message}")
            }
        }
    }

    private fun bitmapCropping(source: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        val safeX = x.coerceAtLeast(0)
        val safeY = y.coerceAtLeast(0)
        val safeWidth = (safeX + width).coerceAtMost(source.width) - safeX
        val safeHeight = (safeY + height).coerceAtMost(source.height) - safeY
        return Bitmap.createBitmap(source, safeX, safeY, safeWidth, safeHeight)
    }




    fun applySepia(intensity: Float) {
        viewModelScope.launch {
            try {
                val current = currentEditingPicture!!
                val result = sepiaFilter(current, intensity)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf("intensity" to intensity)
                addToHistory(result, EffectData("Sepia", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply sepia: ${e.message}")
            }
        }
    }

    private fun sepiaFilter(bitmap: Bitmap, intensity: Float): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = bitmap.copy(config, true)
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val normalizedIntensity = intensity / 100f
        val sepiaKernel = Mat(4, 4, CvType.CV_32F)
        sepiaKernel.put(0, 0,
            0.272 + (1 - normalizedIntensity), 0.534 - (1 - normalizedIntensity), 0.131 - (1 - normalizedIntensity), 0.0,
            0.349 - (1 - normalizedIntensity), 0.686 + (1 - normalizedIntensity), 0.168 - (1 - normalizedIntensity), 0.0,
            0.393 - (1 - normalizedIntensity), 0.769 - (1 - normalizedIntensity), 0.189 + (1 - normalizedIntensity), 0.0,
            0.0, 0.0, 0.0, 1.0
        )

        Core.transform(mat, mat, sepiaKernel)
        Utils.matToBitmap(mat, result)
        return result
    }



    fun applyLocalBlur(blurStrength: Float = 0.5f) {
        viewModelScope.launch {
            try {
                val current = currentEditingPicture!!
                val result = localBlurFilter(current, blurStrength)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf("blurStrength" to blurStrength)
                addToHistory(result, EffectData("LocalBlur", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply blur: ${e.message}")
            }
        }
    }

    private fun localBlurFilter(bitmap: Bitmap, blurStrength: Float): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = bitmap.copy(config, true)
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val kernelSize = ((blurStrength / 2).toInt().coerceAtLeast(1)).let { if (it % 2 == 0) it + 1 else it }
        Imgproc.GaussianBlur(mat, mat, Size(kernelSize.toDouble(), kernelSize.toDouble()), 0.0)
        Utils.matToBitmap(mat, result)
        return result
    }



    fun applyBrightness(brightness: Float) {
        viewModelScope.launch {
            try {
                val current = currentEditingPicture!!
                val result = brightnessFilter(current, brightness)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf("brightness" to brightness)
                addToHistory(result, EffectData("Brightness", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply brightness: ${e.message}")
            }
        }
    }

    private fun brightnessFilter(bitmap: Bitmap, brightness: Float): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = bitmap.copy(config, true)
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val brightnessOffset = brightness - 50f // чтобы 50 = 0
        mat.convertTo(mat, -1, 1.0, brightnessOffset.toDouble())

        Utils.matToBitmap(mat, result)
        return result
    }


    fun applyContrast(contrast: Float) {
        viewModelScope.launch {
            try {
                val current = currentEditingPicture!!
                val result = contrastFilter(current, contrast)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf("contrast" to contrast)
                addToHistory(result, EffectData("Contrast", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply contrast: ${e.message}")
            }
        }
    }

    private fun contrastFilter(bitmap: Bitmap, contrast: Float): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = bitmap.copy(config, true)
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val contrastFactor = contrast / 50f // 50 = 1.0
        mat.convertTo(mat, -1, contrastFactor.toDouble(), 0.0)

        Utils.matToBitmap(mat, result)
        return result
    }




    fun applySaturation(saturation: Float) {
        viewModelScope.launch {
            try {
                val current = currentEditingPicture!!
                val result = saturationFilter(current, saturation)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf("saturation" to saturation)
                addToHistory(result, EffectData("Saturation", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply saturation: ${e.message}")
            }
        }
    }

    private fun saturationFilter(bitmap: Bitmap, saturation: Float): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = bitmap.copy(config, true)
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val hsvMat = Mat()
        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_BGR2HSV)
        val channels = ArrayList<Mat>()
        Core.split(hsvMat, channels)

        Core.multiply(channels[1], Scalar(saturation.toDouble()), channels[1])
        Core.merge(channels, hsvMat)
        Imgproc.cvtColor(hsvMat, mat, Imgproc.COLOR_HSV2BGR)
        Utils.matToBitmap(mat, result)
        return result
    }


    fun applySharpen(strength: Float = 1.0f) {
        viewModelScope.launch {
            try {
                val current = currentEditingPicture!!
                val result = sharpenFilter(current, strength)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf("strength" to strength)
                addToHistory(result, EffectData("Sharpen", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply sharpen: ${e.message}")
            }
        }
    }

    private fun sharpenFilter(bitmap: Bitmap, strength: Float): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = bitmap.copy(config, true)
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        val sharpenValue = 5.0 + (strength / 100f) * 5.0
        val kernel = Mat(3, 3, CvType.CV_32F)
        val center = 5.0 + sharpenValue * 2.0
        kernel.put(0, 0,
            0.0, -1.0, 0.0,
            -1.0, center, -1.0,
            0.0, -1.0, 0.0
        )

        Imgproc.filter2D(mat, mat, -1, kernel)
        Utils.matToBitmap(mat, result)
        return result
    }


    fun applyRotation(angle: Float) {
        viewModelScope.launch {
            try {
                val current = currentEditingPicture!!
                val result = rotationFilter(current, angle)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf("angle" to angle)
                addToHistory(result, EffectData("Rotation", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to rotate image: ${e.message}")
            }
        }
    }

    private fun rotationFilter(bitmap: Bitmap, angle: Float): Bitmap {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        val center = Point(mat.cols() / 2.0, mat.rows() / 2.0)
        val rotationMatrix = Imgproc.getRotationMatrix2D(center, angle.toDouble(), 1.0)
        val rotated = Mat()

        Imgproc.warpAffine(mat, rotated, rotationMatrix, mat.size())
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = Bitmap.createBitmap(rotated.cols(), rotated.rows(), config)
        Utils.matToBitmap(rotated, result)
        return result
    }


    fun applyBlur(file: File) {
        Log.d("EditViewModel", "applyBlur called with file: ${file.name}, size: ${file.length()} bytes")
        Log.d("EditViewModel", "File exists: ${file.exists()}, readable: ${file.canRead()}")

        viewModelScope.launch {
            Log.d("EditViewModel", "Setting state to Loading")
            _uiState.value = EditScreenState.Loading

            try {
                Log.d("EditViewModel", "Calling applyBlurUseCase...")
                val result = applyBlurUseCase(file)
                Log.d("EditViewModel", "UseCase completed, processing result...")

                result.fold(
                    onSuccess = { base64String ->
                        Log.d("EditViewModel", "Success! Base64 length: ${base64String.length}")
                        Log.d("EditViewModel", "Base64 preview: ${base64String.take(100)}...")

                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                            Log.d("EditViewModel", "State set to Result with bitmap: ${bitmap.width}x${bitmap.height}")
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                            Log.e("EditViewModel", "Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        Log.e("EditViewModel", "Error occurred: ${error.message}", error)
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                        Log.d("EditViewModel", "State set to Error")
                    }
                )
            } catch (e: Exception) {
                Log.e("EditViewModel", "Exception in applyBlur: ${e.message}", e)
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyBackgroundBlur(file: File, blurIntensity: Int) {
        Log.d("EditViewModel", "applyBackgroundBlur called with file: ${file.name}, size: ${file.length()} bytes")
        Log.d("EditViewModel", "File exists: ${file.exists()}, readable: ${file.canRead()}")

        viewModelScope.launch {
            Log.d("EditViewModel", "Setting state to Loading")
            _uiState.value = EditScreenState.Loading

            try {
                Log.d("EditViewModel", "Calling applyBackgroundBlur...")
                val result = applyBlurBackgroundUseCase(file, blurIntensity)
                Log.d("EditViewModel", "UseCase completed, processing result...")

                result.fold(
                    onSuccess = { base64String ->
                        Log.d("EditViewModel", "Success! Base64 length: ${base64String.length}")
                        Log.d("EditViewModel", "Base64 preview: ${base64String.take(100)}...")

                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                            Log.d("EditViewModel", "State set to Result with bitmap: ${bitmap.width}x${bitmap.height}")
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                            Log.e("EditViewModel", "Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        Log.e("EditViewModel", "Error occurred: ${error.message}", error)
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                        Log.d("EditViewModel", "State set to Error")
                    }
                )
            } catch (e: Exception) {
                Log.e("EditViewModel", "Exception in applyBackgroundBlur: ${e.message}", e)
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyWarmCold(file: File, warmth: Double, coldness: Double) {
        Log.d("EditViewModel", "applyWarmCold called with file: ${file.name}, warmth=$warmth, coldness=$coldness")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyWarmColdUseCase(file, warmth, coldness)

                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyCartoonify(file: File) {
        Log.d("EditViewModel", "applyCartoonify called with file: ${file.name}, size: ${file.length()} bytes")
        Log.d("EditViewModel", "File exists: ${file.exists()}, readable: ${file.canRead()}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyCartoonifyUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyPencilColor(file: File) {
        Log.d("EditViewModel", "applyPencilColor called with file: ${file.name}, size: ${file.length()} bytes")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyPencilColorUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyOilPainting(file: File) {
        Log.d("EditViewModel", "applyOilPainting called with file: ${file.name}, size: ${file.length()} bytes")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyOilPaintingUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyWaterColor(file: File) {
        Log.d("EditViewModel", "applyWaterColor called with file: ${file.name}, size: ${file.length()} bytes")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyWaterColorUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyPencil(file: File) {
        Log.d("EditViewModel", "applyPencil called with file: ${file.name}, size: ${file.length()} bytes")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyPencilEffectUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyBrightness(file: File, brightness: Int) {
        Log.d("EditViewModel", "applyBrightness called with file: ${file.name}, brightness: $brightness")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyBrightnessEffectUseCase(file, brightness)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyContrast(file: File, contrast: Int) {
        Log.d("EditViewModel", "applyContrast called with file: ${file.name}, contrast: $contrast")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyContrastEffectUseCase(param = ContrastEffectParams(file, contrast))
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyInvertColors(file: File) {
        Log.d("EditViewModel", "invertColors called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = invertColorsUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyGrayScale(file: File) {
        Log.d("EditViewModel", "grayScale called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyGrayScaleUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyNegativeEffect(file: File) {
        Log.d("EditViewModel", "applyNegativeEffect called with file: ${file.name}, size: ${file.length()} bytes")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyNegativeEffectUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyTintEffect(file: File, r: Int, g: Int, b: Int) {
        Log.d("EditViewModel", "applyTintEffect called with file: ${file.name}, r=$r, g=$g, b=$b")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyTintEffectUseCase(file, r, g, b)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyBWNegativeEffect(file: File) {
        Log.d("EditViewModel", "applyBWNegativeEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyBWNegativeEffectUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyRemoveBackground(file: File) {
        Log.d("EditViewModel", "applyRemoveBackgroundUseCase called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyRemoveBackgroundUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyCannyEffect(file: File, threshold1: Int, threshold2: Int) {
        Log.d("EditViewModel", "applyCannyEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyCannyEffectUseCase(file, threshold1, threshold2)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyClaheEffect(file: File, clipLimit: Int) {
        Log.d("EditViewModel", "applyClaheEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyClaheEffectUseCase(file, clipLimit)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyContoursEffect(file: File) {
        Log.d("EditViewModel", "applyContoursEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyContoursEffectUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyEnhanceDetailsEffect(file: File) {
        Log.d("EditViewModel", "applyEnhanceDetailsEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyEnhanceDetailsEffectUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyEqualizeExposureEffect(file: File) {
        Log.d("EditViewModel", "applyEqualizeExposureEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyEqualizeExposureEffectUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyFilmGrainEffect(file: File, intensity: Double) {
        Log.d("EditViewModel", "applyFilmGrainEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyFilmGrainEffectUseCase(file, intensity)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyHDR(file: File) {
        Log.d("EditViewModel", "applyHDR called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyHDRUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyLaplacianEffect(file: File) {
        Log.d("EditViewModel", "applyLaplacianEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyLaplacianEffectUseCase(file)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }


    fun applyPixelateEffect(file: File, blockSize: Int) {
        Log.d("EditViewModel", "applyPixelateEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyPixelateEffectUseCase(file, blockSize)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyReduceNoiseEffect(file: File, noiseStrength: Int) {
        Log.d("EditViewModel", "applyReduceNoiseEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyReduceNoiseEffectUseCase(file, noiseStrength)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applySharpenEffect(file: File, strength: Double) {
        Log.d("EditViewModel", "applySharpenEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applySharpenEffectUseCase(file, strength)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applySobelEffect(file: File, kernelSize: Int) {
        Log.d("EditViewModel", "applySobelEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applySobelEffectUseCase(file, kernelSize)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applySolarizeEffect(file: File, threshold: Int) {
        Log.d("EditViewModel", "applySolarizeEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applySolarizeEffectUseCase(file, threshold)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyStyleTransfer(file: File, styleFile: File) {
        Log.d("EditViewModel", "applyStyleTransfer called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyStyleTransferUseCase(file, styleFile)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun applyVignetteEffect(file: File, strength: Int) {
        Log.d("EditViewModel", "applyVignetteEffect called with file: ${file.name}")

        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading

            try {
                val result = applyVignetteEffectUseCase(file, strength)
                result.fold(
                    onSuccess = { base64String ->
                        val bitmap = decodeBase64ToBitmap(base64String)
                        if (bitmap != null) {
                             _uiState.value = EditScreenState.Result(bitmap)
                            addToHistory(bitmap)
                        } else {
                            _uiState.value = EditScreenState.Error("Failed to decode base64 to bitmap")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = EditScreenState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

}

fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
    return try {
        // Убираем префикс data URL если он есть (например: "data:image/jpeg;base64,")
        val pureBase64 = if (base64Str.contains(",")) {
            base64Str.substringAfter(",")
        } else {
            base64Str
        }

        // ИСПРАВЛЕНО: используем Base64.DEFAULT вместо CoroutineStart.DEFAULT
        val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)

        // Создаем Bitmap из декодированных байтов
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        Log.d("decodeBase64ToBitmap", "Successfully decoded bitmap: ${bitmap?.width}x${bitmap?.height}")
        bitmap

    } catch (e: Exception) {
        Log.e("decodeBase64ToBitmap", "Error decoding base64 to bitmap: ${e.message}", e)
        null
    }
}

sealed class EditScreenState {
    object Idle : EditScreenState()
    object Loading : EditScreenState()
    data class Result(val editedImage: Bitmap) : EditScreenState()
    data class Error(val message: String) : EditScreenState()
}