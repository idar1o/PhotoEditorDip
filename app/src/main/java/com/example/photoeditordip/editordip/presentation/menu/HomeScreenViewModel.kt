package com.example.photoeditordip.editordip.presentation.menu

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditordip.presentation.editing.EditScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadUserImages() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = HomeScreenState.Loading
            try {
                val images = getImagesFromEditorDipFolder(context)
                _uiState.value = HomeScreenState.Result(images)
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Error("Не удалось загрузить изображения: ${e.message}")
            }
        }
    }

    private fun getImagesFromEditorDipFolder(context: Context): List<Bitmap> {
        val imageBitmaps = mutableListOf<Bitmap>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.RELATIVE_PATH
        )

        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Pictures/EditorDip%")

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
                    imageBitmaps.add(bitmap)
                } catch (e: Exception) {
                    // Пропускаем битые или слишком тяжёлые файлы
                    e.printStackTrace()
                }
            }
        }

        return imageBitmaps
    }

}
sealed class HomeScreenState {
    object Idle : HomeScreenState()
    object Loading : HomeScreenState()
    data class Result(val userImages: List<Bitmap>) : HomeScreenState()
    data class Error(val message: String) : HomeScreenState()
}