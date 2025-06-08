@file:OptIn(ExperimentalEncodingApi::class)

package com.example.photoeditordip.presentation.editing
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditordip.domain.usecase.ApplyBlurUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.io.encoding.ExperimentalEncodingApi

@HiltViewModel
class EditViewModel @Inject constructor(
    private val applyBlurUseCase: ApplyBlurUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditScreenState>(EditScreenState.Idle)
    val uiState = _uiState.asStateFlow()

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