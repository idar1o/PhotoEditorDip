package com.example.photoeditordip.editordip.presentation.menu

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditordip.editordip.domain.models.Preset
import com.example.photoeditordip.editordip.domain.usecases.presets.GetAllPresetsUseCase
import com.example.photoeditordip.presentation.editing.EditScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllPresetsUseCase: GetAllPresetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenState>(HomeScreenState.Idle)
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    private val _presets = MutableStateFlow<List<Preset>>(emptyList())
    val presets: StateFlow<List<Preset>> = _presets.asStateFlow()

    fun loadPresets() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllPresetsUseCase().fold(
                onSuccess = { _presets.value = it.reversed() },
                onFailure = { it.printStackTrace() }
            )
        }
    }

    fun loadUserImages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val images = getImagesFromEditorDipFolder(context)
                _uiState.value = HomeScreenState.Result(images)
            } catch (e: Exception) {
                _uiState.value = HomeScreenState.Error("Не удалось загрузить изображения: ${e.message}")
            }
        }
    }

    private fun getImagesFromEditorDipFolder(context: Context): List<Pair<Uri, Bitmap>> {
        val imageList = mutableListOf<Pair<Uri, Bitmap>>()
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
                val uri = ContentUris.withAppendedId(collection, id)
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    imageList.add(uri to bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return imageList
    }
}
sealed class HomeScreenState {
    object Idle : HomeScreenState()
    object Loading : HomeScreenState()
    data class Result(val userImages: List<Pair<Uri, Bitmap>>) : HomeScreenState()
    data class Error(val message: String) : HomeScreenState()
}
