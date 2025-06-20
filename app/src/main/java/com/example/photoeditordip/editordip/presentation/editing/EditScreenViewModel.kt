@file:OptIn(ExperimentalEncodingApi::class)

package com.example.photoeditordip.presentation.editing
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditordip.domain.usecase.ApplyBlurUseCase
import com.example.photoeditordip.editordip.domain.models.EffectData
import com.example.photoeditordip.editordip.domain.usecases.ApplyBlurBackgroundUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyBrightnessEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyBWNegativeEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyCannyEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyCartoonifyUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyClaheEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyContoursEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyContrastEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyEnhanceDetailsEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyEqualizeExposureEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyFilmGrainEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyGrayScaleUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyHDRUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyLaplacianEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyNegativeEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyOilPaintingUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyPencilColorUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyPencilEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyPixelateEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyReduceNoiseEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyRemoveBackgroundUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplySharpenEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplySobelEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplySolarizeEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyStyleTransferUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyTintEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyVignetteEffectUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyWarmColdUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyWaterColorUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyInvertColorsUseCase

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
    private val invertColorsUseCase: ApplyInvertColorsUseCase
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
        if (currentIndex < history.lastIndex) {
            history.subList(currentIndex + 1, history.size).clear()
            effectHistory.subList(currentIndex + 1, effectHistory.size).clear()
        }

        history.add(bitmap)
        effectHistory.add(effect)
        currentIndex++
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

    fun cropBitmap( x: Int, y: Int, width: Int, height: Int){
        Log.d("cropBitmap", "x: $x, y: $y, width: $width, height: $height")
        val source = currentEditingPicture!!
        // Убедимся, что область не выходит за пределы изображения
        val safeX = x.coerceAtLeast(0)
        val safeY = y.coerceAtLeast(0)
        val safeWidth = (safeX + width).coerceAtMost(source.width) - safeX
        val safeHeight = (safeY + height).coerceAtMost(source.height) - safeY

        val result = Bitmap.createBitmap(source, safeX, safeY, safeWidth, safeHeight)


        _uiState.value = EditScreenState.Result(result)
        val params = mapOf(
            Pair("x", x.toFloat()),
            Pair("y", y.toFloat()),
            Pair("width", width.toFloat()),
            Pair("height", height.toFloat())
        )
        addToHistory(result, EffectData("Sepia", params))
    }



    fun applySepia(intensity: Float) {
        viewModelScope.launch {
//            _uiState.value = EditScreenState.Loading
            try {
                val current: Bitmap = currentEditingPicture!!

                val config = current.config ?: Bitmap.Config.ARGB_8888
                val result = current.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(current, mat)
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

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(Pair("intesity", intensity))
                addToHistory(result, EffectData("Sepia", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply sepia: ${e.message}")
            }
        }
    }


    fun applyLocalBlur(blurStrength: Float = 0.5f) {
        viewModelScope.launch {
//            _uiState.value = EditScreenState.Loading
            try {
                val current = currentEditingPicture!!

                val config = current.config ?: Bitmap.Config.ARGB_8888
                val result = current.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(current, mat)
                val kernelSize = ((blurStrength / 2).toInt().coerceAtLeast(1)).let { if (it % 2 == 0) it + 1 else it }

                Imgproc.GaussianBlur(mat, mat, Size(kernelSize.toDouble(), kernelSize.toDouble()), 0.0)
                Utils.matToBitmap(mat, result)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(Pair("blurStrength", blurStrength))
                addToHistory(result, EffectData("LocalBlur", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply blur: ${e.message}")
            }
        }
    }


    fun applyBrightness(brightness: Float) {
        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading
            try {
                val current = currentEditingPicture!!

                val config = current.config ?: Bitmap.Config.ARGB_8888
                val result = current.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(current, mat)
                val brightnessOffset = brightness - 50f // чтобы 50 стало 0
                mat.convertTo(mat, -1, 1.0, brightnessOffset.toDouble())
                Utils.matToBitmap(mat, result)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(Pair("brightness", brightness))
                addToHistory(result, EffectData("Brightness", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply brightness: ${e.message}")
            }
        }
    }

    fun applyContrast(contrast: Float) {
        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading
            try {
                val current = currentEditingPicture!!

                val config = current.config ?: Bitmap.Config.ARGB_8888
                val result = current.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(current, mat)
                val contrastFactor = contrast / 50f // чтобы 50 стало 1.0

                mat.convertTo(mat, -1, contrastFactor.toDouble(), 0.0)
                Utils.matToBitmap(mat, result)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(Pair("contrast", contrast))
                addToHistory(result, EffectData("Contrast", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply contrast: ${e.message}")
            }
        }
    }

    fun applyGammaCorrection(bitmap: Bitmap, gamma: Double): Bitmap {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat) // Нормализуем в диапазон [0.0, 1.0]
        mat.convertTo(mat, CvType.CV_32F)
        Core.divide(mat, Scalar(255.0, 255.0, 255.0), mat) // Применяем gamma-коррекцию
        Core.pow(mat, gamma, mat)// Обратно в диапазон [0, 255]
        Core.multiply(mat, Scalar(255.0, 255.0, 255.0), mat)
        mat.convertTo(mat, CvType.CV_8UC3)
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, result)
        return result
    }
    fun applyUnsharpMasking(bitmap: Bitmap, kernelSize: Int = 5, sigma: Double = 1.0, alpha: Double = 1.5): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val blurred = Mat()
        Imgproc.GaussianBlur(src, blurred, Size(kernelSize.toDouble(), kernelSize.toDouble()), sigma)
        // Разность (детали)
        val details = Mat()
        Core.subtract(src, blurred, details) // Добавляем обратно усиленные детали
        val sharpened = Mat()
        Core.addWeighted(src, 1.0, details, alpha, 0.0, sharpened)
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(sharpened, result)
        return result
    }

    fun cropAndResizeImage(
        originalBitmap: Bitmap,
        cropX: Int,
        cropY: Int,
        cropWidth: Int,
        cropHeight: Int,
        newWidth: Int,
        newHeight: Int
    ): Bitmap {
        val mat = Mat()// Преобразование Bitmap -> Mat
        Utils.bitmapToMat(originalBitmap, mat) // Обрезка изображения
        val rect = Rect(cropX, cropY, cropWidth, cropHeight)
        val croppedMat = Mat(mat, rect) // Изменение размера
        val resizedMat = Mat()
        Imgproc.resize(croppedMat, resizedMat, Size(newWidth.toDouble(), newHeight.toDouble())) // Преобразование Mat -> Bitmap
        val resultBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resizedMat, resultBitmap)
        return resultBitmap
    }

    fun applySaturation(saturation: Float) {
        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading
            try {
                val current = currentEditingPicture!!

                val config = current.config ?: Bitmap.Config.ARGB_8888
                val result = current.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(current, mat)
                val hsvMat = Mat()
                Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_BGR2HSV)
                val channels = ArrayList<Mat>()
                Core.split(hsvMat, channels)
                Core.multiply(channels[1], Scalar(saturation.toDouble()), channels[1])
                Core.merge(channels, hsvMat)
                Imgproc.cvtColor(hsvMat, mat, Imgproc.COLOR_HSV2BGR)
                Utils.matToBitmap(mat, result)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(Pair("saturation", saturation))
                addToHistory(result, EffectData("Saturation", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply saturation: ${e.message}")
            }
        }
    }

    fun applySharpen(strength: Float = 1.0f) {
        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading
            try {
                val current = currentEditingPicture!!
                val sharpenValue = 5.0 + (strength / 100f) * 5.0

                val config = current.config ?: Bitmap.Config.ARGB_8888
                val result = current.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(current, mat)

                val kernel = Mat(3, 3, CvType.CV_32F)
                val center = 5.0 + sharpenValue * 2.0  // base is 5.0
                kernel.put(0, 0, 0.0, -1.0, 0.0,
                    -1.0, center, -1.0,
                    0.0, -1.0, 0.0)

                Imgproc.filter2D(mat, mat, -1, kernel)
                Utils.matToBitmap(mat, result)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(Pair("strength", strength))
                addToHistory(result, EffectData("Sharpen", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to apply sharpen: ${e.message}")
            }
        }
    }

    fun applyRotation(angle: Float) {
        viewModelScope.launch {
            _uiState.value = EditScreenState.Loading
            try {
                val current = currentEditingPicture!!

                val mat = Mat()
                Utils.bitmapToMat(current, mat)
                val center = Point(mat.cols() / 2.0, mat.rows() / 2.0)
                val rotationMatrix = Imgproc.getRotationMatrix2D(center, angle.toDouble(), 1.0)
                val rotated = Mat()
                Imgproc.warpAffine(mat, rotated, rotationMatrix, mat.size())
                val config = current.config ?: Bitmap.Config.ARGB_8888
                val result = Bitmap.createBitmap(rotated.cols(), rotated.rows(), config)
                Utils.matToBitmap(rotated, result)

                _uiState.value = EditScreenState.Result(result)
                val params = mapOf(Pair("angle", angle))
                addToHistory(result, EffectData("Rotation", params))
            } catch (e: Exception) {
                _uiState.value = EditScreenState.Error("Failed to rotate image: ${e.message}")
            }
        }
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
                val result = applyContrastEffectUseCase(file, contrast)
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